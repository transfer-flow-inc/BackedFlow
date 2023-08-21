package fr.nil.backedflow;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Transfer Flow", description = "API for the Transfer Flow project", version = "v1"))

public class BackedFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackedFlowApplication.class, args);
	}

}
