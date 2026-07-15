package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.Match
import com.example.data.Prediction
import com.example.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AppViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Prediction App Login") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        viewModel.login(email)
                        if (email == "l75051117@gmail.com") {
                            navController.navigate("admin_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("user_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login / Register")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(viewModel: AppViewModel, navController: NavController) {
    val users by viewModel.allUsers.collectAsState()
    val matches by viewModel.allMatches.collectAsState()
    var showAddMatch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Text("Exit")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddMatch = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Match")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Matches", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(matches) { match ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${match.team1} vs ${match.team2}")
                        IconButton(onClick = { viewModel.toggleMatchLock(match) }) {
                            Icon(
                                if (match.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Toggle Lock"
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Users", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(users) { user ->
                if (user.email != "l75051117@gmail.com") {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = { navController.navigate("admin_user/${user.id}/${user.email}") }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(user.email)
                        }
                    }
                }
            }
        }

        if (showAddMatch) {
            var team1 by remember { mutableStateOf("") }
            var team2 by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddMatch = false },
                title = { Text("Add New Match") },
                text = {
                    Column {
                        OutlinedTextField(value = team1, onValueChange = { team1 = it }, label = { Text("Team 1") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = team2, onValueChange = { team2 = it }, label = { Text("Team 2") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (team1.isNotBlank() && team2.isNotBlank()) {
                            viewModel.addMatch(team1, team2)
                            showAddMatch = false
                        }
                    }) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddMatch = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(viewModel: AppViewModel, navController: NavController, userId: Int, userEmail: String) {
    val matches by viewModel.allMatches.collectAsState()
    val predictions by viewModel.selectedUserPredictions.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadPredictionsForUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userEmail) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                        Text("${match.team1} vs ${match.team2}", fontWeight = FontWeight.Bold)
                        if (prediction != null) {
                            Text("Predicted: ${prediction.team1Score} - ${prediction.team2Score}")
                            Text("Points Awarded: ${prediction.pointsAwarded ?: "Not graded"}")
                            
                            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { viewModel.gradePrediction(prediction, 3) }) { Text("3 pts") }
                                Button(onClick = { viewModel.gradePrediction(prediction, 2) }) { Text("2 pts") }
                                Button(onClick = { viewModel.gradePrediction(prediction, 0) }) { Text("0 pts") }
                            }
                        } else {
                            Text("No prediction yet.")
                        }
                    }
                }
            }
        }
    }
}
