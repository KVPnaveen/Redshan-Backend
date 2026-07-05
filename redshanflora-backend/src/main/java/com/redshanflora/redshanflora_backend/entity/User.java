package com.redshanflora.redshanflora_backend.entity;

import com.redshanflora.redshanflora_backend.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "\"users\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "registered_date", nullable = false, updatable = false)
    private Instant registeredDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Customer customer;

    @PrePersist
    protected void onCreate() {
        registeredDate = Instant.now();
    }

    public String getFirstName() {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        int spaceIndex = name.indexOf(' ');
        return spaceIndex == -1 ? name : name.substring(0, spaceIndex);
    }

    public String getLastName() {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        int spaceIndex = name.indexOf(' ');
        return spaceIndex == -1 ? "" : name.substring(spaceIndex + 1);
    }

    public String getAddress() {
        return customer != null ? customer.getAddress() : null;
    }
}
