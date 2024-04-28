package org.yplin.project.repository;


import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.WorkspaceModel;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceModel, Long> {

    @Query("SELECT w.id FROM WorkspaceModel w WHERE w.workspaceName = :workspaceName")
    Long findIdByWorkspaceName(String workspaceName);

    @Query("SELECT COUNT(w) > 0 FROM WorkspaceModel w WHERE w.workspaceName = :workspaceName AND w.workspaceOwner = :workspaceOwner")
    boolean isUserOwnerOfWorkspace(@Param("workspaceName") String workspaceName, @Param("workspaceOwner") String workspaceOwner);

    @Modifying
    @Transactional
    @Query("DELETE FROM WorkspaceModel w WHERE w.workspaceName = :workspaceName AND w.workspaceOwner = :workspaceOwner")
    void deleteByWorkspaceNameAndWorkspaceOwner(@Param("workspaceName") String workspaceName, @Param("workspaceOwner") String workspaceOwner);
}
