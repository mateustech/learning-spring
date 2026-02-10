package learning.customer.presentation.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 160, message = "email must have at most 160 characters")
    String email,

    @NotBlank(message = "githubUsername is required")
    @Size(max = 39, message = "githubUsername must have at most 39 characters")
    String githubUsername
) {
}
