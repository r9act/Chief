package ru.resteam.glava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.resteam")
public class GlavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlavaApplication.class, args);
	}
}
