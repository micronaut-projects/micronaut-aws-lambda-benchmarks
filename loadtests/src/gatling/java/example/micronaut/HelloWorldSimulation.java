package example.micronaut;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class HelloWorldSimulation extends Simulation {
    private static final Integer DURATION = 30;
    private static final Integer USERS = 50;

    private static final String API_URI = "API_URL";
    private static final String TEST_SUITE = "TEST_SUITE";
    {
        String apiUrl = System.getenv(API_URI);
        if (apiUrl == null || apiUrl.isBlank()) {
            System.out.println("Environment variable " + API_URI + " does not exist");
        } else {
            HttpProtocolBuilder httpProtocol = http.baseUrl(apiUrl);
            String name = System.getenv(TEST_SUITE) != null ? System.getenv(TEST_SUITE) : "Simple";
            ScenarioBuilder scn = scenario(name)
                    .exec(http("FetchHelloWorld")
                            .get("/")
                            .asJson()
                            .check(
                                    status().is(200),
                                    bodyString().is("{\"message\":\"Hello World\"}")
                            )
                    );
            setUp(scn.injectClosed(
                            constantConcurrentUsers(USERS).during(DURATION),
                            rampConcurrentUsers(USERS).to(USERS * 2).during(DURATION)
                            )
                    .protocols(httpProtocol)
            );
        }
    }
}
