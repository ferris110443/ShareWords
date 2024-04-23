package org.yplin.project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "workspace")
@Data
@NoArgsConstructor
public class WorkspaceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "workspace_name", columnDefinition = "varchar(255)", nullable = false)
    private String workspaceName;

    @Column(name = "workspace_description", columnDefinition = "varchar(255)", nullable = false)
    private String workspaceDescription;

    @Column(name = "workspace_owner", columnDefinition = "varchar(255)", nullable = false)
    private String workspaceOwner;

    @Column(name = "workspace_created_at", nullable = false)
    private Timestamp workspaceCreatedAt;

}
