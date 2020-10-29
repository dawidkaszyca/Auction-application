package pl.dawid.kaszyca.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.model.auction.FavoriteAuction;

import java.util.List;

public interface FavoriteAuctionRepository extends PagingAndSortingRepository<FavoriteAuction, Long> {

    List<FavoriteAuction> findAllByUser(User user, Pageable pageable);

    List<FavoriteAuction> findAllByUserAndAuction(User user, Auction auction);

    Long countByUser(User user);

    List<FavoriteAuction> findAllByAuction(Auction auction);
}
