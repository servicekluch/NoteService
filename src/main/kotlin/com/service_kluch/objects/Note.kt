package com.service_kluch

/**
 * Объект, описывающий заметку
 */
data class Note(
    /**
     * Идентификатор заметки
     */
    var id: Int = 0,
    /**
     * Идентификатор владельца заметки
     */
    val ownerId: Int = 0,
    /**
     * Заголовок заметки
     */
    var title: String = "",
    /**
     * Текст заметки
     */
    var text: String = "text",
    /**
     * Дата создания заметки в формате Unixtime.
     */
    val date: Int = 0,
    /**
     * Список комментариев
     */
    val comments: List<Int> = emptyList(),
    /**
     * Количество прочитанных комментариев
     * (только при запросе информации о заметке текущего пользователя)
     */
    val readComments: Int = 0,
    /**
     * URL страницы для отображения заметки
     */
    val viewUrl: String = "",
    /**
     * Настройки приватности комментирования заметки
     */
    var privacy_view: String = "",
    /**
     * Есть ли возможность оставлять комментарии
     */
    val can_comment: Int = 0,
    /**
     * Тэги ссылок на wiki
     */
    val text_wiki: String = "",
    /**
     * Признак удаленности комментария
     */
    var isDeletes: Boolean = false
)