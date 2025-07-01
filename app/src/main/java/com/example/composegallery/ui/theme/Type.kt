package com.example.composegallery.ui.theme

import androidx.compose.material3.Typography
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
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp
    ),

    headlineLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Light,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 14.sp
    ),

    labelLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 10.sp
    )
)