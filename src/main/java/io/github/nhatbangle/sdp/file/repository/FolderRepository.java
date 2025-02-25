package io.github.nhatbangle.sdp.file.repository;

import io.github.nhatbangle.sdp.file.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface FolderRepository extends JpaRepository<Folder, String> {
    Optional<Folder> findByUser_Id(String id);
}