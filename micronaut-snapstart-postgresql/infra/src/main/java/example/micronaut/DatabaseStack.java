package example.micronaut;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretProps;
import software.amazon.awscdk.services.secretsmanager.SecretStringGenerator;
import software.constructs.Construct;

import java.util.List;

import static example.micronaut.Main.APP_NAME;

public class DatabaseStack extends Stack {
  protected Vpc vpc;

  protected SecurityGroup lambdaSecurityGroup;

  protected String rdsProxyEndpoint;

  protected String rdsSecretArn;

  public DatabaseStack(final Construct scope, final String id, final StackProps props) {
    super(scope, id, props);

    this.vpc = new Vpc(this, "CustomVPC", VpcProps.builder()
      .maxAzs(2)
      .build());

    Secret rdsSecret = new Secret(this, "RDSSecret", SecretProps.builder()
      .secretName("/config/" +  APP_NAME + "/rds")
      .generateSecretString(SecretStringGenerator.builder()
        .excludePunctuation(true)
        .passwordLength(16)
        .generateStringKey("password")
        .secretStringTemplate("{\"username\": \"" + DBConstants.DB_USER_NAME + "\"}")
        .build())
      .build());

    this.setRdsSecretArn(rdsSecret.getSecretArn());

    SecurityGroup rdsSecurityGroup = new SecurityGroup(this, "RDSSecurityGroup", SecurityGroupProps.builder()
      .allowAllOutbound(true)
      .securityGroupName("MnGraalDemo-RDSSecurityGroup")
      .vpc(vpc)
      .build());

    DatabaseCluster rdsCluster = new DatabaseCluster(this, "RDSCluster", DatabaseClusterProps.builder()
      .engine(DatabaseClusterEngine.auroraPostgres(
        AuroraPostgresClusterEngineProps.builder()
          .version(AuroraPostgresEngineVersion.VER_15_3)
          .build()))
      .credentials(Credentials.fromSecret(rdsSecret))
      .defaultDatabaseName(DBConstants.DB_NAME)
      .writer(ClusterInstance.provisioned("writer", ProvisionedClusterInstanceProps.builder()
        .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MEDIUM))
        .allowMajorVersionUpgrade(true)
        .build()))
      .readers(List.of(
        ClusterInstance.provisioned("reader1", ProvisionedClusterInstanceProps.builder().promotionTier(1).build()),
        ClusterInstance.provisioned("reader2", ProvisionedClusterInstanceProps.builder().promotionTier(2).build())
      ))
      .vpcSubnets(SubnetSelection.builder()
        .subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
      .securityGroups(List.of(rdsSecurityGroup))
      .vpc(vpc)
      .build());

    Role proxyRole = new Role(this, "RDSProxyRole", RoleProps.builder()
      .roleName(APP_NAME + "-RDSProxyRole")
      .assumedBy(new ServicePrincipal("rds.amazonaws.com"))
      .build());

    DatabaseProxy proxy = new DatabaseProxy(this, "RDSProxy", DatabaseProxyProps.builder()
      .proxyTarget(ProxyTarget.fromCluster(rdsCluster))
      .securityGroups(List.of(rdsSecurityGroup))
      .secrets(List.of(rdsSecret))
      .role(proxyRole)
      .requireTls(true)
      .vpc(vpc)
      .build());

    //Self referencing group for RDS Proxy
    rdsSecurityGroup.addIngressRule(rdsSecurityGroup, Port.tcp(Integer.parseInt(DBConstants.DB_PORT)));

    this.lambdaSecurityGroup = new SecurityGroup(this, "LambdaSecurityGroup", SecurityGroupProps.builder()
      .allowAllOutbound(true)
      .securityGroupName(APP_NAME + "-LambdaSecurityGroup")
      .vpc(vpc)
      .build());

    //Access from lambda to RDS Proxy
    rdsSecurityGroup.addIngressRule(lambdaSecurityGroup, Port.tcp(Integer.parseInt(DBConstants.DB_PORT)));

    this.rdsProxyEndpoint = proxy.getEndpoint();
  }

  public Vpc getVpc() {
    return vpc;
  }

  public SecurityGroup getLambdaSecurityGroup() {
    return lambdaSecurityGroup;
  }

  public String getRdsProxyEndpoint() {
    return rdsProxyEndpoint;
  }

  public String getRdsSecretArn() {
    return rdsSecretArn;
  }

  public void setRdsSecretArn(String rdsSecretArn) {
    this.rdsSecretArn = rdsSecretArn;
  }
}
