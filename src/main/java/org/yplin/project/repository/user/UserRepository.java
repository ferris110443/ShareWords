package org.yplin.project.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.UserModel;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query
    UserModel findUserByEmail(@Param("email") String email);

    @Query
    UserModel findIdByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserModel u WHERE u.name LIKE %:name%")
    List<UserModel> findByNameContaining(@Param("name") String name);
}
