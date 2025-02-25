package io.github.nhatbangle.sdp.file.repository;

import io.github.nhatbangle.sdp.file.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserRepository extends JpaRepository<User, String> {
}