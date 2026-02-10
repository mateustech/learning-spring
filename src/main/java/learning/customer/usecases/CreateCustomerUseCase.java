package learning.customer.usecases;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.DuplicateEmailException;
import learning.customer.domain.exception.DuplicateGithubUsernameException;
import learning.customer.infrastructure.messaging.CustomerCreatedEvent;
import learning.customer.infrastructure.messaging.CustomerCreatedEventPublisher;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCustomerUseCase.class);

    private final CustomerJpaRepository customerRepository;
    private final CustomerCreatedEventPublisher eventPublisher;

    public CreateCustomerUseCase(
        CustomerJpaRepository customerRepository,
        CustomerCreatedEventPublisher eventPublisher
    ) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Customer execute(String email, String githubUsername) {
        String correlationId = currentCorrelationId();
        String normalizedEmail = normalizeEmail(email);
        String normalizedGithubUsername = normalizeGithubUsername(githubUsername);

        log.info(
            "event=customer_create_started correlationId={} email={} githubUsername={}",
            correlationId,
            normalizedEmail,
            normalizedGithubUsername
        );

        if (customerRepository.existsByEmail(normalizedEmail)) {
            log.warn(
                "event=customer_create_rejected reason=duplicate_email correlationId={} email={}",
                correlationId,
                normalizedEmail
            );
            throw new DuplicateEmailException(normalizedEmail);
        }
        if (customerRepository.existsByGithubUsername(normalizedGithubUsername)) {
            log.warn(
                "event=customer_create_rejected reason=duplicate_github_username correlationId={} githubUsername={}",
                correlationId,
                normalizedGithubUsername
            );
            throw new DuplicateGithubUsernameException(normalizedGithubUsername);
        }

        Customer customer = new Customer();
        customer.setName(normalizedGithubUsername);
        customer.setEmail(normalizedEmail);
        customer.setGithubUsername(normalizedGithubUsername);
        var saved = customerRepository.save(customer);
        log.info(
            "event=customer_create_persisted correlationId={} customerId={} githubUsername={}",
            correlationId,
            saved.getId(),
            saved.getGithubUsername()
        );
        eventPublisher.publish(new CustomerCreatedEvent(saved.getId(), saved.getGithubUsername(), correlationId));
        return saved;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeGithubUsername(String githubUsername) {
        return githubUsername.trim().toLowerCase(Locale.ROOT);
    }

    private String currentCorrelationId() {
        var value = MDC.get("correlationId");
        return value == null || value.isBlank() ? UUID.randomUUID().toString() : value;
    }
}
