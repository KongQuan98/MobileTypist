package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.LocalKamelConfig

@Composable
fun StartMenu(
    visible: Boolean,
    timeOptions: List<Int>,
    selectedTime: Int,
    onTimeSelected: (Int) -> Unit,
    onStart: () -> Unit,
    onSurface: Color,
    onBg: Color,
    primary: Color,
    outline: Color,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GifAnimationHolder(
                source = "https://raw.githubusercontent.com/KongQuan98/kongquan98.github.io/refs/heads/main/Mobile%20Typist%20GIF/warrior_start_screen.gif",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
                    .padding(32.dp)
            )

            Text(
                text = "Keyboard Warrior",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Select time duration:",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = onBg
                )
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                timeOptions.forEach { t ->
                    val selected = t == selectedTime
                    val bg = if (selected) primary else Color.Transparent
                    val fg = if (selected) onSurface else onBg
                    val borderColor = outline
                    OutlinedButton(
                        onClick = { onTimeSelected(t) },
                        modifier = Modifier.shadow(
                            elevation = if (selected) 8.dp else 2.dp,
                            shape = RoundedCornerShape(8.dp)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = bg,
                            contentColor = fg
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) primary else borderColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${t}s"
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onStart,
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(4.dp)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Start",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
