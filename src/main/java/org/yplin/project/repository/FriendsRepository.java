package org.yplin.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yplin.project.model.FriendsModel;

import java.util.List;


@Repository
public interface FriendsRepository extends JpaRepository<FriendsModel, Long> {

    @Query("SELECT COUNT(f) > 0 FROM FriendsModel f WHERE " +
            "(f.userId = :userId AND f.friendId = :friendId) OR " +
            "(f.userId = :friendId AND f.friendId = :userId)")
    boolean existsFriendship(Long userId, Long friendId);


    @Query("SELECT f FROM FriendsModel f WHERE " +
            "(f.userId = :userId " +
            ") OR " +
            "(f.friendId = :userId )")
    List<FriendsModel> fetchByUserId(long userId);
}
