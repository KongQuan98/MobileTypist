package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import org.example.project.utils.hapticClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingsOptionSheet(
    visible: Boolean,
    title: String,
    options: List<T>,
    selected: T,
    onDismiss: () -> Unit,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    optionLeading: (@Composable (T) -> Unit)? = null,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            options.forEachIndexed { index, option ->
                val isSelected = option == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .hapticClickable {
                            onSelect(option)
                            onDismiss()
                        }
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            } else {
                                Color.Transparent
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    optionLeading?.invoke(option)
                    if (optionLeading != null) {
                        Spacer(Modifier.width(12.dp))
                    }

                    Text(
                        text = optionLabel(option),
                        modifier = Modifier.weight(1f),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        ),
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = FeatherIcons.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                if (index < options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeColorSwatch(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                shape = CircleShape,
            ),
    )
}
