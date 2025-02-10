package com.mvilaboa.hogwarts_artifacts_online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mvilaboa.hogwarts_artifacts_online.artifact.utils.IdWorker;

@SpringBootApplication
public class HogwartsArtifactsOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HogwartsArtifactsOnlineApplication.class, args);
	}

	@Bean
	IdWorker idWorker() {
		return new IdWorker(1, 1);
	}

}
