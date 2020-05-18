package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dawid.kaszyca.model.auction.AuctionDetails;

public interface AuctionDetailsRepository extends JpaRepository<AuctionDetails, Long> {
}
