package com.henriquefidelis.screen_match.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.henriquefidelis.screen_match.models.DadosSerie;
import com.henriquefidelis.screen_match.models.DadosTemporada;
import com.henriquefidelis.screen_match.service.ConsumoAPI;
import com.henriquefidelis.screen_match.service.ConverteDados;

public class Principal {

    private Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5282043b";

    public void exibirMenu() {
        System.out.println("==============================================");
        System.out.print("Digite o nome de uma série: ");
        var nomeSerie = sc.nextLine();

		var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);
        
		List<DadosTemporada> temporadas = new ArrayList<>();

		for(int i = 1; i <= dados.totalDeTemporadas(); i++) {
			json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);

//        for(int i = 0; i < dados.totalDeTemporadas(); i++) {
//            List<DadosEpisodio> episodios = temporadas.get(i).episodios();
//            for(int j = 0; j < episodios.size(); j++) {
//                System.out.println(episodios.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
    
}
