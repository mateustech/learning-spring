package learning.customer.infrastructure.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GitHubClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);

    private final RestClient restClient;

    public GitHubClient() {
        this.restClient = RestClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
    }

    public GitHubProfile fetchProfile(String username) {
        try {
            log.info("event=github_fetch_started githubUsername={}", username);
            var response = restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubUserResponse.class);

            if (response == null || response.login() == null || response.login().isBlank()) {
                throw new GitHubIntegrationException("Invalid response from GitHub API");
            }

            var displayName = (response.name() == null || response.name().isBlank())
                ? response.login()
                : response.name().trim();

            log.info("event=github_fetch_succeeded githubUsername={} resolvedName={}", response.login(), displayName);
            return new GitHubProfile(response.login().trim(), displayName);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("event=github_fetch_failed reason=user_not_found githubUsername={}", username);
            throw new GitHubUserNotFoundException(username);
        } catch (RestClientException ex) {
            log.error("event=github_fetch_failed reason=request_error githubUsername={}", username, ex);
            throw new GitHubIntegrationException("Failed to call GitHub API", ex);
        }
    }
}
