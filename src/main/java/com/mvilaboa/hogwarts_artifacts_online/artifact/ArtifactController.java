package com.mvilaboa.hogwarts_artifacts_online.artifact;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvilaboa.hogwarts_artifacts_online.artifact.converter.ArtifactDtoToArtifactConverter;
import com.mvilaboa.hogwarts_artifacts_online.artifact.converter.ArtifactToArtifactDtoConverter;
import com.mvilaboa.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    

    public ArtifactController(ArtifactService artifactService,
            ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
            ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping("/{artifactId}")
    public ResponseEntity<Result<ArtifactDto>> findArtifactById(@PathVariable String artifactId) {
        Artifact artifact = this.artifactService.findById(artifactId);
        Result<ArtifactDto> resultToSend = new Result<>(
                true,
                StatusCode.SUCCESS,
                "Find One Success",
                artifactToArtifactDtoConverter.convert(artifact));
        return ResponseEntity.ok().body(resultToSend);
    }

    @GetMapping("/")
    public ResponseEntity<Result<List<ArtifactDto>>> findAllArtifacts() {

        List<ArtifactDto> foundArtifacts = this.artifactService.findAll()
                .stream()
                .map(this.artifactToArtifactDtoConverter::convert)
                .collect(Collectors.toList());

        Result<List<ArtifactDto>> resultToSend = new Result<>(
                true,
                StatusCode.SUCCESS,
                "Find All Success",
                foundArtifacts);

        return ResponseEntity.ok().body(resultToSend);
    }


    @PostMapping("/")
    public ResponseEntity<Result<ArtifactDto>> addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
        Artifact newArtifact = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = this.artifactService.save(newArtifact);
        Result<ArtifactDto> resultToSend = new Result<>(
            true,
            StatusCode.SUCCESS,
            "Add Success",
            artifactToArtifactDtoConverter.convert(savedArtifact)
        );
        return ResponseEntity.ok().body(resultToSend);
    }

    @PutMapping("/{artifactId}")
    public ResponseEntity<Result<ArtifactDto>> updateArtifact(@PathVariable String artifactId, @Valid @RequestBody ArtifactDto artifactDto) {
        Artifact updatedArtifact = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact finalArtifact = artifactService.update(artifactId, updatedArtifact);
        Result<ArtifactDto> resultToSend = new Result<>(
            true,
            StatusCode.SUCCESS,
            "Update Success",
            artifactToArtifactDtoConverter.convert(finalArtifact)
        );
        return ResponseEntity.ok().body(resultToSend);
    }

    @DeleteMapping("/{artifactId}")
    public ResponseEntity<Result<Object>> deleteArtifact(@PathVariable String artifactId) {
        this.artifactService.delete(artifactId);
        return ResponseEntity.ok().body(new Result<>(true, StatusCode.SUCCESS, "Delete Success"));
    }

}
