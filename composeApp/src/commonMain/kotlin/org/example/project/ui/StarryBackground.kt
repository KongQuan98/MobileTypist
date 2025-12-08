package org.example.project.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.random.Random

data class Star(
    val x: Float,         // 0..1 (relative)
    val y: Float,         // 0..1 (relative)
    val radius: Float,    // px (will be scaled by density)
    val baseAlpha: Float, // 0..1
    val twinkleFreq: Float,
    val twinklePhase: Float
)

/**
 * StarryBackground that draws twinkling stars on a dark gradient, then places [content] on top.
 *
 * Put this composable in shared/commonMain and call it from platform entrypoints.
 */
@Composable
fun StarryBackground(
    modifier: Modifier = Modifier.fillMaxSize(),
    starCount: Int = 120,
    minRadiusPx: Float = 0.6f,
    maxRadiusPx: Float = 3.2f,
    mainColorTop: Color = Color(0xFF07080A),
    mainColorBottom: Color = Color(0xFF0b1220),
    starColor: Color = Color(0xFFFFFFFF),
    twinkleSpeedMultiplier: Float = 1f,
    content: @Composable () -> Unit = {}
) {
    // animate a global time value that loops — we'll compute per-star alpha from this
    val infinite = rememberInfiniteTransition()
    val time by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    BoxWithConstraints(modifier = modifier) {
        val width = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val height = constraints.maxHeight.toFloat().coerceAtLeast(1f)

        // seed random stable across recompositions (but re-generate per size)
        val rnd = remember(width, height) { Random(0xC0FFEE1234L) }

        // create stars with relative positions (0..1) — stable while size is the same
        val stars = remember(width, height) {
            List(starCount) {
                val x = rnd.nextFloat()
                val y = rnd.nextFloat()
                val radius = lerp(minRadiusPx, maxRadiusPx, rnd.nextFloat())
                val baseAlpha = lerp(0.2f, 1.0f, rnd.nextFloat())
                val freq = lerp(0.5f, 3.0f, rnd.nextFloat())
                val phase = rnd.nextFloat() * (2 * PI).toFloat()
                Star(x, y, radius, baseAlpha, freq, phase)
            }
        }

        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // background gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(mainColorTop, mainColorBottom)
                ),
                alpha = 1f
            )

            // subtle nebula / glow behind stars (optional)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x11FFFFFF)),
                ),
                alpha = 0.04f
            )

            // draw stars
            stars.forEach { star ->
                val cx = star.x * width
                val cy = star.y * height

                // compute star alpha by combining baseAlpha and a smooth twinkle function
                // twinkle: (cos(time * freq + phase) + 1) / 2 -> 0..1
                val twinkle =
                    (0.5f * (1f + cos((time * star.twinkleFreq * twinkleSpeedMultiplier) + star.twinklePhase)))
                val alpha = (star.baseAlpha * (0.4f + 0.6f * twinkle)).coerceIn(0f, 1f)

                // bigger stars get a slight glow
                val glowRadius = star.radius * 3f
                if (glowRadius > 1f) {
                    drawCircle(
                        color = starColor,
                        radius = glowRadius,
                        center = Offset(cx, cy),
                        alpha = 0.07f * alpha
                    )
                }

                // core
                drawCircle(
                    color = starColor,
                    radius = star.radius,
                    center = Offset(cx, cy),
                    alpha = alpha
                )

                // tiny sparkle: draw a rotated cross for a subset of stars
                if (star.radius > (minRadiusPx + maxRadiusPx) / 2f && alpha > 0.35f) {
                    val sparkleSize = star.radius * 1.8f
                    withTransform({
                        translate(left = cx - sparkleSize / 2f, top = cy - sparkleSize / 2f)
                        rotate(
                            degrees = (star.twinklePhase * 57.2958f) % 360f,
                            pivot = Offset(sparkleSize / 2f, sparkleSize / 2f)
                        )
                    }) {
                        // thin cross arms
                        drawRect(
                            color = starColor,
                            topLeft = Offset(0f, sparkleSize / 2f - 0.5f),
                            size = androidx.compose.ui.geometry.Size(sparkleSize, 1f),
                            alpha = alpha * 0.9f
                        )
                        drawRect(
                            color = starColor,
                            topLeft = Offset(sparkleSize / 2f - 0.5f, 0f),
                            size = androidx.compose.ui.geometry.Size(1f, sparkleSize),
                            alpha = alpha * 0.9f
                        )
                    }
                }
            }
        }

        // content goes on top
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

// small helper
private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
