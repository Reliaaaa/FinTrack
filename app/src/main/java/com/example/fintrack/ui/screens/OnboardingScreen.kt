package com.example.fintrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun OnboardingScreen(
    onComplete: (name: String, nickname: String, initialBalance: Long) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var nicknameInput by remember { mutableStateOf("") }
    var initialBalanceText by remember { mutableStateOf("10000000") }
    var isError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo Header with Ambient Glow
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryCobalt.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryCobalt, Color(0xFF3B65E8))
                            )
                        )
                        .border(2.dp, SecondaryMint.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "FinTrack Logo",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selamat Datang di FinTrack",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextForeground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Asisten Keuangan Cerdas & Portofolio Multi-Aset Pribadi",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Privacy & Local Storage Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkNavyCardElevated)
                    .border(1.dp, DarkNavyBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = SecondaryMint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "100% Data Lokal Tersimpan Aman di Perangkat Anda",
                    color = SecondaryMint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Onboarding Form Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkNavyCard)
                    .border(1.dp, DarkNavyBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Atur Profil Pengguna Baru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )

                    // Nama Lengkap
                    Column {
                        Text(
                            text = "Nama Lengkap *",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                if (it.isNotBlank()) isError = false
                            },
                            placeholder = { Text("Contoh: Sultan Ageng", color = TextMuted) },
                            singleLine = true,
                            isError = isError,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryCobalt)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkNavyBackground,
                                unfocusedContainerColor = DarkNavyBackground,
                                focusedTextColor = TextForeground,
                                unfocusedTextColor = TextForeground,
                                focusedBorderColor = PrimaryCobalt,
                                unfocusedBorderColor = DarkNavyBorder
                            )
                        )
                        if (isError) {
                            Text(
                                text = "Nama lengkap wajib diisi untuk memulai.",
                                color = Color(0xFFFF5252),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Nama Panggilan (Opsional)
                    Column {
                        Text(
                            text = "Nama Panggilan (Opsional)",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = nicknameInput,
                            onValueChange = { nicknameInput = it },
                            placeholder = { Text("Contoh: Sultan", color = TextMuted) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextMuted)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkNavyBackground,
                                unfocusedContainerColor = DarkNavyBackground,
                                focusedTextColor = TextForeground,
                                unfocusedTextColor = TextForeground,
                                focusedBorderColor = PrimaryCobalt,
                                unfocusedBorderColor = DarkNavyBorder
                            )
                        )
                    }

                    // Saldo Awal Tabungan/Kas
                    Column {
                        Text(
                            text = "Saldo Awal Kas / Rekening Utama (Rp)",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = initialBalanceText,
                            onValueChange = { initialBalanceText = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("Contoh: 10000000", color = TextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = SecondaryMint)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkNavyBackground,
                                unfocusedContainerColor = DarkNavyBackground,
                                focusedTextColor = TextForeground,
                                unfocusedTextColor = TextForeground,
                                focusedBorderColor = PrimaryCobalt,
                                unfocusedBorderColor = DarkNavyBorder
                            )
                        )
                    }

                    // Highlights
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyBackground)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aplikasi ini dapat di-reset kapan saja dari menu Profil.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol Submit
                    Button(
                        onClick = {
                            if (nameInput.trim().isBlank()) {
                                isError = true
                            } else {
                                val balance = initialBalanceText.toLongOrNull() ?: 10000000L
                                onComplete(
                                    nameInput.trim(),
                                    nicknameInput.trim().ifBlank { nameInput.trim() },
                                    balance
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_submit_button")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mulai Kelola Finansial",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
