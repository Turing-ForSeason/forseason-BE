package com.turing.forseason;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ForseasonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForseasonApplication.class, args);
	}

}
