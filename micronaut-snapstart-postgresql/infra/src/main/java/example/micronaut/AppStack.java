package example.micronaut;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.aws.cdk.function.MicronautFunctionFile;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.options.BuildTool;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.stepfunctions.TaskInput;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvocationType;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static software.amazon.awscdk.services.iam.Effect.ALLOW;

public class AppStack extends Stack {

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final AppStackProps props) {
        super(parent, id, props);

        Map<String, String> environmentVariables = props == null ? Collections.emptyMap() : Map.of(
                "RDS_PROXY_ENDPOINT", props.getRdsProxyEndpoint()
        );


        String module = props != null ? props.getModule() : "app";
        Role lambdaRole = props != null ?  createLambdaRole(module, this, props.getRdsSecretArn())  : null;
        Alias alias = createFunction(lambdaRole,  module, "micronaut-" + module + "-function", environmentVariables, props);
        if (module != null && module.equals("app")) {
            LambdaRestApi api = LambdaRestApi.Builder.create(this, "micronaut-function-api")
                    .handler(alias)
                    .build();
            CfnOutput.Builder.create(this, "ApiUrl")
                .exportName("ApiUrl")
                .value(api.getUrl())
                .build();
        }
    }

    Alias createFunction(Role lambdaRole, String module, String id, Map<String, String> environmentVariables, AppStackProps props) {
        Function.Builder functionBuilder = MicronautFunction.create(ApplicationType.FUNCTION,
                        false,
                        this,
                        id)
                .runtime(Runtime.JAVA_21)
                .handler("example.micronaut.FunctionRequestHandler")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath(module)))
                .timeout(Duration.seconds(20))
                .memorySize(2048)
                .logRetention(RetentionDays.ONE_WEEK)
                .tracing(Tracing.ACTIVE)
                .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .architecture(Architecture.X86_64);
        if (lambdaRole != null) {
            functionBuilder = functionBuilder.role(lambdaRole);
        }
        if (props != null) {
            functionBuilder.securityGroups(List.of(props.getLambdaSG()))
                    .vpc(props.getVpc())
                    .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build());
        }
        Function function = functionBuilder.build();

        Version currentVersion = function.getCurrentVersion();
        return Alias.Builder.create(this, id+ "ProdAlias")
                .aliasName("Prod")
                .version(currentVersion)
                .build();
    }

    public static String functionPath(String module) {
        return "../" +  module + "/build/libs/" + functionFilename(module);
    }

    public static String functionFilename(String module) {
        return MicronautFunctionFile.builder()
            .graalVMNative(false)
            .version("0.1")
            .archiveBaseName(module)
            .buildTool(BuildTool.GRADLE)
            .build();
    }

    private Role createLambdaRole(String module, AppStack stack, String rdsSecretArn) {

        Role lambdaRole = new Role(stack, module + "LambdaRole", RoleProps.builder()
                .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                .roleName(PhysicalName.GENERATE_IF_NEEDED)
                .build());
        lambdaRole.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"));
        lambdaRole.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaVPCAccessExecutionRole"));
        lambdaRole.addToPolicy(new PolicyStatement(PolicyStatementProps.builder()
                .effect(ALLOW)
                .actions(List.of("rds-db:connect"))
                .resources(List.of("arn:aws:rds-db:"
                                + Stack.of(stack).getRegion()
                                + ":"
                                + Stack.of(stack).getAccount()
                                + ":dbuser:*"
                                + "/" + DBConstants.DB_USER_NAME
                        )
                )
                .build()));
        lambdaRole.addToPolicy(new PolicyStatement(PolicyStatementProps.builder()
                .effect(ALLOW)
                .actions(List.of("secretsmanager:ListSecrets"))
                .resources(List.of("*"))
                .build()));

        if (rdsSecretArn != null) {
            lambdaRole.addToPolicy(new PolicyStatement(PolicyStatementProps.builder()
                    .effect(ALLOW)
                    .actions(List.of("secretsmanager:*"))
                    .resources(List.of(rdsSecretArn))
                    .build()));
        }
        return lambdaRole;
    }
}