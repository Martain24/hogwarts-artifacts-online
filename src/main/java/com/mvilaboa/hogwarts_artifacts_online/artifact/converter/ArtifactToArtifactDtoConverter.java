package com.mvilaboa.hogwarts_artifacts_online.artifact.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import com.mvilaboa.hogwarts_artifacts_online.wizard.converter.WizardToWizardDtoConverter;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {

    private final WizardToWizardDtoConverter wizardDtoConverter;

    public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardDtoConverter) {
        this.wizardDtoConverter = wizardDtoConverter;
    }

    @Override
    @Nullable
    public ArtifactDto convert(@NonNull Artifact source) {
        return new ArtifactDto(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getImageUrl(),
                source.getOwner() == null ? null : this.wizardDtoConverter.convert(source.getOwner()));
    }

}
