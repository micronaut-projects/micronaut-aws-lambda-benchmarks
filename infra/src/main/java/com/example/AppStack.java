package com.example;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.aws.cdk.function.MicronautFunctionFile;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.options.BuildTool;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.IConstruct;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;
import java.util.HashMap;
import java.util.Map;

public class AppStack extends Stack {

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        createFunctionAndApi( "controller-java-jacksondatabind", false, false);
        createFunctionAndApi( "controller-java-snapstart-jacksondatabind", false, true);
    }

    void createFunctionAndApi(String functionId, boolean optimized, boolean snapstart) {
        Map<String, String> environmentVariables = new HashMap<>();
        Function function = MicronautFunction.create(ApplicationType.DEFAULT,
                false,
                this,
                        functionId)
                .runtime(Runtime.JAVA_17)
                .handler("io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath(functionId, optimized)))
                .timeout(Duration.seconds(10))
                .memorySize(2048)
                .logRetention(RetentionDays.ONE_WEEK)
                .tracing(Tracing.DISABLED)
                .architecture(Architecture.X86_64)
                .build();
        if (snapstart) {
            enableSnapStart(function);
        }

        String apiId = functionId + "-api";
        LambdaRestApi api = LambdaRestApi.Builder.create(this, apiId)
                .handler(snapstart ? aliasToCurrentVersion(function) : function)
                .build();
        String apiUrl = functionId + "-url";
        CfnOutput.Builder.create(this, apiUrl)
                .exportName(apiUrl)
                .value(api.getUrl())
                .build();
    }

    IFunction aliasToCurrentVersion(Function function) {
        Version currentVersion = function.getCurrentVersion();
        return Alias.Builder.create(this, "ProdAlias")
                .aliasName("Prod")
                .version(currentVersion)
                .build();
    }

    void enableSnapStart(Function function) {
        IConstruct defaultChild = function.getNode().getDefaultChild();
        if (defaultChild instanceof CfnFunction) {
            CfnFunction cfnFunction = (CfnFunction) defaultChild;
            cfnFunction.setSnapStart(CfnFunction.SnapStartProperty.builder()
                    .applyOn("PublishedVersions")
                    .build());
        }
    }

    public static String functionPath(String folder, boolean optimized) {
        return "../" + folder + "/build/libs/" + functionFilename(folder, optimized);
    }

    public static String functionFilename(String folder, boolean optimized) {
        MicronautFunctionFile.Builder builder = MicronautFunctionFile.builder();
        if (optimized) {
            builder = builder.optimized();
        }
        return builder
            .graalVMNative(false)
            .version("0.1")
            .archiveBaseName(folder)
            .buildTool(BuildTool.GRADLE)
            .build();

    }
}