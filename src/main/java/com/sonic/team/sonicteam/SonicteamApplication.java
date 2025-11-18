package com.sonic.team.sonicteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SonicteamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SonicteamApplication.class, args);
	}

}
