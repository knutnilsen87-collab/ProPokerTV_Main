package com.propokertv.api.auth.domain;

import com.propokertv.api.common.model.AuditableEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "auth_identity",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_auth_identity_provider_subject", columnNames = {"provider", "provider_subject"})
        },
        indexes = {
                @Index(name = "idx_auth_identity_user_id", columnList = "user_id")
        }
)
public class AuthIdentity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 40)
    private String provider;

    @Column(name = "provider_subject", nullable = false, length = 190)
    private String providerSubject;

    @Column(length = 190)
    private String email;
}
