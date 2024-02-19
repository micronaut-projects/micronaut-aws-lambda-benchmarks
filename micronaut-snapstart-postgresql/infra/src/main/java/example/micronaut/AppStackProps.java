package example.micronaut;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.Vpc;


public class AppStackProps implements StackProps {
  private Vpc vpc;
  private SecurityGroup lambdaSG;
  private String rdsProxyEndpoint;
  private String rdsSecretArn;
  private Environment env;

    public Vpc getVpc() {
        return vpc;
    }

    public void setVpc(Vpc vpc) {
        this.vpc = vpc;
    }

    public SecurityGroup getLambdaSG() {
        return lambdaSG;
    }

    public void setLambdaSG(SecurityGroup lambdaSG) {
        this.lambdaSG = lambdaSG;
    }

    public String getRdsProxyEndpoint() {
        return rdsProxyEndpoint;
    }

    public void setRdsProxyEndpoint(String rdsProxyEndpoint) {
        this.rdsProxyEndpoint = rdsProxyEndpoint;
    }

    public String getRdsSecretArn() {
        return rdsSecretArn;
    }

    public void setRdsSecretArn(String rdsSecretArn) {
        this.rdsSecretArn = rdsSecretArn;
    }

    @Nullable
    @Override
    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private Vpc vpc;
        private SecurityGroup lambdaSG;
        private String rdsProxyEndpoint;
        private String rdsSecretArn;
        private Environment env;

        public Builder env(Environment environment) {
            this.env = environment;
            return this;
        }

        public Builder rdsProxyEndpoint(String rdsProxyEndpoint) {
            this.rdsProxyEndpoint = rdsProxyEndpoint;
            return this;
        }

        public Builder rdsSecretArn(String rdsSecretArn) {
            this.rdsSecretArn = rdsSecretArn;
            return this;
        }

        public  Builder lambdaSG(SecurityGroup lambdaSG) {
            this.lambdaSG = lambdaSG;
            return this;
        }

        public Builder vpc(Vpc vpc) {
            this.vpc = vpc;
            return this;
        }
         public AppStackProps build() {
             AppStackProps instance = new AppStackProps();
             instance.setEnv(env);
             instance.setVpc(vpc);
             instance.setRdsProxyEndpoint(rdsProxyEndpoint);
             instance.setRdsSecretArn(rdsSecretArn);
             instance.setLambdaSG(lambdaSG);
            return instance;
         }
    }
}
