package com.arjavax.repository

import com.arjavax.data.model.User
import com.arjavax.data.table.UserTable
import com.arjavax.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class Repo {

    suspend fun addUser(user: User){
        dbQuery{
            UserTable.insert {
                it[UserTable.email] = user.email
                it[UserTable.name] = user.userName
                it[UserTable.hashPassword] = user.hashPassword
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
}