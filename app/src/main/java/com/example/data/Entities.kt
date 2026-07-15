package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String
)

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val team1: String,
    val team2: String,
    val isLocked: Boolean = false
)

@Entity(
    tableName = "predictions",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Match::class, parentColumns = ["id"], childColumns = ["matchId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("userId"), Index("matchId")]
)
data class Prediction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: Int,
    val userId: Int,
    val team1Score: Int,
    val team2Score: Int,
    val pointsAwarded: Int? = null // null means not graded yet
)
