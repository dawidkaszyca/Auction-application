package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.Image;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findFirstByAuctionIdAndIsMainAuctionPhoto(Long id, boolean isMainAuctionPhoto);

    List<Image> findAllByAuctionId(Long id);
}
