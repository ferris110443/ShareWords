package org.yplin.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.FileContent;

@Repository
public interface FileContentRepository extends JpaRepository<FileContent, Long>{

}
