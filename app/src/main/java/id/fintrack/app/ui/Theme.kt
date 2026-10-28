package id.fintrack.app.ui
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import id.fintrack.app.R
val FinBlue = Color(0xFF2563EB)
val FinGreen = Color(0xFF4EDEA3)
val FinNavy = Color(0xFF0B1326)
@Composable fun FinTrackTheme(content: @Composable () -> Unit) {
 MaterialTheme(colorScheme = darkColorScheme(
  primary=Color(0xFFB4C5FF), onPrimary=Color(0xFF002A78),
  primaryContainer=FinBlue, onPrimaryContainer=Color.White,
  secondary=FinGreen, background=FinNavy, surface=FinNavy,
  surfaceVariant=Color(0xFF171F33), onSurface=Color(0xFFDAE2FD),
  onBackground=Color(0xFFDAE2FD), onSurfaceVariant=Color(0xFFC3C6D7),
  outline=Color(0xFF434655), error=Color(0xFFFFB3AD)
 ), typography=Typography().let { t ->
     val font = FontFamily(Font(R.font.plus_jakarta))
     t.copy(displaySmall=t.displaySmall.copy(fontFamily=font),headlineLarge=t.headlineLarge.copy(fontFamily=font),headlineMedium=t.headlineMedium.copy(fontFamily=font),headlineSmall=t.headlineSmall.copy(fontFamily=font),titleLarge=t.titleLarge.copy(fontFamily=font),titleMedium=t.titleMedium.copy(fontFamily=font),titleSmall=t.titleSmall.copy(fontFamily=font))
 }, content=content)
}
