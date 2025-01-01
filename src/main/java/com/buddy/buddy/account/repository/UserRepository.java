package com.buddy.buddy.account.repository;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.DTO.ProfileInformationDTO;
import com.buddy.buddy.account.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsernameOrEmail(String username, String email);
    User findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "AND u.deleted = false " +
            "AND u.locked = false " +
            "AND u.active = true " +
            "ORDER BY u.subscribersCount DESC")
    Page<User> findByUsernameContainingIgnoreCaseOrderBySubscribersCount(@Param("search") String search, Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.locked = false  AND u.active = true ORDER BY RANDOM()")
    Page<GetUserInformationDTO> findAllRandom(Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.locked = false  AND u.active = true ORDER BY u.subscribersCount DESC")
    Page<GetUserInformationDTO> findAllByPopularity(Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.locked = false  AND u.active = true ORDER BY u.createdAt DESC ")
    Page<GetUserInformationDTO> findAllByCreatedAt(Pageable pageable);
    @Query("SELECT true FROM User u WHERE u.deleted = false AND u.locked = false AND u.active = true AND u.id = :id")
    boolean userIsActiveById(@Param("id") UUID id);

    @Query("SELECT new com.buddy.buddy.account.DTO.ProfileInformationDTO(u.id, u.email, u.username, u.description, u.age, u.avatar, u.active, u.locked, u.posts) FROM User u WHERE u.id = :user_id")
    ProfileInformationDTO findProfileInformationById(@Param("user_id") UUID user_id);
}
