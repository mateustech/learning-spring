package learning.customer.infrastructure.github;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GitHubClient {

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

            return new GitHubProfile(response.login().trim(), displayName);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new GitHubUserNotFoundException(username);
        } catch (RestClientException ex) {
            throw new GitHubIntegrationException("Failed to call GitHub API", ex);
        }
    }
}
