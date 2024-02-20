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

        new AppStack(app, "MicronautSnapstartPostgresqlDbMigrationStack", builder(environment, dbStack)
                .module("dbmigration")
                .build());

        new AppStack(app, "MicronautSnapstartPostgresqlStack", builder(environment, dbStack)
                .module("app")
                .build());
        app.synth();
    }

    private static AppStackProps.Builder builder(Environment environment, DatabaseStack dbStack) {
        return AppStackProps.builder()
                .env(environment)
                .rdsProxyEndpoint(dbStack.getRdsProxyEndpoint())
                .rdsSecretArn(dbStack.getRdsSecretArn())
                .lambdaSG(dbStack.getLambdaSecurityGroup())
                .vpc(dbStack.getVpc());
    }
}