package com.example.demo.customer.application.port;

import com.example.demo.integration.github.GitHubProfile;

public interface GitHubGateway {

    GitHubProfile fetchProfile(String username);
}
