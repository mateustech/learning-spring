package main.customer.domain.exception;

public class DuplicateGithubUsernameException extends RuntimeException {

    public DuplicateGithubUsernameException(String githubUsername) {
        super("GitHub username already in use: " + githubUsername);
    }
}
