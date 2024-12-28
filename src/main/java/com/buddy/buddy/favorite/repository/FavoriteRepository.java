package com.buddy.buddy.favorite.repository;

import com.buddy.buddy.favorite.entity.Favorite;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    Optional<Favorite> findByid(UUID uuid);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(f.image.id, f.image.url, f.image.description, f.image.uploadedDate, " +
            "f.image.likeCount, f.image.open, u.id, u.username, u.avatar, u.createdAt, f.image.mediaType, " +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Favorite f " +
            "JOIN f.image.user u " +
            "LEFT JOIN Like l ON l.image.id = f.image.id AND l.user.id = :userId " +
            "WHERE u.locked = false AND u.deleted = false AND u.active = true AND f.image.deleted = false ORDER BY f.image.uploadedDate DESC")
    Page<ImageWithUserLikeDTO> getUserFavorite(@Param("userId") UUID userId, Pageable pageable);
}
