package io.github.nhatbangle.sdp.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.io.Serial;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USER")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -7091955555593642242L;

    @Id
    @UUID
    @Column(name = "id", nullable = false, length = 36)
    private String id;
}