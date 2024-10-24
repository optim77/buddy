package com.buddy.buddy.image.repository;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.entity.Image;
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
public interface ImageRepository extends JpaRepository<Image, UUID> {

    @Query("SELECT i FROM Image i WHERE i.user.id = :userId  AND i.user.active = true AND i.user.deleted = false AND i.user.locked = false and i.deleted = false ORDER BY i.uploadedDate DESC")
    Page<Image> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT i.user.id FROM Image i WHERE i.user = :photoId")
    Optional<UUID> findUserIdByPhotoId(@Param("photoId") UUID photoId);

    @Query("select true from Image i Where i.user.locked = false and i.user.deleted = false and i.user.active = true and  i.id= :photoId")
    Optional<Boolean> isUserBlockByPhotoId(@Param("photoId") UUID photoId);

    @Query("select i.user from Image i where i.id = :photoId")
    Optional<User> findUserByPhotoId(@Param("photoId") UUID photoId);

    @Query("select i from Image i where i.id = :photoId and i.deleted = false and i.user.locked = false and i.user.deleted = false and i.user.active = true")
    Optional<Image> findByIdIfNotBlocked(@Param("photoId") UUID photoId);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, " +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :userId " +
            "WHERE i.id = :imageId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false")
    Optional<ImageWithUserLikeDTO> findImageByIdWithUserAndLikeStatus(@Param("imageId") UUID imageId, @Param("userId") UUID userId);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, false) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "WHERE i.id = :imageId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false")
    Optional<ImageWithUserLikeDTO> findImageByIdWithUserForNotLoggedUser(@Param("imageId") UUID imageId);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, " +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :userId " +
            "WHERE i.user.id = :authorId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false")
    Page<ImageWithUserLikeDTO> findImagesByUserIdWithUserAndLikeStatus(@Param("authorId") UUID authorId, @Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, false) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "WHERE i.user.id = :authorId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false")
    Page<ImageWithUserLikeDTO> findImagesByUserIdForNotLoggedUser(@Param("authorId") UUID authorId, Pageable pageable);

    @Modifying
    @Query("UPDATE Image i SET i.deleted = true WHERE i.id = :imageId")
    void setDeleteImageById(@Param("imageId") UUID imageId);

}
