package learning.customer.infrastructure.messaging;

import learning.customer.infrastructure.github.GitHubClient;
import learning.customer.infrastructure.github.GitHubUserNotFoundException;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomerCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerCreatedEventListener.class);

    private final CustomerJpaRepository customerRepository;
    private final GitHubClient gitHubClient;

    public CustomerCreatedEventListener(CustomerJpaRepository customerRepository, GitHubClient gitHubClient) {
        this.customerRepository = customerRepository;
        this.gitHubClient = gitHubClient;
    }

    @RabbitListener(queues = CustomerMessagingConfig.CREATED_QUEUE)
    @Transactional
    public void onCustomerCreated(CustomerCreatedEvent event) {
        String correlationId = (event.correlationId() == null || event.correlationId().isBlank())
            ? "na"
            : event.correlationId();
        MDC.put("correlationId", correlationId);
        log.info(
            "event=customer_created_event_consumed correlationId={} customerId={} githubUsername={}",
            correlationId,
            event.customerId(),
            event.githubUsername()
        );
        var customerOpt = customerRepository.findById(event.customerId());
        if (customerOpt.isEmpty()) {
            log.warn(
                "event=customer_enrichment_skipped reason=customer_not_found correlationId={} customerId={}",
                correlationId,
                event.customerId()
            );
            MDC.remove("correlationId");
            return;
        }

        try {
            var profile = gitHubClient.fetchProfile(event.githubUsername());
            var customer = customerOpt.get();
            customer.setName(profile.displayName());
            customer.setGithubUsername(profile.login().toLowerCase(Locale.ROOT));
            customerRepository.save(customer);
            log.info(
                "event=customer_enrichment_succeeded correlationId={} customerId={} resolvedName={} resolvedGithubUsername={}",
                correlationId,
                event.customerId(),
                profile.displayName(),
                profile.login().toLowerCase(Locale.ROOT)
            );
        } catch (GitHubUserNotFoundException ex) {
            log.warn(
                "event=customer_enrichment_failed reason=github_user_not_found correlationId={} customerId={} githubUsername={}",
                correlationId,
                event.customerId(),
                event.githubUsername()
            );
        } catch (Exception ex) {
            // Keep consumer resilient; with DLQ/retry policy this can be revisited.
            log.error(
                "event=customer_enrichment_failed reason=unexpected_error correlationId={} customerId={}",
                correlationId,
                event.customerId(),
                ex
            );
        } finally {
            MDC.remove("correlationId");
        }
    }
}
