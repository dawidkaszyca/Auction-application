package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dawid.kaszyca.model.City;

public interface CityRepository extends JpaRepository<City, String> {
}
