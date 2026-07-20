package org.example.project.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A wrapper that shows a balloon/tooltip hint when hovered (on Desktop/Web) 
 * or long-pressed (on Mobile).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipHint(
    hint: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (hint.isBlank()) {
        content()
        return
    }

    val tooltipState = rememberTooltipState()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = hint,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(4.dp)
                )
            }
        },
        state = tooltipState,
        modifier = modifier
    ) {
        content()
    }
}
