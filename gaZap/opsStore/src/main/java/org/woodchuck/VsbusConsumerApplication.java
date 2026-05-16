package org.woodchuck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VsbusConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VsbusConsumerApplication.class, args);
		try {
			Thread.currentThread().join();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}

