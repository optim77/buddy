package com.buddy.buddy.follow.repository;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.follow.entity.Follow;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    @Query("SELECT f FROM Follow f WHERE f.follower =: user_id AND f.followedTo =: followed_to")
    Optional<Follow> findByUserAndFollowedTo(@Param("user_id") UUID user_id, @Param("followed_to") UUID followedTo);

    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.follower =:user_id AND f.followedTo =:followedTo")
    void deleteByUserAndFollowedTo(@Param("user_id") UUID user_id, UUID followedTo);

    @Query("SELECT new com.buddy.buddy.account.DTO.GetUserInformationDTO(u) FROM Subscription s JOIN s.subscribedTo u " +
            "WHERE s.subscriber =: user_id AND u.deleted = false AND u.locked = false AND u.active = true ")
    Page<GetUserInformationDTO> findFollowedByUser(@Param("user_id") UUID user_id, Pageable pageable);
}
