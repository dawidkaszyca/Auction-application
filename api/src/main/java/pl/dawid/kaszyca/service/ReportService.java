package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.config.DecisionConfig;
import pl.dawid.kaszyca.dto.ReportAuctionDTO;
import pl.dawid.kaszyca.exception.AuctionNotExistException;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.model.auction.ReportedAuction;
import pl.dawid.kaszyca.repository.ReportAuctionRepository;
import pl.dawid.kaszyca.util.MapperUtil;
import pl.dawid.kaszyca.vm.MessageDispatchVM;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private ReportAuctionRepository reportAuctionRepository;
    private AuctionService auctionService;
    private StatisticService statisticService;
    private MailService mailService;
    private UserService userService;
    private ChatService chatService;

    public ReportService(ReportAuctionRepository reportAuctionRepository, AuctionService auctionService,
                         StatisticService statisticService, MailService mailService, UserService userService,
                         ChatService chatService) {
        this.reportAuctionRepository = reportAuctionRepository;
        this.auctionService = auctionService;
        this.statisticService = statisticService;
        this.mailService = mailService;
        this.userService = userService;
        this.chatService = chatService;
    }

    public void saveNewReport(ReportAuctionDTO reportAuctionDTO, String language) {
        ReportedAuction reportedAuction = MapperUtil.map(reportAuctionDTO, ReportedAuction.class);
        Auction auction = auctionService.getAuctionById(reportAuctionDTO.getAuctionId());
        if (auction != null) {
            reportedAuction.setAuction(auction);
            reportAuctionRepository.save(reportedAuction);
            statisticService.incrementDailyAuctionReports();
            mailService.sendReportedAuctionMail(reportAuctionDTO.getEmail(), auction.getTitle(), language);
        } else {
            throw new AuctionNotExistException();
        }
    }

    public List<ReportAuctionDTO> getReports() {
        List<ReportedAuction> reportedAuctions = reportAuctionRepository.findAllByActive(true);
        return MapperUtil.mapAll(reportedAuctions, ReportAuctionDTO.class);
    }

    public void updateReport(ReportAuctionDTO reportAuctionDTO, String language) {
        boolean decision = Boolean.parseBoolean(reportAuctionDTO.getDecision());
        Optional<ReportedAuction> reportedAuctionOptional = reportAuctionRepository.findFirstById(reportAuctionDTO.getId());
        Optional<User> optionalUser = userService.getCurrentUserObject();
        if (reportedAuctionOptional.isPresent() && optionalUser.isPresent()) {
            ReportedAuction reportedAuction = reportedAuctionOptional.get();
            if (decision) {
                reportedAuction.setDecision(DecisionConfig.APPROVE.name());
            } else {
                reportedAuction.setDecision(DecisionConfig.CANCEL.name());
            }
            User user = optionalUser.get();
            reportedAuction.setDecisivePerson(user);
            reportedAuction.setSubstantiation(reportAuctionDTO.getSubstantiation());
            reportedAuction.setActive(false);
            reportAuctionRepository.save(reportedAuction);
            if (decision) {
                sentChatNotificationToAuctionOwner(reportedAuction.getAuction(), reportedAuction.getDescription(), user);
                removeAuction(reportedAuction.getAuction());
            }
            sendEmailNotification(reportedAuction, language, decision);
        }
    }

    private void sendEmailNotification(ReportedAuction reportedAuction, String language, boolean decision) {
        String email = reportedAuction.getEmail();
        String decisionString = decision ? "zaakceptowane" : "odrzucone";
        String description = reportedAuction.getSubstantiation();
        String auctionTitle = reportedAuction.getAuction().getTitle();
        mailService.sendReportedAuctionDecisionEmail(email, auctionTitle, language, decisionString, description);
        if (decision) {
            email = reportedAuction.getAuction().getUser().getEmail();
            mailService.sendReportedAuctionDecisionNotificationEmail(email, auctionTitle, language, description);
        }
    }

    private void removeAuction(Auction auction) {
        Integer id = auction.getId().intValue();
        auctionService.removeAuctionsById(Collections.singletonList(id));
    }

    private void sentChatNotificationToAuctionOwner(Auction auction, @NotNull String decisivePerson, User user) {
        if (!user.getId().equals(auction.getUser().getId())) {
            MessageDispatchVM messageDispatchVM = new MessageDispatchVM();
            String content = "Twoja aukcja o tytule : '" + auction.getTitle() + "' zozstała usunięta. Decyzja osoby rozpatrującej: " + decisivePerson;
            Long userId = auction.getUser().getId();
            messageDispatchVM.setTo(userId);
            messageDispatchVM.setContent(content);
            chatService.sendMessage(messageDispatchVM);
        }
    }

}
