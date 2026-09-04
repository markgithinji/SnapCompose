package com.example.composegallery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.composegallery.R


val SourceSans = FontFamily(
    Font(R.font.sourcesans_extra_light, FontWeight.ExtraLight),
    Font(R.font.sourcesans_light, FontWeight.Light),
    Font(R.font.sourcesans_regular, FontWeight.Normal),
    Font(R.font.sourcesans_medium, FontWeight.Medium),
    Font(R.font.sourcesans_semi_bold, FontWeight.SemiBold),
    Font(R.font.sourcesans_bold, FontWeight.Bold),
    Font(R.font.sourcesans_extra_bold, FontWeight.ExtraBold),
    Font(R.font.sourcesans_black, FontWeight.Black)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    displayMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    displaySmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    headlineLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    headlineMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    headlineSmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    bodyLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    bodyMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    bodySmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    labelLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    labelMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    labelSmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
)
