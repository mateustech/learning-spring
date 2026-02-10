package learning.customer.infrastructure.persistence;

import learning.customer.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
    boolean existsByGithubUsername(String githubUsername);

    List<Customer> findByActiveTrue();

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByGithubUsername(String githubUsername);
}
