package com.moventisusa.carpoolmatch;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;


@SpringBootApplication
public class CarpoolMatchApplication {

	public static void main(String[] args) {
		/**
		 *  Start up changed to enable a second properties file to hold sensitive credentials (API, etc.)
		 *  Orig:  SpringApplication.run(CarpoolMatchApplication.class, args);
		 */
		ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(CarpoolMatchApplication.class)
				.properties("spring.config.name:application,config",
						"spring.config.location:classpath:/")
				.build().run(args);

	}

}

