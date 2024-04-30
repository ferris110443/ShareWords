package org.yplin.project.repository.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.UserModel;
import org.yplin.project.service.impl.UserEmailNameProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {


    @Query("SELECT new org.yplin.project.service.impl.UserEmailNameProjection(u.email, u.name) FROM UserModel u WHERE u.id = :id")
    UserEmailNameProjection findEmailById(Long id);

    @Query
    UserModel findUserByEmail(@Param("email") String email);

    @Query
    UserModel findIdByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserModel u WHERE u.name LIKE %:name%")
    List<UserModel> findByNameContaining(@Param("name") String name);

    Optional<UserModel> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserModel u SET u.userImageUrl = :fileURL WHERE u.id = :userId")
    void updateUserImageURL(@Param("fileURL") String fileURL, @Param("userId") long userId);
}
