package com.example.composegallery.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.domain.model.ColorFilter
import com.example.composegallery.core.domain.model.OrderBy
import com.example.composegallery.core.domain.model.Orientation
import com.example.composegallery.core.domain.model.SearchFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterBottomSheet(
    filters: SearchFilters,
    onDismissRequest: () -> Unit,
    onApplyFilters: (SearchFilters) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentFilters by remember { mutableStateOf(filters) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.filters),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle(stringResource(R.string.sort_by))
            OrderBySelector(
                selected = currentFilters.orderBy,
                onSelected = { currentFilters = currentFilters.copy(orderBy = it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle(stringResource(R.string.orientation))
            OrientationSelector(
                selected = currentFilters.orientation,
                onSelected = { currentFilters = currentFilters.copy(orientation = it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle(stringResource(R.string.color))
            ColorSelector(
                selected = currentFilters.color,
                onSelected = { currentFilters = currentFilters.copy(color = it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        currentFilters = SearchFilters(query = currentFilters.query)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.reset_filters))
                }

                Button(
                    onClick = {
                        onApplyFilters(currentFilters)
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.apply_filters))
                }
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun OrderBySelector(
    selected: OrderBy,
    onSelected: (OrderBy) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OrderBy.entries.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = {
                    Text(
                        text = when (option) {
                            OrderBy.RELEVANT -> stringResource(R.string.relevant)
                            OrderBy.LATEST -> stringResource(R.string.latest)
                        }
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun OrientationSelector(
    selected: Orientation?,
    onSelected: (Orientation?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Any option
        FilterChip(
            selected = selected == null,
            onClick = { onSelected(null) },
            label = { Text(stringResource(R.string.any)) },
            shape = RoundedCornerShape(8.dp)
        )

        Orientation.entries.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = {
                    Text(
                        text = when (option) {
                            Orientation.LANDSCAPE -> stringResource(R.string.landscape)
                            Orientation.PORTRAIT -> stringResource(R.string.portrait)
                            Orientation.SQUARISH -> stringResource(R.string.squarish)
                        }
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun ColorSelector(
    selected: ColorFilter?,
    onSelected: (ColorFilter?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            ColorItem(
                color = null,
                isSelected = selected == null,
                onClick = { onSelected(null) }
            )
        }
        items(ColorFilter.entries) { option ->
            ColorItem(
                color = option,
                isSelected = selected == option,
                onClick = { onSelected(option) }
            )
        }
    }
}

@Composable
private fun ColorItem(
    color: ColorFilter?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displayColor = when (color) {
        null -> Color.Transparent
        ColorFilter.BLACK_AND_WHITE -> Color.Gray
        ColorFilter.BLACK -> Color.Black
        ColorFilter.WHITE -> Color.White
        ColorFilter.YELLOW -> Color(0xFFFFEB3B)
        ColorFilter.ORANGE -> Color(0xFFFF9800)
        ColorFilter.RED -> Color(0xFFF44336)
        ColorFilter.PURPLE -> Color(0xFF9C27B0)
        ColorFilter.MAGENTA -> Color(0xFFE91E63)
        ColorFilter.GREEN -> Color(0xFF4CAF50)
        ColorFilter.TEAL -> Color(0xFF009688)
        ColorFilter.BLUE -> Color(0xFF2196F3)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (color == null) MaterialTheme.colorScheme.surfaceVariant else displayColor)
            .then(
                if (color == ColorFilter.WHITE) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    CircleShape
                ) else Modifier
            )
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                ) else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (color == null) {
            Text(
                text = stringResource(R.string.any),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (color == ColorFilter.BLACK_AND_WHITE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color.Black, Color.White)
                        )
                    )
            )
        }
    }
}
