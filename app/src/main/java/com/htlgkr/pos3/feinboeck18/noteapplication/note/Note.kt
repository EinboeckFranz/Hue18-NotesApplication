package com.htlgkr.pos3.feinboeck18.noteapplication.note

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Note(var noteContent: String, var dateTime: LocalDateTime) {
    companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (noteContent != other.noteContent) return false
        if (dateTime != other.dateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = noteContent.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "${dateTimeFormatter.format(dateTime)}, $noteContent"
    }
}