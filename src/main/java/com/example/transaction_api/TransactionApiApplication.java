package com.example.transaction_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class TransactionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionApiApplication.class, args);
	}

}
