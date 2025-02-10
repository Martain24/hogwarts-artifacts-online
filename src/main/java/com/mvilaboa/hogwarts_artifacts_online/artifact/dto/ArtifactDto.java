package com.mvilaboa.hogwarts_artifacts_online.artifact.dto;

import com.mvilaboa.hogwarts_artifacts_online.wizard.dto.WizardDto;

import jakarta.validation.constraints.NotBlank;

public record ArtifactDto(
        String id,
        @NotBlank(message = "name is required.")
        String name,
        @NotBlank(message = "description is required")
        String description,
        @NotBlank(message = "imageUrl is required")
        String imageUrl,
        WizardDto owner) {

}
