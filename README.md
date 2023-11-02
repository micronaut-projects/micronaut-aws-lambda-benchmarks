# Micronaut AWS Lambda Benchmarks

## Load Tests

- The application uses [Gatling](https://gatling.io/) to load test the application. Module `loadtests` contains the Gatling load tests code.

The load test executes a simulation which runs a GET, DELETE scenario with 30 concurrent users for 30 seconds and then ramps up to 60 concurrent users for extra 30 seconds.

To change the load tests, edit `HelloWorldSimulation.java`

### CloudWatch Logs Insights

Using [CloudWatch Logs Insights](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/AnalyzingLogData.html), you can analyse the latency of the requests made to the Lambda functions.

### Max cold start with Cloud Watch Log Insights

If you are not using SnapStart run:

```
filter @type="REPORT"
| fields greatest(@initDuration, 0) + @duration as duration
| max(duration) as max
```

with SnapStart use:

```
filter @message like "REPORT"
| filter @message not like "RESTORE_REPORT"
| parse @message /Restore Duration: (?<@restore_duration_ms>[0-9\.]+)/
| parse @message / Duration: (?<@invoke_duration_ms>[0-9\.]+)/
| fields
greatest(@restore_duration_ms, 0) as restore_duration_ms,
greatest(@invoke_duration_ms, 0) as invoke_duration_ms
| fields
restore_duration_ms + invoke_duration_ms as total_invoke_ms
| stat
max(total_invoke_ms) as max
```

### Separate Cold Starts without SnapStart

The query separates cold starts from other requests and then gives you p50, p90 and p99 percentiles.

```
filter @type="REPORT"
| fields greatest(@initDuration, 0) + @duration as duration, ispresent(@initDuration) as coldStart
| stats count(*) as count, pct(duration, 50) as p50, pct(duration, 90) as p90, pct(duration, 99) as p99, max(duration) as max by coldStart
```

### Percentiles with SnapStart

if you are using SnapStart you can use:

```
filter @message like "REPORT"
| filter @message not like "RESTORE_REPORT"
| parse @message /Restore Duration: (?<@restore_duration_ms>[0-9\.]+)/
| parse @message / Duration: (?<@invoke_duration_ms>[0-9\.]+)/
| fields
greatest(@restore_duration_ms, 0) as restore_duration_ms,
greatest(@invoke_duration_ms, 0) as invoke_duration_ms
| fields
restore_duration_ms + invoke_duration_ms as total_invoke_ms
| stat
pct(total_invoke_ms, 50) as total_invoke_ms_p50,
pct(total_invoke_ms, 99) as total_invoke_ms_p99,
pct(total_invoke_ms, 99.9) as total_invoke_ms_p99.9,
max(total_invoke_ms) as max
```

## Scenarios

### Application with Controller in Java 17 runtime

`./test-controller-java-jacksondatabind.sh`