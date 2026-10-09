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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun SpendingLocationsScreen() {
    val locations by FinTrackRepository.spendingLocations.collectAsState()
    val totalLocationSpend = locations.sumOf { it.totalSpent }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Text(
                text = "Lokasi Pengeluaran",
                style = MaterialTheme.typography.headlineMedium,
                color = TextForeground,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Analisis pola belanja berdasarkan titik lokasi & merchant",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        item {
            // Summary Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryCobalt.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("TOTAL BELANJA DI LOKASI FAVORIT", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(FinTrackRepository.formatRupiah(totalLocationSpend), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        Text("${locations.size} Titik lokasi terdata", style = MaterialTheme.typography.labelSmall, color = SecondaryMint)
                    }
                }
            }
        }

        items(locations, key = { it.id }) { loc ->
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = loc.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Text(
                            text = "${loc.city} · ${loc.category}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = "${loc.transactionCount} kali transaksi",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCobalt
                        )
                    }
                    Text(
                        text = FinTrackRepository.formatRupiah(loc.totalSpent),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
