package com.henriquefidelis.screen_match;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.henriquefidelis.screen_match.models.DadosEpisodio;
import com.henriquefidelis.screen_match.models.DadosSerie;
import com.henriquefidelis.screen_match.service.ConsumoAPI;
import com.henriquefidelis.screen_match.service.ConverteDados;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoAPI consumoAPI = new ConsumoAPI();
		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=everybody+hates+chris&apikey=5282043b");
		System.out.println(json);

		ConverteDados conversao = new ConverteDados();
		DadosSerie dados = conversao.obterDados(json, DadosSerie.class);
		System.out.println(dados);

		json = consumoAPI.obterDados("https://www.omdbapi.com/?t=everybody+hates+chris&season=3&episode=2&apikey=5282043b");
		DadosEpisodio dadosEpisodio = conversao.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);
	}

}
