package pl.dawid.kaszyca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.repository.impl.AuctionRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository  extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {

    Optional<Auction> findFirstById(Long id);

    Page<Auction> findAllByCategoryCategory(String category, Pageable pageable );

    List<Auction> findTop4ByOrderByViewersDesc();
}
