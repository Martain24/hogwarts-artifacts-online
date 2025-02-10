package com.mvilaboa.hogwarts_artifacts_online.artifact.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.dto.ArtifactDto;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact> {

    @Override
    @Nullable
    public Artifact convert(@NonNull ArtifactDto source) {
        Artifact artifact = new Artifact();
        artifact.setId(source.id());
        artifact.setName(source.name());
        artifact.setDescription(source.description());
        artifact.setImageUrl(source.imageUrl());
        return artifact;
    }
    
}
