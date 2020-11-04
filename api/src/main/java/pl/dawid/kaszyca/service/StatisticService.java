package pl.dawid.kaszyca.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.config.StatisticKeyEnum;
import pl.dawid.kaszyca.dto.StatisticDTO;
import pl.dawid.kaszyca.model.Statistic;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.repository.MessageRepository;
import pl.dawid.kaszyca.repository.StatisticRepository;
import pl.dawid.kaszyca.repository.UserRepository;
import pl.dawid.kaszyca.util.MapperUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class StatisticService {

    private Map<StatisticKeyEnum, Long> statistics;

    private Date statisticCollectDate;

    private Map<StatisticKeyEnum, Map<Long, Long>> statisticsWithAuctionId;

    private StatisticRepository statisticRepository;

    private AuctionService auctionService;

    private UserRepository userRepository;

    private MessageRepository messageRepository;

    StatisticService(StatisticRepository statisticRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.statisticRepository = statisticRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        createNewEmptyMapWithKeys();
        createNewEmptyStatisticsWithIdMap();
        statisticCollectDate = new Date();
    }

    @Autowired
    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    public void incrementDailyAuctionViewsById(Long id) {
        incrementValueByKeyAndAuctionId(StatisticKeyEnum.DAILY_AUCTION_VIEWS_BY_ID, id);
        incrementDailyAuctionViews();
    }

    public void incrementDailyAuctionPhoneClicksById(Long id) {
        incrementValueByKeyAndAuctionId(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS_BY_ID, id);
        incrementDailyAuctionPhoneClicks();
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

    public void incrementDailyAuctionViews() {
        incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_VIEWS);
    }

    public void incrementDailyAuctionReports() {
        incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_REPORTS);
    }

    public void incrementDailyRemovedAuction() {
        incrementValueByKey(StatisticKeyEnum.DAILY_REMOVED_AUCTIONS);
    }

    private void incrementDailyAuctionPhoneClicks() { incrementValueByKey(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS); }

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

    private void saveStatisticsWithAuctionId() {
        for (Map.Entry<StatisticKeyEnum, Map<Long, Long>> entry : statisticsWithAuctionId.entrySet()) {
            saveAuctionsStatistic(entry.getKey(), entry.getValue());
        }
    }

    private void saveAuctionsStatistic(StatisticKeyEnum key, Map<Long, Long> auctionStatistic) {
        try {
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
        } catch (Exception e) {
            log.error("Cannot save daily statistics");
        }
    }

    private boolean isNewDay() {
        return DateUtils.isSameDay(new Date(), statisticCollectDate);
    }

    private void createNewEmptyMapWithKeys() {
        statistics = new EnumMap<>(StatisticKeyEnum.class);
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

    private void createNewEmptyStatisticsWithIdMap() {
        statisticsWithAuctionId = new EnumMap<>(StatisticKeyEnum.class);
        statisticsWithAuctionId.put(StatisticKeyEnum.DAILY_AUCTION_VIEWS_BY_ID, new HashMap<>());
        statisticsWithAuctionId.put(StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS_BY_ID, new HashMap<>());
    }

    /**
     * update information about total auction, users, active reports and message
     * <p>
     * This is scheduled to get fired everyday, at 00:00 (am).
     */
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

    public Map<StatisticKeyEnum, List<StatisticDTO>> getAuctionStatisticsById(Long id) {
        Map<StatisticKeyEnum, List<StatisticDTO>> result = new EnumMap<>(StatisticKeyEnum.class);
        putAuctionStatisticByKey(result, StatisticKeyEnum.DAILY_AUCTION_PHONE_CLICKS_BY_ID, id);
        putAuctionStatisticByKey(result, StatisticKeyEnum.DAILY_AUCTION_VIEWS_BY_ID, id);
        return result;
    }

    private void putAuctionStatisticByKey(Map<StatisticKeyEnum, List<StatisticDTO>> result, StatisticKeyEnum key, Long id) {
        List<Statistic> savedStatistics = statisticRepository.findAllByEnumKeyAndAuctionId(key.name(), id);
        if (!savedStatistics.isEmpty()) {
            List<StatisticDTO> statisticDTOS = MapperUtil.mapAll(savedStatistics, StatisticDTO.class);
            result.put(key, statisticDTOS);
        }
    }
}
