package example.micronaut;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import java.io.File;
import java.util.Collections;

class AppStackTest {

    @Test
    void testAppStack() {
        if (new File(AppStack.functionPath("app")).exists() && new File(AppStack.functionPath("dbmigration")).exists()) {
            AppStack stack = new AppStack(new App(), "TestMicronautSnapstartPostgresqlStack");
            Template template = Template.fromStack(stack);
            template.hasResourceProperties("AWS::Lambda::Function", Collections.singletonMap("Handler", "example.micronaut.FunctionRequestHandler"));
        }
    }
}
