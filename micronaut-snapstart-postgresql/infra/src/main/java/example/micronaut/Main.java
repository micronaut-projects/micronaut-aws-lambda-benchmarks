package example.micronaut;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class Main {
    public static final String APP_NAME = "mnsnappostgresqldemo";

    public static void main(final String[] args) {
        App app = new App();

        Environment environment = Environment.builder()
                .build();

        DatabaseStack dbStack = new DatabaseStack(app, APP_NAME + "-DatabaseStack", StackProps.builder()
                .env(environment)
                .build());

        new AppStack(app, "MicronautSnapstartPostgresqlStack", AppStackProps.builder()
                .env(environment)
                .rdsProxyEndpoint(dbStack.getRdsProxyEndpoint())
                .rdsSecretArn(dbStack.getRdsSecretArn())
                .lambdaSG(dbStack.getLambdaSecurityGroup())
                .vpc(dbStack.getVpc())
                .build());
        app.synth();
    }
}