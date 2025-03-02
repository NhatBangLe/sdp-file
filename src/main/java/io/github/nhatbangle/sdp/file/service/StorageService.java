package io.github.nhatbangle.sdp.file.service;

import io.github.nhatbangle.sdp.file.dto.FileMetadata;
import io.github.nhatbangle.sdp.file.entity.File;
import io.github.nhatbangle.sdp.file.entity.Folder;
import io.github.nhatbangle.sdp.file.exception.FileProcessingException;
import io.github.nhatbangle.sdp.file.mapper.FileMapper;
import io.github.nhatbangle.sdp.file.repository.FileRepository;
import io.github.nhatbangle.sdp.file.repository.FolderRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class StorageService {

    private final UserService userService;
    private final FolderRepository folderRepository;
    private final MessageSource messageSource;
    private final FileRepository repository;
    private final FileMapper fileMapper;

    private final Path storageDir;

    @NotNull
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String store(
            @NotNull @UUID String userId,
            @NotNull MultipartFile multipartFile
    ) throws FileProcessingException, IllegalArgumentException {
        var user = userService.getById(userId);
        var folder = folderRepository.findRootFolderByUserId(userId)
                .orElse(Folder.builder()
                        .name(userId)
                        .user(user)
                        .build());

        var file = repository.save(File.builder()
                .originalName(multipartFile.getOriginalFilename())
                .mimeType(multipartFile.getContentType())
                .bytes(multipartFile.getSize())
                .folder(folder)
                .saveLocation("temporary")
                .user(user)
                .build());
        var fileId = file.getId();

        try {
            // create user folder
            var dir = storageDir.resolve(folder.getName());
            if (!Files.isDirectory(dir)) {
                try {
                    Files.createDirectory(dir);
                } catch (FileAlreadyExistsException e) {
                    var message = messageSource.getMessage(
                            "file.duplicate_root_dir",
                            new Object[]{dir},
                            Locale.getDefault()
                    );
                    log.warn(message);
                }
            }
            // write file
            var bytes = multipartFile.getBytes();
            var newFile = Files.write(dir.resolve(fileId), bytes, StandardOpenOption.CREATE);
            // save path
            file.setSaveLocation(newFile.toString());
            repository.save(file);

            return fileId;
        } catch (IOException | InvalidPathException | SecurityException e) {
            log.error(e.getLocalizedMessage(), e);

            var message = messageSource.getMessage(
                    "file.cannot_save",
                    new Object[]{},
                    Locale.getDefault()
            );
            throw new FileProcessingException(message, e);
        }
    }

    @NotNull
    public Resource getResource(@NotNull @UUID String fileId)
            throws NoSuchElementException, FileProcessingException {
        var file = findById(fileId);
        try {
            var savedPath = Path.of(file.getSaveLocation());
            var realPath = savedPath.getParent().resolve(file.getOriginalName());

            if (Files.notExists(realPath))
                Files.copy(savedPath, realPath, StandardCopyOption.REPLACE_EXISTING);
            return new FileSystemResource(realPath);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);

            var message = messageSource.getMessage(
                    "file.cannot_read_saved_file",
                    new Object[]{fileId},
                    Locale.getDefault()
            );
            throw new FileProcessingException(message, e);
        }
    }

    @NotNull
    public FileMetadata getMetadata(@NotNull @UUID String fileId) throws NoSuchElementException {
        var file = findById(fileId);
        return fileMapper.toResponse(file);
    }

    public void delete(@NotNull @UUID String fileId) throws NoSuchElementException {
        var file = findById(fileId);
        try {
            Files.delete(Paths.get(file.getSaveLocation()));
            repository.delete(file);
        } catch (IOException | InvalidPathException | SecurityException e) {
            log.error(e.getLocalizedMessage(), e);

            var message = messageSource.getMessage(
                    "file.cannot_delete",
                    new Object[]{fileId},
                    Locale.getDefault()
            );
            throw new FileProcessingException(message, e);
        }
    }

    @NotNull
    public File findById(@NotNull @UUID String fileId) throws NoSuchElementException {
        return repository.findById(fileId).orElseThrow(() -> {
            var message = messageSource.getMessage(
                    "file.not_found",
                    new Object[]{fileId},
                    Locale.getDefault()
            );
            return new NoSuchElementException(message);
        });
    }

}
