package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CelestialColors {
    val DeepSpace = Color(0xFF05040A)
    val MidnightBg = Color(0xFF080B17)
    val SurfaceDark = Color(0xFF0E1324)
    val GlassSurface = Color(0xB8141229)
    val GlassCardAlt = Color(0xD9231E3E)
    val GoldLight = Color(0xFFF5D77F)
    val GoldBright = Color(0xFFEAD29A)
    val GoldDeep = Color(0xFFC79D50)
    val VioletPrimary = Color(0xFF6E44FF)
    val VioletSoft = Color(0xFFA98FDC)
    val VioletMuted = Color(0xFF988DB6)
    val TextPrimary = Color(0xFFF2E9D3)
    val TextSecondary = Color(0xFFBBB5C9)
    val BorderGlass = Color(0x38A08CFF)
    val BorderGold = Color(0x66DFB76C)
    val Emerald = Color(0xFF34D399)
    val EmeraldBg = Color(0x55064E3B)
    val Rose = Color(0xFFFB7185)
    val RoseBg = Color(0x55881337)
    val AmberBg = Color(0x22F59E0B)
}

private val CelestialDarkColorScheme = darkColorScheme(
    primary = CelestialColors.GoldBright,
    onPrimary = CelestialColors.MidnightBg,
    primaryContainer = CelestialColors.GlassCardAlt,
    onPrimaryContainer = CelestialColors.GoldLight,
    secondary = CelestialColors.VioletPrimary,
    onSecondary = Color.White,
    secondaryContainer = CelestialColors.SurfaceDark,
    onSecondaryContainer = CelestialColors.VioletSoft,
    tertiary = CelestialColors.GoldDeep,
    background = CelestialColors.DeepSpace,
    onBackground = CelestialColors.TextPrimary,
    surface = CelestialColors.MidnightBg,
    onSurface = CelestialColors.TextPrimary,
    surfaceVariant = CelestialColors.SurfaceDark,
    onSurfaceVariant = CelestialColors.TextSecondary,
    outline = CelestialColors.BorderGlass,
    error = CelestialColors.Rose
)

// For bundled Google Fonts (Cinzel + Inter), install via font-util and swap FontFamily.Serif / SansSerif.
val CelestialTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 2.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.4.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 1.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.8.sp
    )
)

val CelestialShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun CelestialLobbyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CelestialDarkColorScheme,
        typography = CelestialTypography,
        shapes = CelestialShapes,
        content = content
    )
}
