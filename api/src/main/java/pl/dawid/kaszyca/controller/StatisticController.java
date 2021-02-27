package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.config.StatisticKeyEnum;
import pl.dawid.kaszyca.dto.StatisticDTO;
import pl.dawid.kaszyca.service.StatisticService;
import pl.dawid.kaszyca.util.MapperUtil;
import pl.dawid.kaszyca.vm.FilterVM;
import pl.dawid.kaszyca.vm.StatisticVM;

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
            return auctionStatistic.isEmpty() ?
                    new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(auctionStatistic, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Unable to find statistics for the given auction id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/statistics/admin")
    public ResponseEntity getAdminStatistic() {
        try {
            Map<StatisticKeyEnum, Long> auctionStatistic = statisticService.getAdminStatistic();
            return auctionStatistic.isEmpty() ?
                    new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(auctionStatistic, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Unable to find statistics for the given auction id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/statistics/admin/key")
    public ResponseEntity getStatisticByKey(@RequestParam String data) {
        try {
            StatisticVM statistic = MapperUtil.mapJsonToObject(data, StatisticVM.class);
            List<StatisticDTO> auctionStatistic = statisticService.getStatisticByObject(statistic);
            return auctionStatistic.isEmpty() ?
                    new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(auctionStatistic, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Unable to find statistics for the given auction id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }

    }
}
