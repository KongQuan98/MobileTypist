package org.example.project.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.UIKit.UIBlurEffect
import platform.UIKit.UIBlurEffectStyle
import platform.UIKit.UIVisualEffectView

@Composable
actual fun LiquidBottomTabs(
    content: @Composable () -> Unit
//    tabList: List<TabListSettings>,
//    selectedTabIndex: Int,
) {
    Box() {
        UIKitView(
            factory = {
                UIVisualEffectView(effect = UIBlurEffect.effectWithStyle(UIBlurEffectStyle.UIBlurEffectStyleSystemMaterial))
            },
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}
