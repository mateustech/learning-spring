package com.example.demo.integration.github;

public class GitHubIntegrationException extends RuntimeException {

    public GitHubIntegrationException(String message) {
        super(message);
    }

    public GitHubIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
