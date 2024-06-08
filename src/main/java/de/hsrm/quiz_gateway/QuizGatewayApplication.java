package de.hsrm.quiz_gateway;

import de.hsrm.quiz_gateway.model.User;
import de.hsrm.quiz_gateway.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuizGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizGatewayApplication.class, args);
	}
}
