package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {

    suspend fun insertUser(user: User): Long {
        return dao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return dao.getUserByEmail(email)
    }

    fun getAllUsers(): Flow<List<User>> = dao.getAllUsers()

    suspend fun insertMatch(match: Match) {
        dao.insertMatch(match)
    }

    suspend fun updateMatch(match: Match) {
        dao.updateMatch(match)
    }

    fun getAllMatches(): Flow<List<Match>> = dao.getAllMatches()
    
    suspend fun getMatchById(id: Int): Match? {
        return dao.getMatchById(id)
    }

    suspend fun savePrediction(prediction: Prediction) {
        val existing = dao.getPrediction(prediction.userId, prediction.matchId)
        if (existing != null) {
            dao.updatePrediction(prediction.copy(id = existing.id))
        } else {
            dao.insertPrediction(prediction)
        }
    }
    
    suspend fun gradePrediction(predictionId: Int, points: Int) {
        // Need a method to get prediction by id or update by id
    }

    suspend fun getPrediction(userId: Int, matchId: Int): Prediction? {
        return dao.getPrediction(userId, matchId)
    }

    fun getUserPredictions(userId: Int): Flow<List<Prediction>> = dao.getUserPredictions(userId)
    
    fun getUserTotalScore(userId: Int): Flow<Int?> = dao.getUserTotalScore(userId)
    
    // Add grade by getting specific
    suspend fun updatePredictionRaw(prediction: Prediction) {
        dao.updatePrediction(prediction)
    }
}
