package io.github.nhatbangle.sdp.file.controller;

import io.github.nhatbangle.sdp.file.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final StorageService service;

    @GetMapping(
            value = "/download/{signedToken}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getFile(@PathVariable @UUID String signedToken) {
        var file = service.getResource(signedToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

}
