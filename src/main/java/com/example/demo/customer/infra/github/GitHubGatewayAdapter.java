package com.example.demo.customer.infra.github;

import com.example.demo.customer.application.port.GitHubGateway;
import com.example.demo.integration.github.GitHubClient;
import com.example.demo.integration.github.GitHubProfile;
import org.springframework.stereotype.Component;

@Component
public class GitHubGatewayAdapter implements GitHubGateway {

    private final GitHubClient gitHubClient;

    public GitHubGatewayAdapter(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    @Override
    public GitHubProfile fetchProfile(String username) {
        return gitHubClient.fetchProfile(username);
    }
}
