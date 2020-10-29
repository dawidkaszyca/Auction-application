package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.ReportAuctionDTO;
import pl.dawid.kaszyca.exception.AuctionNotExistException;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.model.auction.ReportedAuction;
import pl.dawid.kaszyca.repository.ReportAuctionRepository;
import pl.dawid.kaszyca.util.MapperUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private ReportAuctionRepository reportAuctionRepository;
    private AuctionService auctionService;

    public ReportService(ReportAuctionRepository reportAuctionRepository, AuctionService auctionService) {
        this.reportAuctionRepository = reportAuctionRepository;
        this.auctionService = auctionService;
    }

    public void saveNewReport(ReportAuctionDTO reportAuctionDTO) {
        ReportedAuction reportedAuction = MapperUtils.map(reportAuctionDTO, ReportedAuction.class);
        Auction auction = auctionService.getAuctionById(reportAuctionDTO.getAuctionId());
        if (auction != null) {
            reportedAuction.setAuction(auction);
            reportAuctionRepository.save(reportedAuction);
        } else {
            throw new AuctionNotExistException();
        }
    }

    public List<ReportAuctionDTO> getReported(Boolean active) {
        List<ReportedAuction> reportedAuctions = reportAuctionRepository.findAllByActive(active);
        return MapperUtils.mapAll(reportedAuctions, ReportAuctionDTO.class);
    }

    public void updateReport(ReportAuctionDTO reportAuctionDTO) {
        Optional<ReportedAuction> reportedAuctionOptional = reportAuctionRepository.findFirstById(reportAuctionDTO.getId());
        if (reportedAuctionOptional.isPresent()) {
            ReportedAuction reportedAuction = reportedAuctionOptional.get();
            reportedAuction.setDecision(reportAuctionDTO.getDecision());
            reportAuctionRepository.save(reportedAuction);
        }
    }
}
