package com.buddy.buddy.like.repository;

import com.buddy.buddy.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select true from Like l where l.user.id= :userId and l.image.id = :imageId")
    Optional<Boolean> findByUserAndImage(@Param("userId") UUID userId, @Param("imageId") UUID imageId);
}
