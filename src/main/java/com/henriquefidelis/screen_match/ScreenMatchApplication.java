package com.henriquefidelis.screen_match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.henriquefidelis.screen_match.main.Principal;
import com.henriquefidelis.screen_match.repository.SerieRepository;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	@Autowired
    private SerieRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository);
		principal.exibirMenu();
	}

}
