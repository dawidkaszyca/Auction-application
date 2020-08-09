package pl.dawid.kaszyca.service;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.dawid.kaszyca.dto.AuctionBaseDTO;
import pl.dawid.kaszyca.dto.AuctionWithDetailsDTO;
import pl.dawid.kaszyca.exception.PermissionDeniedToAuction;
import pl.dawid.kaszyca.model.Statistic;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.*;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.AuctionVM;
import pl.dawid.kaszyca.vm.FilterVM;
import pl.dawid.kaszyca.vm.NewAuctionVM;

import java.time.Instant;
import java.util.*;

@Service
public class AuctionService {

    AuctionRepository auctionRepository;
    UserService userService;
    CategoryService categoryService;
    StatisticService statisticService;

    public AuctionService(AuctionRepository auctionRepository, UserService userService, CategoryService categoryService,
                          StatisticService statisticService) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.statisticService = statisticService;
    }

    public AuctionWithDetailsDTO getAuctionWithDetailsObjectById(long id) {
        Optional<Auction> auctionOptional = auctionRepository.findFirstById(id);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            incrementViewersAmount(auction);
            AuctionWithDetailsDTO auctionWithDetailsDTO = MapperUtils.map(auction, AuctionWithDetailsDTO.class);
            return auctionWithDetailsDTO;
        }
        return null;
    }

    public Auction getAuctionById(long id) {
        Optional<Auction> auctionOptional = auctionRepository.findFirstById(id);
        return auctionOptional.orElse(null);
    }

    private void incrementViewersAmount(Auction auction) {
        statisticService.incrementDailyAuctionViews();
        statisticService.incrementDailyAuctionViewsById(auction.getId());
        auction.setViewers(auction.getViewers() + 1);
        auctionRepository.save(auction);
    }

    public Long saveAuction(NewAuctionVM auctionVM) {
        Auction auction = MapperUtils.map(auctionVM, Auction.class);
        setDataToNewAuction(auctionVM, auction);
        Optional<User> user = userService.getCurrentUserObject();
        if (user.isPresent()) {
            auction.setUser(user.get());
            auctionRepository.save(auction);
            statisticService.incrementDailyNewAuction();
            return auction.getId();
        }
        return null;
    }

    private void setDataToNewAuction(NewAuctionVM auctionVM, Auction auction) {
        auctionVM.setPrice(auctionVM.getPrice().replace(",", "."));
        Category category = categoryService.getCategoryById(auctionVM.getCategory());
        auction.setCategory(category);
        setAuctionDetails(auction, auctionVM);
    }

    public Long updateAuction(NewAuctionVM auctionVM) {
        Auction auction = MapperUtils.map(auctionVM, Auction.class);
        setDataToNewAuction(auctionVM, auction);
        if (auctionVM.getId() != null) {
            auction.setId(auctionVM.getId());
            setAuctionImages(auction);
        }
        Optional<User> user = userService.getCurrentUserObject();
        if (user.isPresent()) {
            auction.setUser(user.get());
            checkPermissionToEdit(auction.getUser().getId());
            removeOldAuctionDetails(auction.getId());
            auction.getCity().setId(getCityIdToUpdate(auction.getId()));
            auctionRepository.save(auction);
            statisticService.incrementDailyEditedAuction();
            return auction.getId();
        }
        return null;
    }

    private void removeOldAuctionDetails(Long id) {
        Optional<Auction> auction = auctionRepository.findById(id);
        if (auction.isPresent()) {
            auction.get().getAuctionDetails().clear();
            auctionRepository.save(auction.get());
        }
    }

    private Long getCityIdToUpdate(Long id) {
        Optional<Auction> auction = auctionRepository.findById(id);
        return auction.map(value -> value.getCity().getId()).orElse(null);
    }

    private void setAuctionImages(Auction auction) {
        Optional<Auction> auctionAllReadySaved = auctionRepository.findFirstById(auction.getId());
        if (auctionAllReadySaved.isPresent())
            auction.setImages(auctionAllReadySaved.get().getImages());
    }

    private void setAuctionDetails(Auction auction, NewAuctionVM auctionVM) {
        List<CategoryAttributes> categoryAttributesList = MapperUtils.mapAll(auctionVM.getAttributes(), CategoryAttributes.class);
        List<AuctionDetails> detailsToSave = new ArrayList<>();
        for (CategoryAttributes categoryAttributes : categoryAttributesList) {
            for (AttributeValues attributeValues : categoryAttributes.getAttributeValues()) {
                AuctionDetails auctionDetails = new AuctionDetails();
                auctionDetails.setAuction(auction);
                auctionDetails.setCategoryAttribute(categoryAttributes.getAttribute());
                auctionDetails.setAttributeValue(attributeValues.getValue());
                detailsToSave.add(auctionDetails);
            }
        }
        auction.setAuctionDetails(detailsToSave);
    }

    public AuctionVM getAuctionsFilter(FilterVM filterVM) {
        AuctionVM auctionVM = new AuctionVM();
        List<Auction> auctions = auctionRepository.findByFilters(filterVM);
        auctionVM.setAuctionListBase(MapperUtils.mapAll(auctions, AuctionBaseDTO.class));
        auctionVM.setNumberOfAuctionByProvidedFilters(auctionRepository.countByFilters(filterVM));
        return auctionVM;
    }

    public Map<String, List<String>> getAuctionData() {
        Map<String, List<String>> data = new HashMap<>();
        List<String> category = categoryService.getCategoriesName();
        data.put("category", category);
        String name = userService.getCurrentUserName();
        if (name != null)
            data.put("name", Collections.singletonList(name));
        String email = userService.getCurrentUserEmail();
        if (name != null)
            data.put("email", Collections.singletonList(email));
        data.put("condition", Arrays.asList("Nowy", "Używany"));
        return data;
    }

    public List<AuctionBaseDTO> getTopAuction(String category) {
        List<Auction> auctions;
        if (!StringUtils.isEmpty(category) && category.equals("all"))
            auctions = auctionRepository.findTop4ByOrderByViewersDesc();
        else
            auctions = auctionRepository.findTop4ByCategoryOrderByViewers(category);
        return MapperUtils.mapAll(auctions, AuctionBaseDTO.class);
    }

    public void removeAuctionsById(List<Integer> ids) {
        for (Integer id : ids) {
            Optional<Auction> auction = auctionRepository.findById(Long.valueOf(id));
            if (auction.isPresent() && checkPermissionToEdit(auction.get().getUser().getId())) {
                auctionRepository.delete(auction.get());
                statisticService.incrementDailyRemovedAuction();
            }
        }
    }

    public boolean checkPermissionToEdit(Long auctionUserId) {
        Optional<User> user = userService.getCurrentUserObject();
        if (!isUserAuctionId(user, auctionUserId)) {
            throw new PermissionDeniedToAuction();
        }
        return true;
    }

    private boolean isUserAuctionId(Optional<User> optionalUser, Long auction) {
        return optionalUser.isPresent() && optionalUser.get().getId().equals(auction);
    }

    public Instant extendAuctionEndTimeById(long id) {
        Optional<Auction> auction = auctionRepository.findById(id);
        if (auction.isPresent()) {
            checkPermissionToEdit(auction.get().getUser().getId());
            auction.get().setExpiredDate(getCurrentDatePlusOneMonth());
            Auction savedAuction = auctionRepository.save(auction.get());
            return savedAuction.getExpiredDate();
        }
        return null;
    }

    public Long getTotalAmountOfAuction() {
        return auctionRepository.count();
    }

    private Instant getCurrentDatePlusOneMonth() {
        return DateUtils.addMonths(new Date(), 1).toInstant();
    }

}
