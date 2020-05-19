package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.AuctionBaseDTO;
import pl.dawid.kaszyca.dto.AuctionDTO;
import pl.dawid.kaszyca.dto.AuctionWithDetailsDTO;
import pl.dawid.kaszyca.service.AuctionService;
import pl.dawid.kaszyca.vm.NewAuctionVM;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("api")
public class AuctionController {

    private AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/auctions")
    public ResponseEntity getAuction(@RequestParam String category, Integer pageSize, Integer page) {
        try {
            List<AuctionBaseDTO> auction = auctionService.getAuctionsByCategoryAndPage(category, page, pageSize);
            if(!auction.isEmpty())
                return new ResponseEntity(auction, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  auctions");
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/auctions")
    public ResponseEntity saveAuction(@RequestBody NewAuctionVM newAuctionVM) {
        try {
            Long auctionId = auctionService.saveAuction(newAuctionVM);
            return new ResponseEntity(auctionId, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during saving new Auction Object");
            return new ResponseEntity(HttpStatus.valueOf(422));
        }
    }

    @GetMapping("/auctions/{id}")
    public ResponseEntity getAuctionById(@PathVariable long id) {
        try {
            AuctionWithDetailsDTO auction = auctionService.getAuctionById(id);
            if( auction != null)
                return new ResponseEntity(auction, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  auction by id");
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auctions/form")
    public ResponseEntity getNewAuctionData() {
        try {
            Map<String, List<String>> auctionData = auctionService.getAuctionData();
            if(!auctionData.isEmpty())
                return new ResponseEntity(auctionData, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting auctionsDetail");
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
