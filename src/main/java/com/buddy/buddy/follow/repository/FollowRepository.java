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

    @Query("SELECT f FROM Follow f WHERE f.follower.id = :user_id AND f.followedTo.id = :followed_to")
    Optional<Follow> findByUserAndFollowedTo(@Param("user_id") UUID user_id, @Param("followed_to") UUID followed_to);

    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.follower.id = :user_id AND f.followedTo.id = :followed_to")
    void deleteByUserAndFollowedTo(@Param("user_id") UUID user_id, @Param("followed_to") UUID followed_to);

    @Query("SELECT new com.buddy.buddy.account.DTO.GetUserInformationDTO(f.follower) FROM Follow f " +
            "WHERE f.followedTo.id = :user_id AND f.follower.deleted = false AND f.follower.locked = false AND f.follower.active = true ")
    Page<GetUserInformationDTO> findFollowersForUser(@Param("user_id") UUID user_id, Pageable pageable);

    @Query("SELECT new com.buddy.buddy.account.DTO.GetUserInformationDTO(f.followedTo) FROM Follow f " +
            "WHERE f.follower.id = :user_id AND f.followedTo.deleted = false")
    Page<GetUserInformationDTO> findFollowingForUser(@Param("user_id") UUID user_id, Pageable pageable);
}
