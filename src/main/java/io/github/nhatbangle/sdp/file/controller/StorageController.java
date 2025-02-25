package io.github.nhatbangle.sdp.file.controller;

import io.github.nhatbangle.sdp.file.dto.FileMetadata;
import io.github.nhatbangle.sdp.file.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class StorageController {

    private final StorageService service;

    @GetMapping("/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Get download URL")
    public ResponseEntity<Resource> getDownloadPath(@PathVariable @UUID String fileId) {
        var file = service.getResource(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/{fileId}/metadata")
    @ResponseStatus(HttpStatus.OK)
    public FileMetadata getMetadata(@PathVariable @UUID String fileId) {
        return service.getMetadata(fileId);
    }

    @PostMapping("/{userId}")
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
