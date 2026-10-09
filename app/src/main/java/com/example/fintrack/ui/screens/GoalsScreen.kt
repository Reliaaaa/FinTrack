package com.example.fintrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.ui.theme.WarningAmber

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
                        text = "Target Barang Impian",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                    Text(
                        text = "Rencanakan & capai barang yang ingin kamu beli",
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
                    Text("Target Baru", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            // Overall Savings Metric Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text("TOTAL TARGET IMPIAN AKTIF", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("${goals.size} Sasaran Aktif", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = FinTrackRepository.formatRupiah(totalCollected),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                FinTrackProgressBar(percent = overallPercent, threshold = 80)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$overallPercent% Terkumpul", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Total Target Penuh: ${FinTrackRepository.formatRupiah(totalTarget)}", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Detailed Goals list
        items(goals, key = { it.id }) { goal ->
            val pct = if (goal.targetAmount > 0) ((goal.currentAmount * 100) / goal.targetAmount).toInt() else 0
            val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0L)

            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (goal.iconName) {
                                "flight" -> Icons.Default.Flight
                                "shield" -> Icons.Default.Stars
                                else -> Icons.Default.LaptopMac
                            },
                            contentDescription = null,
                            tint = PrimaryCobalt,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryCobalt.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(goal.category, color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        if (goal.reasonNote.isNotBlank()) {
                            Text(goal.reasonNote, style = MaterialTheme.typography.labelSmall, color = TextMuted, maxLines = 2)
                        }
                    }
                    Text("$pct%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SecondaryMint)
                }

                Spacer(modifier = Modifier.height(12.dp))

                FinTrackProgressBar(percent = pct, threshold = 80)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Terkumpul", color = TextMuted, fontSize = 10.sp)
                        Text(FinTrackRepository.formatRupiah(goal.currentAmount), color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sisa Target", color = TextMuted, fontSize = 10.sp)
                        Text(FinTrackRepository.formatRupiah(remaining), color = WarningAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Increment Presets (+1 Jt, +5 Jt, +10 Jt)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            FinTrackRepository.contributeGoal(goal.id, 1000000L)
                            onShowToast("+Rp 1.000.000 disetor ke ${goal.title}!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+1 Jt", fontSize = 11.sp, color = TextForeground)
                    }
                    Button(
                        onClick = {
                            FinTrackRepository.contributeGoal(goal.id, 5000000L)
                            onShowToast("+Rp 5.000.000 disetor ke ${goal.title}!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+5 Jt", fontSize = 11.sp, color = TextForeground)
                    }
                    Button(
                        onClick = {
                            FinTrackRepository.contributeGoal(goal.id, 10000000L)
                            onShowToast("+Rp 10.000.000 disetor ke ${goal.title}!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+10 Jt", fontSize = 11.sp, color = PrimaryCobalt, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
