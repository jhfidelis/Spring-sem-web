package com.henriquefidelis.screen_match.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.henriquefidelis.screen_match.models.DadosEpisodio;
import com.henriquefidelis.screen_match.models.DadosSerie;
import com.henriquefidelis.screen_match.models.DadosTemporada;
import com.henriquefidelis.screen_match.models.Episodio;
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

        for (int i = 1; i <= dados.totalDeTemporadas(); i++) {
            json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // List<DadosEpisodio> dadosEpisodios = temporadas.stream()
        //         .flatMap(t -> t.episodios().stream())
        //         .collect(Collectors.toList());

        // System.out.println("\n===== 5 MELHORES EPISÓDIOS =====");
        // dadosEpisodios.stream()
        //         .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
        //         .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
        //         .limit(5)
        //         .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        System.out.println("");
        episodios.forEach(System.out::println);

        System.out.print("\nDigite o nome de um episódio dessa série: ");
        var tituloEpisodio = sc.nextLine();

        Optional<Episodio> epBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(tituloEpisodio.toUpperCase()))
                .findFirst();

        if (epBuscado.isPresent()) {
            System.out.println("Episódio encontrado!");
            System.out.println("Nome do episódio: " + epBuscado.get().getTitulo());
            System.out.println("Episódio número: " + epBuscado.get().getNumeroEpisodio() + 
                    " - Temporada: " + epBuscado.get().getTemporada());
        } else {
            System.out.println("Episódio não encontrado!");
        }

        // System.out.print("\nA partir de que ano você deseja visualizar os episódios? ");
        // var ano = sc.nextInt();
        // sc.nextLine();

        // LocalDate dataPesquisada = LocalDate.of(ano, 1, 1);

        // DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // episodios.stream()
        //         .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento()
        //                 .isAfter(dataPesquisada))
        //         .forEach(e -> System.out.println(
        //                 "Temporada: " + e.getTemporada() +
        //                         " | Episódio: " + e.getTitulo() +
        //                         " | Data de lançamento: " + e.getDataDeLancamento().format(formatador)));
    }

}
