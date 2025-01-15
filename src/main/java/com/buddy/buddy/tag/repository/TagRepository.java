package com.buddy.buddy.tag.repository;

import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    @Query("SELECT t FROM Tag t WHERE t.count > 0 ORDER BY RANDOM()")
    Page<Tag> findAllOrderByName(Pageable pageable);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tag_name, '%')) ORDER BY t.name")
    Optional<Tag> findByNameContainingIgnoreCase(@Param("tag_name") String tag_name);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tag_name, '%')) ORDER BY t.name")
    Page<Tag> findByNameContainingIgnoreCaseToAdd(@Param("tag_name") String tag_name, Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(" +
            "i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, " +
            "u.id, u.username, u.avatar, u.createdAt, i.mediaType, " +
            "CASE WHEN (l IS NOT NULL) THEN true ELSE false END) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "LEFT JOIN Like l ON l.image.id = i.id AND l.user.id = :user_id " +
            "JOIN i.tags t " +
            "WHERE t.name = :tag_name " +
            "AND u.locked = false AND u.deleted = false AND u.active = true " +
            "AND i.deleted = false AND i.open = true " +
            "ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenRandomMediaByTagForLoggedUser(
            @Param("tag_name") String tag_name,
            @Param("user_id") UUID user_id,
            Pageable pageable);

    @Query("SELECT new com.buddy.buddy.image.DTO.ImageWithUserLikeDTO(" +
            "i.id, i.url, i.description, i.uploadedDate, i.likeCount, i.open, " +
            "u.id, u.username, u.avatar, u.createdAt, i.mediaType, " +
            "false) " +
            "FROM Image i " +
            "JOIN i.user u " +
            "JOIN i.tags t " +
            "WHERE t.name = :tag_name " +
            "AND u.locked = false AND u.deleted = false AND u.active = true " +
            "AND i.deleted = false AND i.open = true " +
            "ORDER BY RANDOM()")
    Page<ImageWithUserLikeDTO> findOpenRandomMediaByTagForNotLoggedUser(
            @Param("tag_name") String tag_name,
            Pageable pageable);

    @Query("SELECT i.id FROM Image i JOIN i.tags t WHERE t.name = :tag_name ORDER BY i.likeCount DESC LIMIT 3")
    List<String> getMediaForTagIcons(@Param("tag_name") String tag_name);

    Optional<Tag> findByName(String name);

//    // To change
//    @Query("SELECT i FROM Image i JOIN i.tags t WHERE t.name = :tagName")
//    Page<Image> findImagesByTagName(@Param("tagName") String tagName);
}
