package org.yplin.project.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.service.WorkspaceFileContentProjection;

import java.util.List;

@Repository
public interface FileContentRepository extends JpaRepository<FileContentModel, Long> {


    @Query(value = "SELECT " +
            "fc.file_title AS fileTitle, " +
            "fc.content AS content, " +
            "fc.file_url AS fileURL, " +
            "fc.file_id AS fileId, " +
            "w.workspace_name AS workspaceName, " +
            "w.workspace_description AS workspaceDescription, " +
            "w.workspace_owner AS workspaceOwner " +
            "FROM workspace w " +
            "INNER JOIN file_content fc ON w.id = fc.workspace_id " +
            "WHERE w.workspace_name = :workspaceName",
            nativeQuery = true)
    List<WorkspaceFileContentProjection> getWorkspaceFilesContent(@Param("workspaceName") String workspaceName);

    FileContentModel findByFileId(String fileId);

    List<FileContentModel> findByWorkspaceId(long workspaceId);


    @Transactional
    void deleteByFileId(String fileId);
}


