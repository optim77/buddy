package com.buddy.buddy.session.repository;

import com.buddy.buddy.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT CASE WHEN s.id != null THEN true ELSE false END FROM Session s WHERE s.session = :session_id")
    boolean existsBySessionId(@Param("session_id") UUID session_id);

    @Query("DELETE FROM Session s WHERE s.user.id = :user_id")
    void deleteAllByUserId(@Param("user_id") UUID user_id);

}
