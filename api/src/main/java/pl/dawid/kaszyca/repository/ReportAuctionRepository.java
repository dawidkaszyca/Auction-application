package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.auction.ReportedAuction;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportAuctionRepository extends JpaRepository<ReportedAuction, Long> {

    List<ReportedAuction> findAllByActive(boolean active);

    Optional<ReportedAuction> findFirstById(Long id);

    Long countAllByActive(Boolean active);
}
