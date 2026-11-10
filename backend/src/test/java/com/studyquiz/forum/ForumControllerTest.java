package com.studyquiz.forum;

import com.studyquiz.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForumControllerTest {
    private ForumThreadRepository threads;
    private ForumPostRepository posts;
    private CurrentUser current;
    private ForumController controller;
    private User author, outsider, admin;
    private ForumThread thread;

    @BeforeEach void setup() {
        threads = mock(ForumThreadRepository.class);
        posts = mock(ForumPostRepository.class);
        current = mock(CurrentUser.class);
        controller = new ForumController(threads, posts, current);
        author = new User("author", "author@test.fr", "hash"); author.id = 1L;
        outsider = new User("outsider", "outsider@test.fr", "hash"); outsider.id = 2L;
        admin = new User("admin", "admin@test.fr", "hash"); admin.id = 3L; admin.role = UserRole.admin;
        thread = new ForumThread(author, "Titre", "Texte"); thread.id = 50L;
        when(threads.findById(50L)).thenReturn(Optional.of(thread));
    }

    @Test void anotherUserCannotUpdateThread() {
        when(current.require(2L)).thenReturn(outsider);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                controller.update(50L, new ForumController.CreateThread("Nouveau", "Texte"), 2L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(threads, never()).save(any());
    }

    @Test void anotherUserCannotDeleteThread() {
        when(current.require(2L)).thenReturn(outsider);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.delete(50L, 2L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(threads, never()).delete(any());
    }

    @Test void authorCanUpdateThread() {
        when(current.require(1L)).thenReturn(author);
        when(threads.save(any(ForumThread.class))).thenAnswer(inv -> inv.getArgument(0));
        ForumController.ThreadView updated = controller.update(50L, new ForumController.CreateThread("Nouveau", "Texte mis à jour"), 1L);
        assertEquals("Nouveau", updated.title());
        verify(threads).save(thread);
    }

    @Test void adminCanDeleteThread() {
        when(current.require(3L)).thenReturn(admin);
        assertEquals(HttpStatus.NO_CONTENT, controller.delete(50L, 3L).getStatusCode());
        verify(threads).delete(thread);
    }

    @Test void unauthenticatedCreationReturns401() {
        when(current.require(null)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                controller.create(new ForumController.CreateThread("Titre", "Texte"), null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(threads, never()).save(any());
    }
}
