package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert
    suspend fun insertMatch(match: Match): Long

    @Update
    suspend fun updateMatch(match: Match)

    @Query("SELECT * FROM matches")
    fun getAllMatches(): Flow<List<Match>>
    
    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: Int): Match?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: Prediction): Long

    @Update
    suspend fun updatePrediction(prediction: Prediction)

    @Query("SELECT * FROM predictions WHERE userId = :userId AND matchId = :matchId")
    suspend fun getPrediction(userId: Int, matchId: Int): Prediction?

    @Query("SELECT * FROM predictions WHERE userId = :userId")
    fun getUserPredictions(userId: Int): Flow<List<Prediction>>
    
    @Query("SELECT SUM(pointsAwarded) FROM predictions WHERE userId = :userId AND pointsAwarded IS NOT NULL")
    fun getUserTotalScore(userId: Int): Flow<Int?>
}
