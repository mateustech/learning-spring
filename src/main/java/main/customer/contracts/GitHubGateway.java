package main.customer.contracts;

import main.customer.infrastructure.github.GitHubProfile;

public interface GitHubGateway {

    GitHubProfile fetchProfile(String username);
}
