#!/usr/bin/env bash

set -e
PORT=8080

cd mn4
./gradlew shadowJar
cd ..

cd mn3
./gradlew shadowJar
cd ..

cd runtime
./gradlew build
java -jar build/libs/runtime-0.1-all.jar -o /dev/null &
RUNTIME_PID=$!

sleep 1.5
echo "Awake after 500 milliseconds of sleep!"

cd ..

export AWS_LAMBDA_RUNTIME_API=localhost:${PORT}
echo "AWS_LAMBDA_RUNTIME_API: $AWS_LAMBDA_RUNTIME_API"
max_retries=10
retry_interval=5
url="http://localhost:${PORT}/health"
for ((i=1; i<=$max_retries; i++)); do
    echo "Attempt $i: Checking health..."
    response=$(curl -o /dev/null -w "%{http_code}" $url)
    if [ "$response" -eq 200 ]; then
        echo "Health check succeeded (HTTP 200). Exiting loop."
        break
    else
        echo "Health check failed (HTTP $response). Retrying in $retry_interval seconds..."
        sleep $retry_interval
    fi
done

if [ "$response" -ne 200 ]; then
    echo "Max retries reached. Health check did not succeed."
    exit 1
fi

echo "runtime started with PID: $RUNTIME_PID"

echo "running Micronaut 4 Handler"
cd mn4
start_time=$(gdate +%s%N)

java -jar build/libs/handler-0.1-all.jar -o /dev/null &
HANDLER_PID=$!
echo "hanlder started with PID: $HANDLER_PID"

while true; do
  response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/response/123456")
  if [ "$response" -eq 200 ]; then
    echo "HTTP 200 response received. Exiting loop."
    break
  fi
  sleep 0.001;
done
end_time=$(gdate +%s%N)
elapsed_time=$(( (end_time - start_time) / 1000000 ))

echo "Micronaut 4 Total execution time: $elapsed_time milliseconds"
kill -9 $HANDLER_PID
cd ..

response=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "http://localhost:${PORT}/response")
if [ "$response" -eq 202 ]; then
    echo "HTTP 202 response received. Responses cleared."
fi
echo "running Micronaut 3 Handler"
cd mn3
start_time=$(gdate +%s%N)
java -jar build/libs/handler-0.1-all.jar -o /dev/null &
HANDLER_PID=$!
echo "handler started with PID: $HANDLER_PID"
while true; do
  response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/response/123456")
  if [ "$response" -eq 200 ]; then
    echo "HTTP 200 response received. Exiting loop."
    break
  fi
  sleep 0.001;
done
end_time=$(gdate +%s%N)
elapsed_time=$(( (end_time - start_time) / 1000000 ))
echo "Micronaut 3 Total execution time: $elapsed_time milliseconds"
kill -9 $HANDLER_PID
cd ..


kill -9 $RUNTIME_PID

exit 0
