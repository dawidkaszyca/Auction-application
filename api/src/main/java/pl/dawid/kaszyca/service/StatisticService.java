package pl.dawid.kaszyca.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.config.StatisticKeyEnum;
import pl.dawid.kaszyca.model.Statistic;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.repository.MessageRepository;
import pl.dawid.kaszyca.repository.StatisticRepository;
import pl.dawid.kaszyca.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class StatisticService {

    private Map<StatisticKeyEnum, Long> statistics;
    private Date statisticCollectDate;
    /**
     * Map<Long, Long>
     * key - auctionId
     * value - amount
     */
    private Map<StatisticKeyEnum, Map<Long, Long>> statisticsWithAuctionId;
    private StatisticRepository statisticRepository;

    @Autowired
    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    private AuctionService auctionService;
    private UserRepository userRepository;
    private MessageRepository messageRepository;

    StatisticService(StatisticRepository statisticRepository, UserRepository userRepository, MessageRepository messageRepository) {
        createNewEmptyMapWithKeys();
        createNewEmptyStatisticsWithIdMap();
        this.statisticRepository = statisticRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        statisticCollectDate = new Date();
    }

    private void createNewEmptyStatisticsWithIdMap() {
        statisticsWithAuctionId = new HashMap<>();
        statisticsWithAuctionId.put(StatisticKeyEnum.DAILY_AUCTION_VIEWS_BY_ID, new HashMap<>());
        statisticsWithAuctionId.put(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS_BY_ID, new HashMap<>());
    }

    public void incrementDailyAuctionViewsById(Long id) {
        incrementValueByKeyAndAuctionId(StatisticKeyEnum.DAILY_AUCTION_VIEWS_BY_ID, id);
    }

    public void incrementDailyAuctionPhoneClicksById(Long id) {
        incrementValueByKeyAndAuctionId(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS_BY_ID, id);
    }

    private void incrementValueByKeyAndAuctionId(StatisticKeyEnum dailyAuctionViews, Long id) {
        Map<Long, Long> statisticsByEnumKey = statisticsWithAuctionId.get(dailyAuctionViews);
        Long value = statisticsByEnumKey.get(id);
        if (value == null) {
            value = 0L;
        }
        value++;
        statisticsByEnumKey.put(id, value);
    }

    public void incrementDailyNewAuction() {
        incrementValueByKey(StatisticKeyEnum.DAILY_NEW_AUCTIONS);
    }

    public void incrementDailyEditedAuction() {
        incrementValueByKey(StatisticKeyEnum.DAILY_EDITED_AUCTIONS);
    }

    public void incrementDailyMessages() {
        incrementValueByKey(StatisticKeyEnum.DAILY_MESSAGES);
    }

    public void incrementDailyRegistration() {
        incrementValueByKey(StatisticKeyEnum.DAILY_REGISTRATIONS);
    }

    public void incrementDailyLogin() {
        incrementValueByKey(StatisticKeyEnum.DAILY_USERS_LOGIN);
    }

    public void incrementDailyAuctionReports() {
        incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_REPORTS);
    }

    public void incrementDailyAuctionViews() {
        incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_VIEWS);
    }

    public void incrementDailyAuctionPhoneClicksBy() {
        incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS);
    }

    public void incrementDailyRemovedAuction() {
        incrementValueByKey(StatisticKeyEnum.DAILY_REMOVED_AUCTIONS);
    }

    private void incrementValueByKey(StatisticKeyEnum statisticKeyEnum) {
        statistics.put(statisticKeyEnum, statistics.get(statisticKeyEnum) + 1);
    }


    /**
     * update information about daily new, edited, views, phone clicks auctions, messages, registrations, login, reports
     * <p>
     * This is scheduled to get fired every hour
     */
    //TODO Active reports
    @Scheduled(cron = "0 0 * * * *")
    public void updateDailyStatistics() {
        log.info("start cron to daily statistics");
        saveStatistics();
        saveStatisticsWithAuctionId();
        if (!isNewDay()) {
            statisticCollectDate = new Date();
            createNewEmptyMapWithKeys();
            createNewEmptyStatisticsWithIdMap();
        }
        log.info("end cron to daily statistics");
    }

    private void saveStatistics() {
        Optional<Statistic> optionalStatistic;
        Statistic statistic;
        for (Map.Entry<StatisticKeyEnum, Long> entry : statistics.entrySet()) {
            optionalStatistic = statisticRepository.findFirstByEnumKeyAndDate(entry.getKey().name(), statisticCollectDate);
            if (optionalStatistic.isPresent())
                statistic = optionalStatistic.get();
            else {
                statistic = new Statistic();
                statistic.setDate(statisticCollectDate);
                statistic.setEnumKey(entry.getKey().name());
            }
            statistic.setValue(entry.getValue());
            statisticRepository.save(statistic);
        }
    }

    private boolean isNewDay() {
        return DateUtils.isSameDay(new Date(), statisticCollectDate);
    }

    private void saveStatisticsWithAuctionId() {
        for (Map.Entry<StatisticKeyEnum, Map<Long, Long>> entry : statisticsWithAuctionId.entrySet()) {
            saveAuctionsStatistic(entry.getKey(), entry.getValue());
        }
    }

    private void saveAuctionsStatistic(StatisticKeyEnum key, Map<Long, Long> auctionStatistic) {
        Optional<Statistic> optionalStatistic;
        Statistic statistic;
        for (Map.Entry<Long, Long> entry : auctionStatistic.entrySet()) {
            optionalStatistic = statisticRepository.findFirstByEnumKeyAndDateAndAuctionId(key.name(), statisticCollectDate, entry.getKey());
            if (optionalStatistic.isPresent())
                statistic = optionalStatistic.get();
            else {
                statistic = new Statistic();
                statistic.setDate(statisticCollectDate);
                statistic.setEnumKey(key.name());
                Auction auction = auctionService.getAuctionById(entry.getKey());
                if (auction == null)
                    break;
                statistic.setAuction(auction);
            }
            statistic.setValue(entry.getValue());
            statisticRepository.save(statistic);
        }
    }

    /**
     * update information about total auction, users, active reports and message
     * <p>
     * This is scheduled to get fired everyday, at 00:00 (am).
     */
    //TODO Active reports
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTotalStatistics() {
        log.info("start cron to total statistics");
        Long amountOfAuction = auctionService.getTotalAmountOfAuction();
        Long amountOfUser = userRepository.count();
        Long amountOfMessage = messageRepository.count();
        saveStatistic(StatisticKeyEnum.TOTAL_AUCTIONS, amountOfAuction);
        saveStatistic(StatisticKeyEnum.TOTAL_USERS, amountOfUser);
        saveStatistic(StatisticKeyEnum.TOTAL_MESSAGES, amountOfMessage);
        log.info("end cron to total statistics");
    }

    private void saveStatistic(StatisticKeyEnum totalAuctions, Long amountOfAuction) {
        Date yesterdayDate = getYesterdayDate();
        Statistic statistic = new Statistic();
        statistic.setDate(yesterdayDate);
        statistic.setEnumKey(totalAuctions.name());
        statistic.setValue(amountOfAuction);
        statisticRepository.save(statistic);
    }

    private Date getYesterdayDate() {
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        return Date.from(yesterday);
    }

    private void createNewEmptyMapWithKeys() {
        statistics = new HashMap<>();
        statistics.put(StatisticKeyEnum.DAILY_NEW_AUCTIONS, 0L);
        statistics.put(StatisticKeyEnum.DAILY_EDITED_AUCTIONS, 0L);
        statistics.put(StatisticKeyEnum.DAILY_MESSAGES, 0L);
        statistics.put(StatisticKeyEnum.DAILY_REGISTRATIONS, 0L);
        statistics.put(StatisticKeyEnum.DAILY_USERS_LOGIN, 0L);
        statistics.put(StatisticKeyEnum.DAILY_AUCTION_REPORTS, 0L);
        statistics.put(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS, 0L);
        statistics.put(StatisticKeyEnum.DAILY_AUCTION_VIEWS, 0L);
        statistics.put(StatisticKeyEnum.DAILY_REMOVED_AUCTIONS, 0L);
    }
}
