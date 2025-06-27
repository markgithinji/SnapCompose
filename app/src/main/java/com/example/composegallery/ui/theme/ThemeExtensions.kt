package com.example.composegallery.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val Shapes.searchBar: Shape
    @Composable
    @ReadOnlyComposable
    get() = RoundedCornerShape(8.dp)