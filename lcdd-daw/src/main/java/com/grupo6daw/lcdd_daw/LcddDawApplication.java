package com.grupo6daw.lcdd_daw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LcddDawApplication {

	public static void main(String[] args) {
		SpringApplication.run(LcddDawApplication.class, args);
	}

}
