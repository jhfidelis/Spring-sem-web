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
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                
                0 - sair
                """;
            
        System.out.print(menu + "\nEscolha uma opção: ");
        var opcao = sc.nextInt();
        sc.nextLine();

        switch (opcao) {
            case 1:
                buscarSeriesWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }

    public void buscarSeriesWeb() {
        DadosSerie dados = getDadosSerie();
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("Digite o nome de uma série: ");
        var nomeSerie = sc.nextLine();

        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        return dados;
    }

    private void buscarEpisodioPorSerie() {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalDeTemporadas(); i++) {
            var json = consumoAPI.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }

}
