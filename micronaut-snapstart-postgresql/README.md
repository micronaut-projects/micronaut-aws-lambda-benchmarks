# Micronaut AWS Lambda + SnapStart + RDS Proxy + RDS Cluster + Aura PostgreSQL + Secrets Manager + API Gateway

This repository contains a demo of a function developed with [Micronaut Framework](https://micronaut.io) and deployed to [AWS Lambda](https://aws.amazon.com/pm/lambda/).

It connects to a [PostgreSQL](https://www.postgresql.org) database via an [RDS Proxy](https://aws.amazon.com/rds/proxy/). The proxy connects to a Cluster with two read and writer instances.

The Lambda function fetches the credentials to connect to the database from [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/).

The function uses [Liquibase](http://liquibase.org) to manage the database schema. The database schema is created and updated when a dedicated Lambda function [Snapstart](https://docs.aws.amazon.com/lambda/latest/dg/snapstart.html) snapshot is created.

The infrastructure is created, and the functions are deployed with [AWS CDK](https://aws.amazon.com/cdk/).

## Architecture

The architecture is as follows:

![Architecture](architecture.png)

The following image shows a trace:

![Trace](trace.png)

## Requisites
- [AWS Account](https://aws.amazon.com/free/)
- [CDK CLI](https://docs.aws.amazon.com/cdk/v2/guide/cli.html)
- [AWS CLI](https://aws.amazon.com/cli/)

The bash scripts `./deploy.sh` and `./destroy.sh` ease the deployment and deletion of the Cloud formation stacks.
