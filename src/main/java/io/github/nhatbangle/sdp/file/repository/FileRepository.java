package io.github.nhatbangle.sdp.file.repository;

import io.github.nhatbangle.sdp.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface FileRepository extends JpaRepository<File, String> {
}