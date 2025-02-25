package io.github.nhatbangle.sdp.file.service;

import io.github.nhatbangle.sdp.file.entity.User;
import io.github.nhatbangle.sdp.file.exception.ServiceUnavailableException;
import io.github.nhatbangle.sdp.file.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = "sdp_software-user")
public class UserService {

    private final MessageSource messageSource;
    private final UserRepository repository;

    /**
     * Get a {@link User} by id
     *
     * @param userId the id of the user
     * @return the user
     * @throws NoSuchElementException if the user is not found
     */
    public User getById(@NotNull @UUID String userId)
            throws NoSuchElementException {
        return repository.findById(userId).orElseThrow(() -> {
                    var message = messageSource.getMessage(
                            "user.not_found",
                            new Object[]{userId},
                            Locale.getDefault()
                    );
                    return new NoSuchElementException(message);
                }
        );
    }

    /**
     * Validate the user id
     *
     * @param userId the id of the user
     * @throws ServiceUnavailableException if the authentication service is unavailable
     */
    @Cacheable(cacheNames = "sdp_software-user-validate", key = "#userId")
    public boolean validateUserId(@NotNull @UUID String userId) throws ServiceUnavailableException {
        // call to authenticate-service
        return true;
    }

    /**
     * Throw an exception if {@code validateResult} is {@code false}
     *
     * @param userId the id of the user
     * @throws IllegalArgumentException if the user is not found
     */
    public void foundOrElseThrow(@NotNull @UUID String userId, boolean validateResult)
            throws IllegalArgumentException {
        if (!validateResult) {
            var message = messageSource.getMessage(
                    "user.not_found",
                    new Object[]{userId},
                    Locale.getDefault()
            );
            throw new IllegalArgumentException(message);
        }
    }

}
