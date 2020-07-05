package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.config.StatisticKeyEnum;
import pl.dawid.kaszyca.dto.StatisticDTO;
import pl.dawid.kaszyca.model.Statistic;
import pl.dawid.kaszyca.service.StatisticService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api")
public class StatisticController {

    StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/statistics/auctions/{id}")
    public ResponseEntity getAuctionStatisticsById(@PathVariable Long id) {
        try {
            Map<StatisticKeyEnum, List<StatisticDTO>> auctionStatistic = statisticService.getAuctionStatisticsById(id);
            return new ResponseEntity(auctionStatistic, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Unable to find statistics for the given auction ID");
            return new ResponseEntity(HttpStatus.valueOf(422));
        }
    }
}
