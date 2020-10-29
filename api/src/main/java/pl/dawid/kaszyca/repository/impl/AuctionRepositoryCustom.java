package pl.dawid.kaszyca.repository.impl;

import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.vm.FilterVM;

import java.util.List;

public interface AuctionRepositoryCustom {

    List<Auction> findByFilters(FilterVM filterVM);

    Long countByFilters(FilterVM filterVM);

    List<Auction> findTop4ByCategoryOrderByViewers(String category);
}
