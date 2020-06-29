package pl.dawid.kaszyca.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.config.StatisticKeyEnum;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.repository.StatisticRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticService {

    private Map<StatisticKeyEnum, Integer> statistics;
    private Map<String, Integer> statisticsWithAuctionId;
    private StatisticRepository statisticRepository;
    private AuctionRepository auctionRepository;

    StatisticService(StatisticRepository statisticRepository, AuctionRepository auctionRepository) {
        createNewEmptyMapWithKeys();
        this.statisticRepository = statisticRepository;
        this.auctionRepository = auctionRepository;
    }

    public void incrementDailyAuctionViewsById(Long id) {
        incrementValueByKeyAndAuctionId(StatisticKeyEnum.DAILY_AUCTION_VIEWS, id);
    }

    public void incrementDailyAuctionPhoneClicksById(Long id) {
        incrementValueByKeyAndAuctionId(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS, id);
    }

    private void incrementValueByKeyAndAuctionId(StatisticKeyEnum dailyAuctionViews, Long id) {
        Integer value = statisticsWithAuctionId.get(dailyAuctionViews.name() + "_" + id);
        if (value == null) {
            value = 0;
        }
        value++;
        statisticsWithAuctionId.put(dailyAuctionViews.name() + "_" + id, value);
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

    public void incrementDailyAuctionPhoneClicksBy() {
        incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_REPORTS);
    }

    private void incrementValueByKey(StatisticKeyEnum statisticKeyEnum) {
        statistics.put(statisticKeyEnum, statistics.get(statisticKeyEnum) + 1);
    }

    /**
     * update information about total auction, users, active reports and message
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTotalStatistics() {
        Date date = new Date();
    }

    private void createNewEmptyMapWithKeys() {
        statistics = new HashMap<>();
        statistics.put(StatisticKeyEnum.DAILY_NEW_AUCTIONS, 0);
        statistics.put(StatisticKeyEnum.DAILY_EDITED_AUCTIONS, 0);
        statistics.put(StatisticKeyEnum.DAILY_MESSAGES, 0);
        statistics.put(StatisticKeyEnum.DAILY_REGISTRATIONS, 0);
        statistics.put(StatisticKeyEnum.DAILY_USERS_LOGIN, 0);
        statistics.put(StatisticKeyEnum.DAILY_AUCTION_REPORTS, 0);
    }
}
