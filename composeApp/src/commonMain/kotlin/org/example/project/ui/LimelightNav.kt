package org.example.project.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.navigation.model.Screen

/**
 * Custom Trapezoid shape for the Limelight spotlight beam.
 * Matches CSS clip-path polygon(5% 100%, 25% 0, 75% 0, 95% 100%)
 */
private object LimelightSpotlightShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.25f, 0f)                     // Top left
            lineTo(size.width * 0.75f, 0f)                     // Top right
            lineTo(size.width * 0.95f, size.height)            // Bottom right
            lineTo(size.width * 0.05f, size.height)            // Bottom left
            close()
        }
        return Outline.Generic(path)
    }
}

data class LimelightNavItem(
    val id: String,
    val icon: @Composable () -> Unit,
    val label: String,
    val screenRoute: Screen
)

@Composable
fun LimelightNav(
    items: List<LimelightNavItem>,
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    primaryColor: Color = MaterialTheme.colorScheme.primary
) {
    val activeIndex = items.indexOfFirst { it.screenRoute == currentScreen }.coerceAtLeast(0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            val totalWidth = maxWidth
            val itemCount = items.size.coerceAtLeast(1)
            val tabWidth = totalWidth / itemCount

            // Smooth animated center X position for active tab spotlight
            val animatedTargetX by animateFloatAsState(
                targetValue = (activeIndex + 0.5f) * tabWidth.value,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "LimelightOffset"
            )

            val spotlightWidth = 68.dp
            val pillWidth = 42.dp

            // 1. Limelight Spotlight Light Beam (Trapezoid Gradient)
            Box(
                modifier = Modifier
                    .offset(x = (animatedTargetX.dp - (spotlightWidth / 2)))
                    .padding(top = 4.dp)
                    .size(spotlightWidth, 60.dp)
                    .clip(LimelightSpotlightShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // 2. Limelight Top Indicator Pill Bar
            Box(
                modifier = Modifier
                    .offset(x = (animatedTargetX.dp - (pillWidth / 2)))
                    .padding(top = 2.dp)
                    .size(pillWidth, 4.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
            )

            // 3. Navigation Items Row
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == activeIndex
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.45f,
                        label = "TabAlpha"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onTabSelected(item.screenRoute)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.alpha(animatedAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            item.icon()
                        }

                        if (item.label.isNotBlank()) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) primaryColor else contentColor.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .alpha(animatedAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}
