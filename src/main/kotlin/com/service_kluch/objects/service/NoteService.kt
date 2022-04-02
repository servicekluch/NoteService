package com.service_kluch

object NoteService {
    private val notesList = mutableListOf<Note>()
    private val commentsList = mutableListOf<Comment>()
    private val deletedCommentsList = mutableListOf<Int>()

    fun add(note: Note): Note {

        val newNote = note.copy(id = notesList.lastIndex + 1)

        notesList += newNote

        return newNote
    }

    fun createComment(comment: Comment, noteId: Int): Comment {

        val note: Note = getById(noteId)
        val noteComments = if (note.comments.isEmpty()) mutableListOf() else note.comments as MutableList<Int>
        val newComment = comment.copy(id = commentsList.lastIndex + 1)

        commentsList += newComment
        noteComments += newComment.id
        update(note.copy(comments = noteComments))

        return newComment
    }

    fun get(
        noteIds: List<Int> = emptyList(),
        userId: Int = 0,
        offset: Int = 0,
        count: Int = 7,
    ): List<Note> {

        val userNotes = mutableListOf<Note>()

        for (note in notesList) {
            if (note.ownerId == userId) userNotes += note
        }

        userNotes.sortByDescending { it.date }

        val userNotesByIds = if (noteIds.isNotEmpty()) {
            getNotesByIds(noteIds = noteIds, noteList = userNotes)
        } else userNotes

        if (userNotesByIds.isEmpty()) throw NoteNotFoundException("Заметки не существует")

        val size = userNotesByIds.size
        val rangedOffset = subtractionWithMaxValue(int = offset, max = size)

        return if (userNotesByIds.size < count) userNotesByIds else {
            if (rangedOffset <= size - count) {
                userNotesByIds.subList(fromIndex = rangedOffset, toIndex = count + rangedOffset)
            } else {
                userNotesByIds.subList(0, rangedOffset + count - size) + userNotesByIds.subList(rangedOffset, size)
            }
        }
    }

    private fun getNotesByIds(noteIds: List<Int>, noteList: MutableList<Note>): MutableList<Note> {

        val tempNotesList= mutableListOf<Note>()

        for (note in noteList) {
            for (id in noteIds) {
                if (note.id == id) tempNotesList += note
            }
        }

        if (tempNotesList.isNotEmpty()) return tempNotesList
        throw throw NoteNotFoundException("Заметки не существует")
    }

    fun getById(
        noteId: Int,
        ownerId: Int = 0,
    ): Note {

        for (note: Note in notesList)
            if (note.id == noteId && note.ownerId == ownerId) return note
        throw NoteNotFoundException("Заметки не существует")
    }

    fun getComments(
        noteId: Int,
        ownerId: Int = 0,
        offset: Int = 0,
        count: Int = 7
    ): List<Comment> {

        val note = getById(noteId)
        val userComments = userComments(ownerId)
        val noteComments = mutableListOf<Comment>()

        for (comment in userComments)
            for (id in note.comments)
                if (comment.id == id) noteComments += comment

        noteComments.sortByDescending { it.date }

        val deletedNoteComments = mutableListOf<Comment>()

        for (comment in noteComments)
            for (deletedCommentId in deletedCommentsList)
                if (comment.id == deletedCommentId) deletedNoteComments += comment

        noteComments.removeAll(deletedNoteComments)

        if (noteComments.isEmpty()) throw CommentNotFoundException("Комментарий не существует")

        val size = noteComments.size
        val rangedOffset = subtractionWithMaxValue(int = offset, max = size)

        return listOffset(
            count = count,
            offset = rangedOffset,
            list = noteComments
        )
    }

    fun update(note: Note): Boolean {

        for ((index, updatingNote) in notesList.withIndex()) {
            if (index == note.id) {
                notesList[index] = note.copy(
                    id = updatingNote.id,
                    ownerId = updatingNote.ownerId,
                    date = updatingNote.date
                )
                return true
            }
        }
        throw NoteNotFoundException("Заметки не существует")
    }

    fun updateComment(comment: Comment): Boolean {

        val newComments = mutableListOf<Comment>()

        for ((index, updatingComment) in commentsList.withIndex())
            newComments += if (index == comment.id) {
                comment.copy(
                    id = updatingComment.id,
                    fromId = updatingComment.fromId,
                    date = updatingComment.date
                )
            } else commentsList[index]

        if (newComments == commentsList) throw CommentNotFoundException("Комментарий не существует")

        val userComments = userComments(comment.fromId)

        for (userComment in userComments)
            if (userComment.id == comment.id) {
                commentsList.clear()
                commentsList += newComments
                return true
            }
        throw throw CommentNotFoundException("Комментарий не этого автора")
    }

    private fun subtractionWithMaxValue(int: Int, max: Int): Int {
        return if (int > max) {
            subtractionWithMaxValue(int = int - max, max = max)
        } else int
    }

    private fun <E> listOffset(count: Int, offset: Int, list: List<E>): List<E> {
        return if (list.size < count) list else {
            if (offset <= list.size - count) {
                list.subList(fromIndex = offset, toIndex = count + offset)
            } else {
                list.subList(0, offset + count - list.size) +
                        list.subList(offset, list.size)
            }
        }
    }

    private fun userComments(userId: Int): MutableList<Comment> {

        val userComments = mutableListOf<Comment>()

        for (comment in commentsList) if (comment.fromId == userId) userComments += comment
        if (userComments.isNotEmpty()) return userComments
        throw CommentNotFoundException("Комментарий автора не найден")
    }

    fun delete(noteId: Int): Boolean {

        val note = getById(noteId = noteId)

        for (commentId in note.comments) {
            for (deletedCommentId in deletedCommentsList)
                if (deletedCommentId == commentId) deletedCommentsList.remove(commentId)
            for (comment in commentsList) if (comment.id == commentId) commentsList.remove(comment)
        }

        notesList.remove(note)

        return true
    }

    fun deleteComment(commentId: Int, ownerId: Int = 0): Boolean {

        val userComments = userComments(ownerId)

        for (comment in userComments) if (comment.id == commentId) {
            deletedCommentsList += commentId
            return true
        }
        throw CommentNotFoundException("Комментарий не существует")
    }

    fun restoreComment(commentId: Int, ownerId: Int = 0): Boolean {

        val userComments = userComments(ownerId)

        for (comment in commentsList) {
            if (comment.id == commentId) {
                for (userComment in userComments) {
                    if (userComment.id == commentId) {
                        for (id in deletedCommentsList) {
                            if (id == commentId) {
                                deletedCommentsList.remove(commentId)
                                return true
                            }
                        }
                        throw CommentNotFoundException("Комментарий не удален")
                    }
                }
                throw CommentNotFoundException("Комментарий не этого автора")
            }
        }
        throw CommentNotFoundException("Комментарий автора не найден")
    }

    fun clear() {
        notesList.clear()
        commentsList.clear()
        deletedCommentsList.clear()
    }
}