package org.yplin.project.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.UserOwnWorkspaceDetailsModel;

import java.util.List;

@Repository
public interface UserOwnWorkspaceDetailsRepository extends JpaRepository<UserOwnWorkspaceDetailsModel, Long> {
    @Query(value = "SELECT ui.email, us.user_id, us.workspace_id, w.workspace_name, w.workspace_description, w.workspace_owner " +
            "FROM user_workspace us " +
            "INNER JOIN workspace w ON us.workspace_id = w.id " +
            "INNER JOIN user_information ui ON us.user_id = ui.id " +
            "WHERE ui.email = :userEmail", nativeQuery = true)
    List<UserOwnWorkspaceDetailsModel> fetchWorkspaceDetailsWithNativeQuery(@Param("userEmail") String userEmail);
}
