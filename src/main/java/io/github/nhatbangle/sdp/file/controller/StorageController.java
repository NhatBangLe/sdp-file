package io.github.nhatbangle.sdp.file.controller;

import io.github.nhatbangle.sdp.file.dto.FileMetadata;
import io.github.nhatbangle.sdp.file.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class StorageController {

    private final StorageService service;

    @GetMapping(value = "/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Generate file download URL")
    public String generateDownloadUrl(@PathVariable @UUID String fileId) {
        return service.generateDownloadUrl(fileId);
    }

    @GetMapping(
            value = "/{fileId}/metadata",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public FileMetadata getMetadata(@PathVariable @UUID String fileId) {
        return service.getMetadata(fileId);
    }

    @PostMapping(
            value = "/{userId}",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public String store(
            @PathVariable @UUID String userId,
            @RequestParam("file") @NotNull MultipartFile file
    ) {
        return service.store(userId, file);
    }

    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String fileId) {
        service.delete(fileId);
    }

}
