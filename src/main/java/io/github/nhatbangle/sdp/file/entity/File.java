package io.github.nhatbangle.sdp.file.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "FILE")
@EntityListeners(AuditingEntityListener.class)
public class File implements Serializable {
    @Serial
    private static final long serialVersionUID = 5838347582295320125L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "save_location", nullable = false, length = 1000)
    private String saveLocation;

    @NotBlank
    @Size(max = 100)
    @Column(name = "type", nullable = false, length = 100)
    private String mimeType;

    @NotNull
    @Column(name = "bytes", columnDefinition = "unsigned int not null")
    private Long bytes;

    @CreatedDate
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "USER_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "FOLDER_id", nullable = false)
    private Folder folder;
}