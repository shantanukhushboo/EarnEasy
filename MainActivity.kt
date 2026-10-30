package com.example.earneasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Task(val id: Int, val title: String, val description: String, val points: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EarnEasyApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarnEasyApp() {
    val tasks = listOf(
        Task(1, "Daily knowledge quiz", "Answer a short general-knowledge quiz.", 20),
        Task(2, "Learn something new", "Read a short learning lesson and answer its check-in.", 30),
        Task(3, "Create a mini poster", "Make an original digital poster and submit it for review.", 50),
        Task(4, "Daily goal check-in", "Review your goals for today.", 10)
    )
    var points by remember { mutableIntStateOf(0) }
    var submitted by remember { mutableStateOf(setOf<Int>()) }
    var tab by remember { mutableIntStateOf(0) }

    MaterialTheme(colorScheme = lightColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF1D4ED8))) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Column {
                        Text("EarnEasy", fontWeight = FontWeight.Bold)
                        Text("Tasks & rewards", style = MaterialTheme.typography.labelSmall)
                    }
                })
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, label = { Text("Home") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = "Tasks") }, label = { Text("Tasks") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") }, label = { Text("Wallet") })
                }
            }
        ) { padding ->
            when (tab) {
                0 -> HomeScreen(points, submitted.size, tasks.size, Modifier.padding(padding))
                1 -> TasksScreen(tasks, submitted, onSubmit = { id -> submitted = submitted + id }, Modifier.padding(padding))
                else -> WalletScreen(points, submitted.size, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun HomeScreen(points: Int, done: Int, total: Int, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Welcome to EarnEasy 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Your points", style = MaterialTheme.typography.titleMedium)
                Text("$points 🪙", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                Text("Demo points only — not cash.")
            }
        }
        Text("Your progress: $done / $total submissions")
        LinearProgressIndicator(progress = { if (total == 0) 0f else done.toFloat() / total }, modifier = Modifier.fillMaxWidth())
        Text("How it works", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Choose a task, complete it, and submit it. Submissions are marked pending review in this local demo.")
    }
}

@Composable
fun TasksScreen(tasks: List<Task>, submitted: Set<Int>, onSubmit: (Int) -> Unit, modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Available tasks", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Submit genuine work for review. Points are not awarded automatically.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tasks) { task ->
                Card {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(task.title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            AssistChip(onClick = {}, label = { Text("+${task.points} pts") })
                        }
                        Text(task.description)
                        if (submitted.contains(task.id)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(6.dp))
                                Text("Submitted — pending review")
                            }
                        } else {
                            Button(onClick = { onSubmit(task.id); message = "Task submitted for review." }) {
                                Text("Submit task")
                            }
                        }
                    }
                }
            }
        }
        if (message.isNotBlank()) Text(message, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun WalletScreen(points: Int, submissions: Int, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("My wallet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Available demo points")
                Text("$points 🪙", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            }
        }
        Text("Submissions: $submissions")
        Text("This prototype stores no real-money balance and does not process withdrawals or payments.")
    }
}
