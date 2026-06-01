package org.spring.ai.model;

import jakarta.validation.constraints.NotBlank;

public record Question(@NotBlank(message = "Game title is Required") String gameTitle,
                       @NotBlank(message = "Question is required") String question) {
}
