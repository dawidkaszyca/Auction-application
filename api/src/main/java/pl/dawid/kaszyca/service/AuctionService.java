package pl.dawid.kaszyca.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.AuctionBaseDTO;
import pl.dawid.kaszyca.dto.AuctionDTO;
import pl.dawid.kaszyca.model.City;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.*;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.repository.CityRepository;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.NewAuctionVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    AuctionRepository auctionRepository;
    UserService userService;
    CategoryService categoryService;
    CityRepository cityRepository;

    public AuctionService(AuctionRepository auctionRepository, UserService userService, CategoryService categoryService,
                          CityRepository cityRepository) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.cityRepository = cityRepository;
    }

    public AuctionDTO getAuctionById(long id) {
        Optional<Auction> auction = auctionRepository.findFirstById(id);
        if (auction.isPresent()) {
            return MapperUtils.map(auction.get(), AuctionDTO.class);
        }
        return null;
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
                auctionDetails.setCategoryAttributes(categoryAttributes.getId());
                auctionDetails.setAttributeValues(attributeValues.getValue());
                detailsToSave.add(auctionDetails);
            }
        }
        setCityToAuction(auction);
        auction.setAuctionDetails(detailsToSave);
        auctionRepository.save(auction);
        return auction.getId();
    }

    private void setCityToAuction(Auction auction) {
        City city = auction.getCity();
        auction.setCity(cityRepository.save(city));
    }
    //TODO if category is empty find by views!!!
    public List<AuctionBaseDTO> getAuctionsByCategoryAndPage(String category, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Auction> auctions;
        if (category.equals("all"))
            auctions = auctionRepository.findAll(pageable);
        else
            auctions = auctionRepository.findAllByCategoryCategory(category, pageable);
        return MapperUtils.mapAll(auctions.getContent(), AuctionBaseDTO.class);
    }
}
