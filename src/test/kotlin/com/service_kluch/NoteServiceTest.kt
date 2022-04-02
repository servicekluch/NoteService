package com.service_kluch

import org.junit.Test

import org.junit.Assert.*

class MainKtTest {

    @Test
    fun main_note_delete() {
        val notesService = NoteService
        notesService.clear()

        notesService.add(Note())
        val deleteNoteId = notesService.add(Note()).id
        notesService.add(Note())

        assertTrue(notesService.delete(deleteNoteId))
    }

    @Test
    fun main_noteComment_getComments_success(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        val comments = mutableListOf<Comment>()
        val comment1 = notesService.createComment(comment = Comment(text = "Комментарий раз"), noteId = noteId)
        val comment2 = notesService.createComment(comment = Comment(text = "Комментарий два"), noteId = noteId)

        comments += comment1
        comments += comment2

        assertEquals(comments, notesService.getComments(noteId = noteId))
    }

    @Test
    fun main_note_getById_success() {
        val notesService = NoteService

        notesService.clear()
        notesService.add(Note())

        val getNote = notesService.add(Note(text = "Hello Kotlin"))
        notesService .add(Note())

        assertEquals(getNote, notesService.getById(getNote.id))
    }

    @Test
    fun main_note_update_success() {
        val notesService = NoteService
        notesService.clear()

        notesService.add(Note())
        val updateNote = notesService.add(Note(text = "Hello Kotlin"))
        val newNote = updateNote.copy(text = "Hello Kotlin 2022")

        assertTrue(notesService.update(newNote))
    }

    @Test
    fun main_noteComment_updateComment_success(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        notesService.createComment(comment = Comment(), noteId = noteId)
        val updateComment = notesService.createComment(comment = Comment(), noteId = noteId)
        notesService.createComment(comment = Comment(), noteId = noteId)
        val newComment = updateComment.copy(text = "Hello Kotlin 2022")

        assertTrue(notesService.updateComment(newComment))
    }

    @Test
    fun main_noteComment_deleteComment_success(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        val commentId = notesService.createComment(comment = Comment(), noteId = noteId).id

        assertTrue(notesService.deleteComment(commentId = commentId))
    }

    @Test
    fun main_noteComment_restoreComment_success(){
        val notesService= NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        val commentId = notesService.createComment(comment = Comment(), noteId = noteId).id
        notesService.deleteComment(commentId = commentId)

        assertTrue(notesService.restoreComment(commentId = commentId))
    }

    /**
     * Заметка не найдена
     */
    @Test(expected = NoteNotFoundException::class)
    fun main_noteNotFoundException() {
        val notesService = NoteService
        notesService.clear()

        notesService.getById(Int.MIN_VALUE)
    }

    /**
     * Заметка не найдена
     */
    @Test(expected = NoteNotFoundException::class)
    fun main_note_update_NotFoundException() {
        val notesService = NoteService
        notesService.clear()

        notesService.update(Note())
    }

    /**
     * Комментарий автора не найден
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_getComments_commentOwnerNotFound(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        notesService.createComment(comment = Comment(), noteId = noteId)
        notesService.getComments(noteId = noteId, ownerId = Int.MIN_VALUE)
    }

    /**
     * Комментарий не существует
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_getComments_commentNotFound(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        val commentId = notesService.createComment(comment = Comment(), noteId = noteId).id
        notesService.deleteComment(commentId = commentId)
        notesService.getComments(noteId = noteId)
    }

    /**
     * Комментарий не существует
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_updateComment_commentNotFound(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        notesService.createComment(comment = Comment(), noteId = noteId)
        notesService.createComment(comment = Comment(), noteId = noteId)
        notesService.updateComment(Comment(id = Int.MIN_VALUE))
    }

    /**
     * Комментарий не этого автора
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_updateComment_commentNotBelongToOwner(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        notesService.createComment(comment = Comment(), noteId = noteId)
        val updatingComment = notesService.createComment(comment = Comment(fromId = 1), noteId = noteId)
        notesService.createComment(comment = Comment(), noteId = noteId)
        val newComment = updatingComment.copy(text = "Hello Kotlin", fromId = 0)
        notesService.updateComment(newComment)
    }

    /**
     * Комментарий не существует
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_deleteComment_commentNotFound(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        notesService.createComment(comment = Comment(), noteId = noteId)
        notesService.deleteComment(commentId = Int.MIN_VALUE)
    }

    /**
     * Комментарий не существует
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_restoreComment_commentNotFound(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        notesService.createComment(comment = Comment(), noteId = noteId)
        notesService.restoreComment(commentId = Int.MIN_VALUE)
    }

    /**
     * Комментарий не этого автора
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_restoreComment_commentNotBelongToOwner(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        val ownerId = notesService.createComment(comment = Comment(), noteId = noteId).fromId
        val restoringCommentId = notesService.createComment(comment = Comment(fromId = ownerId + 1), noteId = noteId).id
        notesService.restoreComment(commentId = restoringCommentId, ownerId = ownerId)
    }

    /**
     * Комментарий не удален
     */
    @Test(expected = CommentNotFoundException::class)
    fun main_noteComment_restoreComment_commentNotDeleted(){
        val notesService = NoteService
        notesService.clear()

        val noteId = notesService.add(Note()).id
        val commentId = notesService.createComment(comment = Comment(), noteId = noteId).id
        notesService.restoreComment(commentId = commentId)
    }
}