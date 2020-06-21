package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.dawid.kaszyca.dto.AuctionBaseDTO;
import pl.dawid.kaszyca.dto.AuctionWithDetailsDTO;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.*;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.AuctionVM;
import pl.dawid.kaszyca.vm.FilterVM;
import pl.dawid.kaszyca.vm.NewAuctionVM;

import java.util.*;

@Service
public class AuctionService {

    AuctionRepository auctionRepository;
    UserService userService;
    CategoryService categoryService;

    public AuctionService(AuctionRepository auctionRepository, UserService userService, CategoryService categoryService) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public AuctionWithDetailsDTO getAuctionById(long id) {
        Optional<Auction> auctionOptional = auctionRepository.findFirstById(id);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            incrementViewersAmount(auction);
            AuctionWithDetailsDTO auctionWithDetailsDTO = MapperUtils.map(auction, AuctionWithDetailsDTO.class);
            return auctionWithDetailsDTO;
        }
        return null;
    }

    private void incrementViewersAmount(Auction auction) {
        auction.setViewers(auction.getViewers() + 1);
        auctionRepository.save(auction);
    }

    public Long saveAuction(NewAuctionVM auctionVM) {
        auctionVM.setPrice(auctionVM.getPrice().replace(",", "."));
        Auction auction = MapperUtils.map(auctionVM, Auction.class);
        Optional<User> user = userService.getCurrentUserObject();
        Category category = categoryService.getCategoryById(auctionVM.getCategory());
        auction.setCategory(category);
        if (user.isPresent()) {
            auction.setUser(user.get());
            return saveAuctionWithDetails(auction, auctionVM);
        }
        return null;
    }

    private Long saveAuctionWithDetails(Auction auction, NewAuctionVM auctionVM) {
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
        auctionRepository.save(auction);
        return auction.getId();
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
            if (auction.isPresent()) {
                Optional<User> optionalUser = userService.getCurrentUserObject();
                if (optionalUser.isPresent()) {
                    if (optionalUser.get().equals(auction.get().getUser())) {
                        auctionRepository.delete(auction.get());
                    }
                }
            }
        }
    }
}
