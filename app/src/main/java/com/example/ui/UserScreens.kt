package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.Match

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(viewModel: AppViewModel, navController: NavController) {
    val matches by viewModel.allMatches.collectAsState()
    val predictions by viewModel.myPredictions.collectAsState()
    val totalScore by viewModel.myTotalScore.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyPredictions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Predictions (Score: $totalScore)") },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Text("Exit")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(matches) { match ->
                val prediction = predictions.find { it.matchId == match.id }
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${match.team1} vs ${match.team2}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (match.isLocked) {
                            Text(
                                "Locked",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            if (prediction != null) {
                                Text("Your Prediction: ${prediction.team1Score} - ${prediction.team2Score}")
                                Text(
                                    "Points Received: ${prediction.pointsAwarded ?: "Pending"}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text("No prediction made before lock.")
                            }
                        } else {
                            var score1 by remember(prediction) { mutableIntStateOf(prediction?.team1Score ?: 0) }
                            var score2 by remember(prediction) { mutableIntStateOf(prediction?.team2Score ?: 0) }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(match.team1)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { if (score1 > 0) score1-- }) { Text("-") }
                                        Text("$score1")
                                        IconButton(onClick = { score1++ }) { Text("+") }
                                    }
                                }
                                
                                Text(":")
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(match.team2)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { if (score2 > 0) score2-- }) { Text("-") }
                                        Text("$score2")
                                        IconButton(onClick = { score2++ }) { Text("+") }
                                    }
                                }
                            }
                            
                            Button(
                                onClick = { viewModel.savePrediction(match.id, score1, score2) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Save Prediction")
                            }
                        }
                    }
                }
            }
        }
    }
}
