package org.yplin.project.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.FileContentModel;

@Repository
public interface FileContentRepository extends JpaRepository<FileContentModel, Long> {


    @Modifying
    @Transactional
    @Query("UPDATE FileContentModel f SET f.content = :content, f.fileTitle = :fileTitle, f.fileURL = :fileURL WHERE f.fileId = :fileId")
    void updateFileContent(@Param("fileId") String fileId, @Param("content") String content, @Param("fileTitle") String fileTitle, @Param("fileURL") String fileURL);
}
