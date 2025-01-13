package com.buddy.buddy.image.repository;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.tag.entity.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {


    @Query("select i.user from Image i where i.id = :photoId")
    Optional<User> findUserByPhotoId(@Param("photoId") UUID photoId);


    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType," +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END)" +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :userId " +
            "WHERE i.id = :imageId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false")
    Optional<ImageWithUserLikeDTO> findImageByIdWithUserAndLikeStatus(@Param("imageId") UUID imageId, @Param("userId") UUID userId);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType, false) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "WHERE i.id = :imageId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false AND i.open = true")
    Optional<ImageWithUserLikeDTO> findImageByIdWithUserForNotLoggedUser(@Param("imageId") UUID imageId);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType, " +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :userId " +
            "WHERE i.user.id = :authorId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false ORDER BY i.uploadedDate DESC")
    Page<ImageWithUserLikeDTO> findImagesByUserIdWithUserAndLikeStatus(@Param("authorId") UUID authorId, @Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType, false) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "WHERE i.user.id = :authorId AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false AND i.open = true")
    Page<ImageWithUserLikeDTO> findImagesByUserIdForNotLoggedUser(@Param("authorId") UUID authorId, Pageable pageable);

    @Modifying
    @Query("UPDATE Image i SET i.deleted = true WHERE i.id = :imageId")
    void setDeleteImageById(@Param("imageId") UUID imageId);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, i.user.id, i.user.username, i.user.avatar, i.user.createdAt, i.mediaType, false) " +
            "FROM Image i " +
            "JOIN i.tags t  " +
            "WHERE i.user.locked = false AND i.user.deleted = false AND i.user.active = true AND i.deleted = false AND t.name = :tag AND i.open = true ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenImagesByTagNotLoggedUser(@Param("tag") String tag, Pageable pageable);


    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType," +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "JOIN i.tags t  " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :user_id " +
            "WHERE i.user.id = :user_id AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false AND t.name = :tag AND i.open = true ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenImagesByTagLoggedUser(@Param("tag") String tag, @Param("user_id") UUID user_id, Pageable pageable);


    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType," +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :user_id " +
            "WHERE u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false AND i.open = true ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenImagesByRandomLoggedUser(@Param("user_id") UUID user_id, Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, i.user.id, i.user.username, i.user.avatar, i.user.createdAt, i.mediaType, false) " +
            "FROM Image i " +
            "WHERE i.user.locked = false AND i.user.deleted = false AND i.user.active = true AND i.deleted = false AND i.open = true ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenImagesByRandomNotLoggedUser(Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, u.id, u.username, u.avatar, u.createdAt, i.mediaType," +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :user_id " +
            "WHERE i.user.id = :user_id AND i.mediaType = 'VIDEO' AND u.locked = false AND u.deleted = false AND u.active = true AND i.deleted = false AND i.open = true ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenVideosByRandomLoggedUser (@Param("user_id") UUID user_id, Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, i.user.id, i.user.username, i.user.avatar, i.user.createdAt, i.mediaType, false) " +
            "FROM Image i " +
            "WHERE i.user.locked = false AND i.mediaType = 'VIDEO' AND i.user.deleted = false AND i.user.active = true AND i.deleted = false AND i.open = true ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenVideosByRandomNotLoggedUser(Pageable pageable);

    @Query("SELECT t.name FROM Image i JOIN i.tags t WHERE i.id = :image_id")
    Set<String> findTagsByImageId(@Param("image_id") UUID image_id);

    @Modifying
    @Transactional
    @Query("UPDATE Image i SET i.likeCount = i.likeCount + 1 WHERE i.id = :image_id")
    void incrementLikesCount(@Param("image_id") UUID image_id);

    @Modifying
    @Transactional
    @Query("UPDATE Image i SET i.likeCount = i.likeCount - 1 WHERE i.id = :image_id")
    void decrementLikesCount(@Param("image_id") UUID image_id);


}
