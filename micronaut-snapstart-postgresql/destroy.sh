#!/bin/bash
EXIT_STATUS=0
cd infra
cdk destroy --require-approval never --all
cd ..
exit $EXIT_STATUS
