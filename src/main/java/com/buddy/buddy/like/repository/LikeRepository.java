package com.buddy.buddy.like.repository;

import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.like.entity.Like;
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
public interface LikeRepository extends JpaRepository<Like, Long> {


    @Query("SELECT true FROM Like l WHERE l.image.id = :image_id AND l.user.id = :user_id")
    Optional<Like> isLikedByUser(@Param("image_id") UUID image_id, @Param("user_id") UUID user_id);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.image.id = :image_id AND l.user.id = :user_id")
    void deleteLikeByUser(@Param("image_id") UUID image_id, @Param("user_id") UUID user_id);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(l.image.id, l.image.url, l.image.blurredUrl, l.image.description, " +
            "l.image.uploadedDate, l.image.likeCount, l.image.open, l.image.user.id, l.image.user.username, " +
            "l.image.user.avatar, l.image.user.createdAt, l.image.mediaType, false)" +
            "FROM Like l WHERE l.user.id = :user_id AND l.user.deleted = false AND l.user.active = true AND l.image.deleted = false")
    Page<ImageWithUserLikeDTO> getLikedImagesByUser(@Param("user_id") UUID user_id, Pageable pageable);
}
