package com.example.fintrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.components.FinTrackProgressBar
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun GoalsScreen(
    onOpenAddGoal: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val goals by FinTrackRepository.savingsGoals.collectAsState()

    val totalTarget = goals.sumOf { it.targetAmount }
    val totalCollected = goals.sumOf { it.currentAmount }
    val overallPercent = if (totalTarget > 0) ((totalCollected * 100) / totalTarget).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Target Impian",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Wujudkan rencana keuangan masa depan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onOpenAddGoal,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Target Baru", fontSize = 12.sp)
                }
            }
        }

        item {
            // Summary Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SecondaryMint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("TOTAL TABUNGAN TERKUMPUL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(FinTrackRepository.formatRupiah(totalCollected), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                FinTrackProgressBar(percent = overallPercent, threshold = 80)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "$overallPercent% tercapai", style = MaterialTheme.typography.labelSmall, color = SecondaryMint)
                    Text(text = "Target: ${FinTrackRepository.formatRupiah(totalTarget)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }

        items(goals, key = { it.id }) { goal ->
            val percent = if (goal.targetAmount > 0) ((goal.currentAmount * 100) / goal.targetAmount).toInt() else 0
            val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0L)

            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Text(
                            text = "${goal.category} · Batas: ${goal.deadline}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (percent >= 100) SecondaryMint else PrimaryCobalt
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                FinTrackProgressBar(percent = percent, threshold = 80)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = FinTrackRepository.formatRupiah(goal.currentAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextForeground
                    )
                    Text(
                        text = "Sisa: ${FinTrackRepository.formatRupiah(remaining)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick contribute action
                Button(
                    onClick = {
                        FinTrackRepository.contributeGoal(goal.id, 500000L)
                        onShowToast("Berhasil menabung +Rp 500.000 ke ${goal.title}!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Tambah Tabungan Rp 500.000", color = PrimaryCobalt, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
