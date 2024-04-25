package org.yplin.project.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@Entity
@Table(name = "friends")
public class FriendsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "friend_id", nullable = false)
    private Long friendId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('pending', 'accepted', 'declined', 'blocked')")
    private StatusEnum status;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Transient
    private String friendEmail;
    @Transient
    private String friendName;
    @Transient
    private String userEmail;
    @Transient
    private String userName;
}
