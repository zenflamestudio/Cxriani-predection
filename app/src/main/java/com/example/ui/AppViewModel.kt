package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val allMatches: StateFlow<List<Match>> = repository.getAllMatches()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    private val _selectedUserPredictions = MutableStateFlow<List<Prediction>>(emptyList())
    val selectedUserPredictions: StateFlow<List<Prediction>> = _selectedUserPredictions.asStateFlow()
    
    private val _myTotalScore = MutableStateFlow<Int>(0)
    val myTotalScore: StateFlow<Int> = _myTotalScore.asStateFlow()

    fun initializeMatches() {
        viewModelScope.launch {
            val matches = allMatches.first()
            if (matches.isEmpty()) {
                repository.insertMatch(Match(team1 = "England", team2 = "Argentina"))
            }
        }
    }

    fun login(email: String) {
        viewModelScope.launch {
            var user = repository.getUserByEmail(email)
            if (user == null) {
                val userId = repository.insertUser(User(email = email))
                user = User(id = userId.toInt(), email = email)
            }
            _currentUser.value = user
            
            // Listen to my score
            repository.getUserTotalScore(user.id).collect { score ->
                _myTotalScore.value = score ?: 0
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun addMatch(team1: String, team2: String) {
        viewModelScope.launch {
            repository.insertMatch(Match(team1 = team1, team2 = team2))
        }
    }

    fun toggleMatchLock(match: Match) {
        viewModelScope.launch {
            repository.updateMatch(match.copy(isLocked = !match.isLocked))
        }
    }

    fun savePrediction(matchId: Int, team1Score: Int, team2Score: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.savePrediction(
                Prediction(
                    matchId = matchId,
                    userId = user.id,
                    team1Score = team1Score,
                    team2Score = team2Score
                )
            )
        }
    }
    
    fun loadPredictionsForUser(userId: Int) {
        viewModelScope.launch {
            repository.getUserPredictions(userId).collect {
                _selectedUserPredictions.value = it
            }
        }
    }

    fun gradePrediction(prediction: Prediction, points: Int) {
        viewModelScope.launch {
            repository.updatePredictionRaw(prediction.copy(pointsAwarded = points))
        }
    }
    
    // Helper to get my predictions
    private val _myPredictions = MutableStateFlow<List<Prediction>>(emptyList())
    val myPredictions: StateFlow<List<Prediction>> = _myPredictions.asStateFlow()
    
    fun loadMyPredictions() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.getUserPredictions(user.id).collect {
                _myPredictions.value = it
            }
        }
    }
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
