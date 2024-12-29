package com.example.LB_6;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAsync
public class Lb6Application {

	public static void main(String[] args) {
		SpringApplication.run(Lb6Application.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

			executorService.submit(() -> retryTask(executorService));

			executorService.schedule(() -> System.out.println("15 секунд від запуску програми!"), 15, TimeUnit.SECONDS);
		};
	}
	@Async
    protected void retryTask(ScheduledExecutorService executorService) {
		int[] attempts = {0};
		executorService.scheduleAtFixedRate(() -> {
			attempts[0]++;
			try {
				System.out.println("Спроба виконання задачі, спроба #" + attempts[0]);
				executeTask();
				System.out.println("Задачу виконано успішно!");
				executorService.shutdown();
			} catch (Exception e) {
				System.out.println("Помилка виконання задачі: " + e.getMessage());
				if (attempts[0] >= 3) {
					System.out.println("Задача не виконана після 3 спроб. Завершення програми.");
					executorService.shutdown();
					System.exit(1);
				}
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

	private void executeTask() throws Exception {
		if (Math.random() > 0.5) {
			throw new Exception("Задача зазнала невдачі");
		}
	}
}
