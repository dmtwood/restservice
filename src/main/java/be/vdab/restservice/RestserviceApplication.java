package be.vdab.restservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestserviceApplication.class, args);
    }

    // extra method to change title of open api docs page
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info( new Info()
                        .title("Filialen")
                        .description("Toegang tot onze filialen.")
                );
    }
}
