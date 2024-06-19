package de.hsrm.quiz_gateway.request;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
