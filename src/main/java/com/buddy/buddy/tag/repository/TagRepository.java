package com.buddy.buddy.tag.repository;

import com.buddy.buddy.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    @Query("select t from Tag t ORDER BY RANDOM()")
    Page<Tag> findAllOrderByName(Pageable pageable);

    @Query("select t from Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tag_name, '%')) ORDER BY t.name")
    Optional<Tag> findByNameContainingIgnoreCase(@Param("tag_name") String tag_name);

    @Query("select t from Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tag_name, '%')) ORDER BY t.name")
    Page<Tag> findByNameContainingIgnoreCaseToAdd(@Param("tag_name") String tag_name, Pageable pageable);
}
