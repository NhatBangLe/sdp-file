package io.github.nhatbangle.sdp.file.dto;

import java.io.Serializable;

/**
 * DTO for {@link io.github.nhatbangle.sdp.file.entity.File}
 */
public record FileMetadata(
        String id,
        String name,
        long size,
        long createdAtMs,
        String mimeType
) implements Serializable {
}