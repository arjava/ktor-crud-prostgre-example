package com.arjavax.repository

import com.arjavax.data.model.Note
import com.arjavax.data.model.User
import com.arjavax.data.table.NoteTable
import com.arjavax.data.table.UserTable
import com.arjavax.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class Repo {

    suspend fun addUser(user: User){
        dbQuery{
            UserTable.insert {
                it[email] = user.email
                it[name] = user.userName
                it[hashPassword] = user.hashPassword
            }
        }
    }

    suspend fun findUserByEmail(email:String) = dbQuery {
        UserTable.select{UserTable.email.eq(email)}
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?):User?{
        if(row == null){
            return null
        }
        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }

    suspend fun addNote(note: Note, email: String){
        dbQuery {
            NoteTable.insert {
                it[id] = note.id
                it[noteTitle] = note.noteTitle
                it[userEmail] = email
                it[description] = note.description
                it[date] = note.date
            }
        }
    }

    suspend fun getAllNotes(email: String):List<Note> = dbQuery {
        NoteTable.select {
            NoteTable.userEmail.eq(email)
        }.mapNotNull { rowToNote(it) }
    }

    suspend fun updateNote(note: Note, email: String){
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ){
                it[NoteTable.noteTitle] = note.noteTitle
                it[description] = note.description
                it[date] = note.date
            }
        }
    }

    suspend fun deleteNote(id: String){
        dbQuery {
            NoteTable.deleteWhere {
                NoteTable.id.eq(id)
            }
        }
    }

    private fun rowToNote(row: ResultRow?):Note?{
        if (row == null){
            return null
        }
        return Note(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }
}