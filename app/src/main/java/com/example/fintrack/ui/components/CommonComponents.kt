package com.example.fintrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.theme.DangerRed
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun FinTrackCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkNavyCard,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = DarkNavyBorder, shape = RoundedCornerShape(cornerRadius))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun CategoryIconBadge(
    category: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val (icon, tint, bgColor) = when {
        category.contains("Makanan", ignoreCase = true) || category.contains("Minuman", ignoreCase = true) || category.contains("Kopi", ignoreCase = true) ->
            Triple(Icons.Default.Fastfood, PrimaryCobalt, PrimaryCobalt.copy(alpha = 0.15f))
        category.contains("Belanja", ignoreCase = true) ->
            Triple(Icons.Default.ShoppingCart, WarningAmber, WarningAmber.copy(alpha = 0.15f))
        category.contains("Transportasi", ignoreCase = true) || category.contains("Grab", ignoreCase = true) ->
            Triple(Icons.Default.DirectionsCar, SecondaryMint, SecondaryMint.copy(alpha = 0.15f))
        category.contains("Pemasukan", ignoreCase = true) || category.contains("Gaji", ignoreCase = true) ->
            Triple(Icons.Default.TrendingUp, SecondaryMint, SecondaryMint.copy(alpha = 0.15f))
        category.contains("Tagihan", ignoreCase = true) || category.contains("Utilitas", ignoreCase = true) ->
            Triple(Icons.Default.Wifi, DangerRed, DangerRed.copy(alpha = 0.15f))
        category.contains("Investasi", ignoreCase = true) ->
            Triple(Icons.Default.AccountBalance, SecondaryMint, SecondaryMint.copy(alpha = 0.15f))
        else ->
            Triple(Icons.Default.Receipt, PrimaryCobalt, PrimaryCobalt.copy(alpha = 0.15f))
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category,
            tint = tint,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
fun FinTrackProgressBar(
    percent: Int,
    threshold: Int = 80,
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        percent >= 100 -> DangerRed
        percent >= threshold -> WarningAmber
        else -> PrimaryCobalt
    }
    val safeProgress = (percent.coerceIn(0, 100)) / 100f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(CircleShape)
            .background(DarkNavyMuted)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = safeProgress)
                .height(7.dp)
                .clip(CircleShape)
                .background(progressColor)
        )
    }
}
