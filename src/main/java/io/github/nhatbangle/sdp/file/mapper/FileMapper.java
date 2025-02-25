package io.github.nhatbangle.sdp.file.mapper;

import io.github.nhatbangle.sdp.file.dto.FileMetadata;
import io.github.nhatbangle.sdp.file.entity.File;

public class FileMapper {

    public FileMetadata toResponse(File file) {
        return new FileMetadata(
                file.getId(),
                file.getOriginalName(),
                file.getBytes(),
                file.getCreatedAt().toEpochMilli(),
                file.getMimeType()
        );
    }

}
