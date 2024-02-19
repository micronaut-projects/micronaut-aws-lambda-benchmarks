package example.micronaut;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.aws.cdk.function.MicronautFunctionFile;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.options.BuildTool;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.SnapStartConf;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Alias;
import software.amazon.awscdk.services.lambda.Version;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
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


        Role lambdaRole = createLambdaRole(this, props != null ? props.getRdsSecretArn() : null);

        Function.Builder functionBuilder = MicronautFunction.create(ApplicationType.FUNCTION,
                false,
                this,
                "micronaut-function")
                .runtime(Runtime.JAVA_21)
                .role(lambdaRole)
                .runtime(Runtime.PROVIDED_AL2023)
                .handler("example.micronaut.FunctionRequestHandler")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath()))
                .timeout(Duration.seconds(20))
                .memorySize(2048)
                .logRetention(RetentionDays.ONE_WEEK)
                .tracing(Tracing.ACTIVE)
                //.snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .architecture(Architecture.X86_64);
        if (props != null) {
            functionBuilder.securityGroups(List.of(props.getLambdaSG()))
                    .vpc(props.getVpc())
                    .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build());
        }
        Function function = functionBuilder.build();

        Version currentVersion = function.getCurrentVersion();
//        Alias prodAlias = Alias.Builder.create(this, "ProdAlias")
//                .aliasName("Prod")
//                .version(currentVersion)
//                .build();
        LambdaRestApi api = LambdaRestApi.Builder.create(this, "micronaut-function-api")
                //.handler(prodAlias)
                .handler(function)
                .build();
        CfnOutput.Builder.create(this, "MnTestApiUrl")
                .exportName("MnTestApiUrl")
                .value(api.getUrl())
                .build();
    }

    public static String functionPath() {
        return "../app/build/libs/" + functionFilename();
    }

    public static String functionFilename() {
        return MicronautFunctionFile.builder()
            .graalVMNative(false)
            .version("0.1")
            .archiveBaseName("app")
            .buildTool(BuildTool.GRADLE)
            .build();
    }

    private Role createLambdaRole(AppStack stack, String rdsSecretArn) {

        Role lambdaRole = new Role(stack, "LambdaRole", RoleProps.builder()
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