package org.yplin.project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "user_workspace")
@NoArgsConstructor
@Entity
public class UserWorkspaceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "workspace_id", nullable = false)
    private long workspaceId;

}
