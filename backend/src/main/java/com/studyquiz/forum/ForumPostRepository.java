package com.studyquiz.forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
public interface ForumPostRepository extends JpaRepository<ForumPost,Long> {
    Page<ForumPost> findByThreadId(Long threadId,Pageable pageable);
}
