package com.henriquefidelis.screen_match.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henriquefidelis.screen_match.dto.SerieDTO;
import com.henriquefidelis.screen_match.service.SerieService;

@RestController
public class SerieController {

    @Autowired
    private SerieService service;

    @GetMapping("/series")
    public List<SerieDTO> obterSeries() {
        return service.obterTodasAsSeries();
    }

}
