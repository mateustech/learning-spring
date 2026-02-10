package learning.customer.infrastructure.github;

public record GitHubUserResponse(
    String login,
    String name
) {
}
