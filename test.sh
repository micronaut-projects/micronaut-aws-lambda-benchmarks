#!/bin/bash
EXIT_STATUS=0
QUERY="Stacks[0].Outputs[?OutputKey==\`$1\`].OutputValue"
./gradlew shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi
./gradlew test || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi
cd infra
cdk synth --quiet true
cdk deploy --require-approval never
cd ..
STACK_NAME=MnAwsLambdaBenchmark
CMD="aws cloudformation describe-stacks --stack-name $STACK_NAME --query '$QUERY' --output text"
API_URL="$(eval $CMD)"
export API_URL=$API_URL
RESPONSE="$(curl -s $API_URL)"
EXPECTED_RESPONSE='{"message":"Hello World"}'
if [ "$RESPONSE" != "$EXPECTED_RESPONSE" ]; then echo $RESPONSE && exit 1; fi
echo "success"
./gradlew :loadtests:gatlingRun
exit $EXIT_STATUS