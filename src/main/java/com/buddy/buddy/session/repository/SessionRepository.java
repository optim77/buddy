package com.buddy.buddy.session.repository;

import com.buddy.buddy.session.DTO.GetSessionDTO;
import com.buddy.buddy.session.DTO.SessionLogoutRequestDTO;
import com.buddy.buddy.session.entity.Session;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT new com.buddy.buddy.session.DTO.GetSessionDTO(s.id, s.session, s.user.id, s.startTime, s.endTime, s.ip, s.agent, s.country) FROM Session s WHERE s.user.id = :user_id")
    Page<GetSessionDTO> getSessions(@Param("user_id") UUID user_id, Pageable pageable);

    @Query("SELECT CASE WHEN s.id != null THEN true ELSE false END FROM Session s WHERE s.session = :session_id")
    boolean existsBySessionId(@Param("session_id") UUID session_id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Session s WHERE s.user.id = :user_id AND s.id = :session_id AND s.session = :session_token")
    void deleteOneByUserId(@Param("user_id") UUID user_id, @Param("session_id") UUID session_id, @Param("session_token") String session_token);

    @Modifying
    @Transactional
    @Query("DELETE FROM Session s WHERE s.user.id = :user_id")
    void deleteAllByUserId(@Param("user_id") UUID user_id);

    @Query("SELECT s.id, s FROM Session s WHERE s.user.id = :user_id")
    List<Session> getAllByUserId(@Param("user_id") UUID user_id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Session s WHERE s.endTime < CURRENT_TIMESTAMP")
    int deleteOldSessions();

    @Query("SELECT CASE WHEN (s IS NOT NULL) THEN true ELSE false END FROM Session s WHERE s.session = :session_id AND s.user.id = :user_id")
    boolean checkSession(@Param("session_id") String session_id, @Param("user_id") UUID user_id);
}
