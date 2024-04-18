package org.yplin.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.WorkspaceModel;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceModel, Long> {

}
