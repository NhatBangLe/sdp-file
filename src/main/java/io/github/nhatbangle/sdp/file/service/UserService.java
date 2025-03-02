package io.github.nhatbangle.sdp.file.service;

import io.github.nhatbangle.sdp.file.entity.User;
import io.github.nhatbangle.sdp.file.exception.ServiceUnavailableException;
import io.github.nhatbangle.sdp.file.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final MessageSource messageSource;
    private final UserRepository repository;

    @Transactional
    public synchronized User getById(@NotNull @UUID String userId)
            throws IllegalArgumentException, ServiceUnavailableException {
        var optional = repository.findById(userId);
        if (optional.isPresent()) return optional.get();

        var validateResult = validateUserId(userId);
        foundOrElseThrow(userId, validateResult);
        return repository.save(User.builder().id(userId).build());
    }

    /**
     * Validate the user id
     *
     * @param userId the id of the user
     * @throws ServiceUnavailableException if the authentication service is unavailable
     */
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
