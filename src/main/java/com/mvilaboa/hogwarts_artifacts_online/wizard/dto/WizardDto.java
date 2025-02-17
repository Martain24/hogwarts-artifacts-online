package com.mvilaboa.hogwarts_artifacts_online.wizard.dto;

import jakarta.validation.constraints.NotBlank;

public record WizardDto(
        Integer id,
        @NotBlank
        String name, 
        Integer numberOfArtifacts) {
    
}
