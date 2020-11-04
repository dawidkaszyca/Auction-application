package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.ReportAuctionDTO;
import pl.dawid.kaszyca.service.ReportService;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api")
public class ReportController {

    ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/report/auctions")
    public ResponseEntity saveNewReport(@RequestHeader("Language") String language,
                                        @RequestBody ReportAuctionDTO reportAuctionDTO) {
        try {
            reportService.saveNewReport(reportAuctionDTO, language);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during saving new report auction");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/report/auctions")
    public ResponseEntity getReports(@RequestParam String activeOnly) {
        try {
            List<ReportAuctionDTO> reports = reportService.getReported(Boolean.valueOf(activeOnly));
            return !reports.isEmpty() ?
                    new ResponseEntity(reports, HttpStatus.CREATED) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("Something went wrong during saving new report auction");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PutMapping("/report/auctions")
    public ResponseEntity updateReport(ReportAuctionDTO reportAuctionDTO) {
        try {
            reportService.updateReport(reportAuctionDTO);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during saving new report auction");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}
