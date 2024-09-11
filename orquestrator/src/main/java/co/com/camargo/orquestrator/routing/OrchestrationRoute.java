package co.com.camargo.orquestrator.routing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrchestrationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Manejo de excepciones global
        onException(Exception.class)
                .log("Error occurred: ${exception.message}")
                .setBody(simple("{\"error\": \"Service unavailable\"}"))
                .handled(true);

        // Ruta principal que orquesta los microservicios
        from("direct:orchestrateSteps")
                .log("Starting orchestration...")

                // Invocar el paso 1
                .to("direct:getStepOne")
                .log("Received Step 1: ${body}")
                .setProperty("step1", simple("${body}"))

                // Invocar el paso 2
                .to("direct:getStepTwo")
                .log("Received Step 2: ${body}")
                .setProperty("step2", simple("${body}"))

                // Invocar el paso 3
                .to("direct:getStepThree")
                .log("Received Step 3: ${body}")
                .setProperty("step3", simple("${body}"))

                // Consolidar la respuesta final
                .process(exchange -> {
                    String step1 = exchange.getProperty("step1", String.class);
                    String step2 = exchange.getProperty("step2", String.class);
                    String step3 = exchange.getProperty("step3", String.class);

                    // Construir la respuesta final
                    String finalAnswer = "Step1: " + step1 + " - Step2: " + step2 + " - Step3: " + step3;
                    exchange.getIn().setBody(finalAnswer);
                })
                .log("Final answer: ${body}");
    }
}
