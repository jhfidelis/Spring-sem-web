package com.henriquefidelis.screen_match.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.henriquefidelis.screen_match.models.Categoria;
import com.henriquefidelis.screen_match.models.DadosSerie;
import com.henriquefidelis.screen_match.models.DadosTemporada;
import com.henriquefidelis.screen_match.models.Episodio;
import com.henriquefidelis.screen_match.models.Serie;
import com.henriquefidelis.screen_match.repository.SerieRepository;
import com.henriquefidelis.screen_match.service.ConsumoAPI;
import com.henriquefidelis.screen_match.service.ConverteDados;

public class Principal {

    private Scanner sc = new Scanner(System.in, "UTF-8");
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=" + System.getenv("OMDB_APIKEY");

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBuscada;

    private SerieRepository repository;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibirMenu() {
        var opcao = -1;
        while (opcao != 0) {
            System.out.println("==============================================");
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Top 5 Séries
                    7 - Buscar séries por categoria
                    8 - Filtrar séries
                    9 - Buscar episódio por trecho
                    10 - Melhores episódios da série
                    11 - Buscar episódios a partir de uma data

                    0 - sair
                    """;

            System.out.print(menu + "\nEscolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    buscarSeriesWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosPorLancamento();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    public void buscarSeriesWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("\nDigite o nome de uma série: ");
        var nomeSerie = sc.nextLine();

        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.print("\nEscolha uma série pelo nome: ");
        var nomeSerie = sc.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
                var json = consumoAPI
                        .obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                .flatMap(d -> d.episodios().stream()
                        .map(e -> new Episodio(d.numero(), e)))
                .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }

    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.print("\nEscolha uma série pelo nome: ");
        var nomeSerie = sc.nextLine();

        serieBuscada = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()) {
            System.out.println("Dados da série" + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.print("Digite o nome do ator: ");
        var nomeAtor = sc.nextLine();
        System.out.print("Digite a nota mínima de avaliação: ");
        var avaliacao = sc.nextDouble();

        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        
        System.out.println("\n-> Séries que " + nomeAtor.toUpperCase() + " atuou:");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repository.findTop5ByOrderByAvaliacaoDesc();

        topSeries.forEach(s -> System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }

    public void buscarSeriesPorCategoria() {
        System.out.print("Digite a categoria que você deseja buscar: ");
        var nomeGenero = sc.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);

        System.out.println("Séries da categoria " + nomeGenero + ":");
        seriesPorCategoria.forEach(System.out::println);
    }

    public void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.print("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = sc.nextInt();
        sc.nextLine();

        System.out.print("Com avaliação a partir de que valor? ");
        var avaliacao = sc.nextDouble();
        sc.nextLine();

        List<Serie> filtroSeries = repository.buscarSeriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("\n===== Séries Filtradas =====");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }

    public void buscarEpisodioPorTrecho() {
        System.out.print("Qual o nome do episódio para busca? ");
        var trechoEpisodio = sc.nextLine();

        List<Episodio> episodiosEncontrados = repository.buscarEpisodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e -> 
                System.out.printf("Série: %s | Temporada: %s | Episódio %s = %s\n", 
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
    }

    public void buscarTopEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repository.buscarTopEpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> 
                    System.out.printf("Série: %s | Temporada: %s | Episódio %s = %s | Avaliação: %s\n", 
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(),
                            e.getTitulo(), e.getAvaliacao()));
        }
    }

    public void buscarEpisodiosPorLancamento() {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            System.out.print("Digite o ano limite de lançamento: ");
            var anoLancamento = sc.nextInt();
            sc.nextLine();

            List<Episodio> episodiosPorAno = repository.buscarEpisodiosPorDataDeLancamento(serie, anoLancamento);
            episodiosPorAno.forEach(System.out::println);
        }
    }

}
