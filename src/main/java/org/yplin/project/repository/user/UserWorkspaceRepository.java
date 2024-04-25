package org.yplin.project.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.UserWorkspaceModel;

import java.util.Optional;

@Repository
public interface UserWorkspaceRepository extends JpaRepository<UserWorkspaceModel, Long> {
    Optional<UserWorkspaceModel> findByUserIdAndWorkspaceId(long userId, long workspaceId);
}
