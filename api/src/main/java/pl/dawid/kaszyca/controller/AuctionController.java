package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.AuctionBaseDTO;
import pl.dawid.kaszyca.dto.AuctionDTO;
import pl.dawid.kaszyca.dto.AuctionWithDetailsDTO;
import pl.dawid.kaszyca.service.AuctionService;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.AuctionVM;
import pl.dawid.kaszyca.vm.FilterVM;

import java.time.Instant;
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
    public ResponseEntity getAuction(@RequestParam String criteria) {
        try {
            FilterVM filterVM = MapperUtils.mapJsonToObject(criteria, FilterVM.class);
            AuctionVM auction = auctionService.getAuctionsFilter(filterVM);
            return auction != null ?
                    new ResponseEntity(auction, HttpStatus.OK) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("Something went wrong during getting  auctions");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/auctions/top")
    public ResponseEntity getTopAuctions(@RequestParam String category) {
        try {
            List<AuctionBaseDTO> auction = auctionService.getTopAuction(category);
            return auction.isEmpty() ?
                    new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(auction, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  auctions");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PostMapping("/auctions")
    public ResponseEntity saveAuction(@RequestBody AuctionDTO newAuctionVM) {
        try {
            Long auctionId = auctionService.saveAuction(newAuctionVM);
            return new ResponseEntity(auctionId, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during saving new auction object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PutMapping("/auctions")
    public ResponseEntity updateAuction(@RequestBody AuctionDTO newAuctionVM) {
        try {
            Long auctionId = auctionService.updateAuction(newAuctionVM);
            return new ResponseEntity(auctionId, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during edit Auction Object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PutMapping("/auctions/phone/{id}")
    public ResponseEntity incrementPhoneClick(@PathVariable long id) {
        try {
            auctionService.incrementPhoneClick(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during edit Auction Object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }


    @PutMapping("/auctions/{id}")
    public ResponseEntity extendAuctionEndTime(@PathVariable long id) {
        try {
            Instant expiredDate = auctionService.extendAuctionEndTimeById(id);
            return new ResponseEntity(expiredDate, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during edit Auction Object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @DeleteMapping("/auctions")
    public ResponseEntity removeAuctions(@RequestParam List<Integer> ids) {
        try {
            auctionService.removeAuctionsById(ids);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during removing object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }


    @GetMapping("/auctions/{id}")
    public ResponseEntity getAuctionById(@PathVariable long id) {
        try {
            AuctionWithDetailsDTO auction = auctionService.getAuctionWithDetailsObjectById(id);
            return auction != null ?
                    new ResponseEntity(auction, HttpStatus.OK) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("Something went wrong during getting auction by id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/auctions/edit/{id}")
    public ResponseEntity getAuctionToEditById(@PathVariable long id) {
        try {
            AuctionWithDetailsDTO auction = auctionService.getAuctionWithDetailsObjectById(id);
            if (auction != null) {
                auctionService.checkPermissionToEdit(auction.getUserId());
                return new ResponseEntity(auction, HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("Something went wrong during getting auction by id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/auctions/form")
    public ResponseEntity getNewAuctionData() {
        try {
            Map<String, List<String>> auctionData = auctionService.getAuctionData();
            return !auctionData.isEmpty() ?
                    new ResponseEntity(auctionData, HttpStatus.OK) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("Something went wrong during getting auctionsDetail");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PostMapping("/auction/favorites")
    public ResponseEntity addAuctionToFavorites(@RequestBody String id) {
        try {
            auctionService.addAuctionToFavorites(Long.valueOf(id));
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during add favorites auction");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/auction/favorites")
    public ResponseEntity getFavoriteAuctions(@RequestParam Integer page, Integer pageSize) {
        try {
            AuctionVM auctions = auctionService.getFavoritesAuction(page, pageSize);
            return auctions != null ?
                    new ResponseEntity(auctions, HttpStatus.OK) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("Something went wrong during add favorites auction");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}
