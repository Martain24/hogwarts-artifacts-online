package com.mvilaboa.hogwarts_artifacts_online.wizard.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.wizard.Wizard;
import com.mvilaboa.hogwarts_artifacts_online.wizard.dto.WizardDto;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {

    @Override
    @Nullable
    public WizardDto convert(@NonNull Wizard source) {
        return new WizardDto(
                source.getId(),
                source.getName(),
                source.getNumberOfArtifacts());
    }

}
