package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.Statistic;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
}
