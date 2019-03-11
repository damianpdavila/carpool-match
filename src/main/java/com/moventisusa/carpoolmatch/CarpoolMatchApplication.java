package com.moventisusa.carpoolmatch;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@SpringBootApplication
@EnableAsync
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
	@Bean(name = "threadPoolTaskExecutor")
	public TaskExecutor getAsyncExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(5);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Async-");

		return executor;

	}

}

