package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

}

