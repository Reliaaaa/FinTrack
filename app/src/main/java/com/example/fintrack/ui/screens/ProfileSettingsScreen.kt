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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.fintrack.ui.theme.DangerRed
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun ProfileSettingsScreen(
    onShowToast: (String) -> Unit
) {
    val userProfile by FinTrackRepository.userProfile.collectAsState()
    val accounts by FinTrackRepository.accounts.collectAsState()

    var nameInput by remember { mutableStateOf(userProfile.name) }
    var nicknameInput by remember { mutableStateOf(userProfile.nickname) }
    var emailInput by remember { mutableStateOf(userProfile.email) }
    var phoneInput by remember { mutableStateOf(userProfile.phone) }

    var biometricActive by remember { mutableStateOf(userProfile.biometricEnabled) }
    var twoFactorActive by remember { mutableStateOf(userProfile.twoFactorEnabled) }
    var selectedCurrency by remember { mutableStateOf(userProfile.currency) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header & Avatar Uploader
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(PrimaryCobalt)
                            .border(3.dp, SecondaryMint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SA", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Akun FinTrack Pro Terverifikasi", color = PrimaryCobalt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                    Text("${userProfile.userCode} • Anggota sejak Januari $currentYear", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        item {
            // Section 2: Informasi Pribadi
            FinTrackCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Informasi Pribadi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nama Lengkap", color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextMuted) },
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { nicknameInput = it },
                    label = { Text("Nama Panggilan", color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.AlternateEmail, contentDescription = null, tint = TextMuted) },
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Alamat Email Finansial", color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = TextMuted) },
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Nomor Ponsel (OTP & Notifikasi)", color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.PhoneIphone, contentDescription = null, tint = TextMuted) },
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
        }

        item {
            // Section 3: Rekening Utama Switcher
            FinTrackCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pilih Rekening Utama Transaksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                }
                Text("Otomatis dipakai untuk catat transaksi & rekonsiliasi", color = TextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(12.dp))

                accounts.forEach { acc ->
                    val isSel = acc.isPrimary
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) PrimaryCobalt.copy(alpha = 0.15f) else DarkNavyBackground)
                            .border(1.dp, if (isSel) PrimaryCobalt else DarkNavyBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                FinTrackRepository.setPrimaryAccount(acc.id)
                                onShowToast("${acc.name} dijadikan rekening utama!")
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            alignItems = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(acc.name, fontWeight = FontWeight.Bold, color = TextForeground, fontSize = 13.sp)
                                Text("${acc.accountNumber} • Saldo ${FinTrackRepository.formatRupiah(acc.amount)}", color = TextMuted, fontSize = 11.sp)
                            }
                            if (isSel) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Jadikan Utama", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Section 4: Preferensi Mata Uang & Siklus Payroll
            FinTrackCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EventRepeat, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Preferensi Finansial & Regional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Mata Uang Utama Portfolio:", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("IDR", "USD", "SGD", "EUR").forEach { curr ->
                        val isCurrSel = selectedCurrency == curr
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrSel) PrimaryCobalt else DarkNavyBackground)
                                .border(1.dp, if (isCurrSel) PrimaryCobalt else DarkNavyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedCurrency = curr
                                    onShowToast("Mata uang diset ke $curr")
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(curr, color = if (isCurrSel) Color.White else TextForeground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyBackground)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Awal Siklus Anggaran Bulanan", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Tanggal 25 tiap bulan (Siklus Payroll)", color = TextMuted, fontSize = 10.sp)
                    }
                    Text("Tgl 25", color = SecondaryMint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        item {
            // Section 5: Keamanan & Autentikasi Biometrik
            FinTrackCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keamanan & Akses Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Kunci Aplikasi Biometrik (Face ID / Sidik Jari)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextForeground)
                        Text("Autentikasi aman setiap membuka aplikasi", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Switch(
                        checked = biometricActive,
                        onCheckedChange = {
                            biometricActive = it
                            onShowToast(if (it) "Kunci biometrik diaktifkan" else "Kunci biometrik dinonaktifkan")
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SecondaryMint)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Autentikasi Dua Langkah (2FA)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextForeground)
                        Text("Google Authenticator aktif", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Switch(
                        checked = twoFactorActive,
                        onCheckedChange = {
                            twoFactorActive = it
                            onShowToast(if (it) "2FA diaktifkan" else "2FA dinonaktifkan")
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryCobalt)
                    )
                }
            }
        }

        item {
            // Save Changes CTA
            Button(
                onClick = {
                    FinTrackRepository.updateUserProfile(
                        userProfile.copy(
                            name = nameInput,
                            nickname = nicknameInput,
                            email = emailInput,
                            phone = phoneInput,
                            biometricEnabled = biometricActive,
                            twoFactorEnabled = twoFactorActive,
                            currency = selectedCurrency
                        )
                    )
                    onShowToast("Perubahan profil berhasil disimpan!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simpan Perubahan Profil", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
