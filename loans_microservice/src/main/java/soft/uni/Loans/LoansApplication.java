package soft.uni.Loans;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing
@OpenAPIDefinition(
		info = @Info(
				title = "Loans microservice REST API Documentation",
				description = "Clever Bank Loans microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Venelin Davidov",
						email = "v.davidov@gmail.com",
						url = "https://www.davidov.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.davidov.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Clever Bank Loans microservice REST API Documentation",
				url = "https://www.davidov.com/swagger-ui.html"
		)
)
public class LoansApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoansApplication.class, args);
	}

}
