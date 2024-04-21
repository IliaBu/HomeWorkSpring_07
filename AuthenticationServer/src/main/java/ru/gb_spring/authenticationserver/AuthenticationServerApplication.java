package ru.gb_spring.authenticationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Authentication server application.
 */
@SpringBootApplication
public class AuthenticationServerApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
		SpringApplication.run(AuthenticationServerApplication.class, args);
	}

}
