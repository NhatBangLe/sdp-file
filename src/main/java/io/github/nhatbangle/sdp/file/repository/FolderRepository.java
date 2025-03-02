package io.github.nhatbangle.sdp.file.repository;

import io.github.nhatbangle.sdp.file.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface FolderRepository extends JpaRepository<Folder, String> {
    @Query("select f from Folder f where f.user.id = ?1 and f.folder is null")
    Optional<Folder> findRootFolderByUserId(String id);
}