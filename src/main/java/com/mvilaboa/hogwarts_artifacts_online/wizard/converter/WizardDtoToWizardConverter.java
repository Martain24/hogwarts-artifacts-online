package com.mvilaboa.hogwarts_artifacts_online.wizard.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.wizard.Wizard;
import com.mvilaboa.hogwarts_artifacts_online.wizard.dto.WizardDto;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDto, Wizard> {

    @Override
    public Wizard convert(@NonNull WizardDto source) {
        Wizard wizard = new Wizard();
        wizard.setName(source.name());
        return wizard;
    }
    
}
