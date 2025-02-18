package com.mvilaboa.hogwarts_artifacts_online.wizard;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;
import com.mvilaboa.hogwarts_artifacts_online.wizard.converter.WizardDtoToWizardConverter;
import com.mvilaboa.hogwarts_artifacts_online.wizard.converter.WizardToWizardDtoConverter;
import com.mvilaboa.hogwarts_artifacts_online.wizard.dto.WizardDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

    private final WizardService wizardService;
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;
    private final WizardDtoToWizardConverter wizardDtoToWizardConverter;

    

    public WizardController(WizardService wizardService, WizardToWizardDtoConverter wizardToWizardDtoConverter,
            WizardDtoToWizardConverter wizardDtoToWizardConverter) {
        this.wizardService = wizardService;
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
        this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
    }

    @GetMapping("/{wizardId}")
    public ResponseEntity<Result<WizardDto>> findWizardById(@PathVariable Integer wizardId) {
        Wizard foundWizard = wizardService.findById(wizardId);
        Result<WizardDto> resultToSend = new Result<>(
            true,
            StatusCode.SUCCESS,
            "Find One Success",
            wizardToWizardDtoConverter.convert(foundWizard)
        );
        return ResponseEntity.ok().body(resultToSend);
    }

    @GetMapping({"/", ""})
    public ResponseEntity<Result<List<WizardDto>>> findAllWizards() {

        List<WizardDto> allWizardDtos = wizardService.findAll().stream()
                .map(wizardToWizardDtoConverter::convert)
                .collect(Collectors.toList());

        Result<List<WizardDto>> resultToSend = new Result<>(
            true,
            StatusCode.SUCCESS,
            "Find All Success",
            allWizardDtos
        );

        return ResponseEntity.ok().body(resultToSend);

    }

    @PostMapping({"", "/"})
    public ResponseEntity<Result<WizardDto>> addWizard(@RequestBody @Valid WizardDto wizardDto) {
        Wizard wizardToSave = wizardDtoToWizardConverter.convert(wizardDto);
        Wizard savedWizard = wizardService.save(wizardToSave);
        Result<WizardDto> resultToSend = new Result<>();
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setData(wizardToWizardDtoConverter.convert(savedWizard));
        resultToSend.setFlag(true);
        resultToSend.setMessage("Add Success");
        return ResponseEntity.ok().body(resultToSend);
    }

    @PutMapping("/{wizardId}")
    public ResponseEntity<Result<WizardDto>> updateWizard(@PathVariable Integer wizardId,
                                                          @RequestBody @Valid WizardDto wizardDto) {
        Wizard wizard = wizardDtoToWizardConverter.convert(wizardDto);
        Wizard wizardUpdated = wizardService.updateById(wizardId, wizard);
        Result<WizardDto> resultToSend = new Result<>();
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setData(wizardToWizardDtoConverter.convert(wizardUpdated));
        resultToSend.setFlag(true);
        resultToSend.setMessage("Update Success");
        return ResponseEntity.ok().body(resultToSend);
    }

    @DeleteMapping("/{wizardId}")
    public ResponseEntity<Result<String>> deleteWizard(@PathVariable Integer wizardId) {
        wizardService.deleteById(wizardId);
        return ResponseEntity.ok().body(new Result<>(true, StatusCode.SUCCESS, "Delete Success"));
    }

    @PutMapping("/{wizardId}/artifacts/{artifactId}")
    public ResponseEntity<Result<String>> assignArtifact(@PathVariable Integer wizardId, @PathVariable String artifactId) {
        this.wizardService.assignArtifact(wizardId, artifactId);
        Result<String> resultToSend = new Result<>();
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setFlag(true);
        resultToSend.setMessage("Artifact Assignment Success");
        return ResponseEntity.ok().body(resultToSend);
    }

}
