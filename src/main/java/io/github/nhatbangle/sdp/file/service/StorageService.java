package io.github.nhatbangle.sdp.file.service;

import io.github.nhatbangle.sdp.file.dto.FileMetadata;
import io.github.nhatbangle.sdp.file.entity.File;
import io.github.nhatbangle.sdp.file.entity.Folder;
import io.github.nhatbangle.sdp.file.entity.User;
import io.github.nhatbangle.sdp.file.exception.FileProcessingException;
import io.github.nhatbangle.sdp.file.mapper.FileMapper;
import io.github.nhatbangle.sdp.file.repository.FileRepository;
import io.github.nhatbangle.sdp.file.repository.FolderRepository;
import io.github.nhatbangle.sdp.file.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
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
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final MessageSource messageSource;
    private final FileRepository repository;
    private final FileMapper fileMapper;

    private final Path storageDir;

    @NotNull
    @Transactional
    public String store(
            @NotNull @UUID String userId,
            @NotNull MultipartFile multipartFile
    ) throws FileProcessingException, IllegalArgumentException {
        var user = userRepository.findById(userId).orElseGet(() -> {
            var result = userService.validateUserId(userId);
            userService.foundOrElseThrow(userId, result);
            return User.builder().id(userId).build();
        });
        var folder = folderRepository.findByUser_Id(userId)
                .orElse(Folder.builder()
                        .name(userId)
                        .user(user)
                        .build());
        folder.setBytes(folder.getBytes() + multipartFile.getSize());

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
            if (!Files.isDirectory(dir)) Files.createDirectory(dir);
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
            throw new FileProcessingException(message);
        }
    }

    @NotNull
    public Resource getResource(@NotNull @UUID String fileId)
            throws NoSuchElementException, FileProcessingException {
        var file = findById(fileId);
        return new FileSystemResource(file.getSaveLocation());
//        try {
//            return UrlResource.from(file.getSaveLocation());
//        } catch (UncheckedIOException e) {
//            log.error(e.getLocalizedMessage(), e);
//
//            var message = messageSource.getMessage(
//                    "file.cannot_read_saved_file",
//                    new Object[]{fileId},
//                    Locale.getDefault()
//            );
//            throw new FileProcessingException(message);
//        }
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
            throw new FileProcessingException(message);
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
