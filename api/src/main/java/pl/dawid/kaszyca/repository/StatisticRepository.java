package pl.dawid.kaszyca.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.Statistic;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    Optional<Statistic> findFirstByEnumKeyAndDate(String enumKey, Date date);

    Optional<Statistic> findFirstByEnumKeyAndDateAndAuctionId(String enumKey, Date date, Long id);

    List<Statistic> findAllByEnumKeyAndAuctionId(String enumKey, Long id);

    Long countAllByEnumKey(String enumKey);

    List<Statistic> findAllByEnumKeyOrderByDateDesc(String enumKey, Pageable pageable);
}
