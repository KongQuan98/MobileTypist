package org.example.project.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import org.example.project.ThemeColors
import org.example.project.liquidglasssetup.LiquidBottomTab
import org.example.project.liquidglasssetup.LiquidBottomTabs

@Composable
actual fun LiquidBottomTabs(
    tabList: List<TabListSettings>,
    selectedTabIndex: Int,
) {
    val backdrop = rememberLayerBackdrop()

    var selectedIndex by rememberSaveable { mutableIntStateOf(selectedTabIndex) }

    LaunchedEffect(selectedTabIndex) {
        selectedIndex = selectedTabIndex
    }

    LiquidBottomTabs(
        selectedTabIndex = { selectedIndex },
        onTabSelected = {
            tabList[it].onClick(it)
        },
        backdrop = backdrop,
        tabsCount = 3,
        modifier = Modifier.padding(horizontal = 36f.dp, vertical = 32.dp)
    ) {
        tabList.forEachIndexed { index, item ->
            val (title, icon, onClicked) = item
            LiquidBottomTab(
                onClick = {
                    selectedIndex = index
                    onClicked(index)
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28f.dp),
                    tint = ThemeColors.Primary,
                )
                BasicText(
                    title,
                    style = TextStyle(contentColor, 12f.sp)
                )
            }
        }
    }

}


