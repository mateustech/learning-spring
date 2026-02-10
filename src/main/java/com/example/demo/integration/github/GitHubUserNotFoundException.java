package com.example.demo.integration.github;

public class GitHubUserNotFoundException extends RuntimeException {

    public GitHubUserNotFoundException(String username) {
        super("GitHub user not found: " + username);
    }
}
