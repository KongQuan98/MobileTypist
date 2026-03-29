package org.example.project.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable

@Composable
actual fun LiquidBottomTabs(
    content: @Composable () -> Unit
//    tabList: List<TabListSettings>,
//    selectedTabIndex: Int,
) {
    Box() {
        content()
    }
//    val backdrop = rememberLayerBackdrop()
//
//    var selectedIndex by rememberSaveable { mutableIntStateOf(selectedTabIndex) }
//
//    LaunchedEffect(selectedTabIndex) {
//        selectedIndex = selectedTabIndex
//    }
//
//    LiquidBottomTabs(
//        selectedTabIndex = { selectedIndex },
//        onTabSelected = {
//            tabList[it].onClick(it)
//        },
//        backdrop = backdrop,
//        tabsCount = 3,
//        modifier = Modifier.padding(horizontal = 36f.dp, vertical = 32.dp)
//    ) {
//        tabList.forEachIndexed { index, item ->
//            val (title, icon, onClicked) = item
//            LiquidBottomTab(
//                onClick = {
//                    selectedIndex = index
//                    onClicked(index)
//                }
//            ) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = title,
//                    modifier = Modifier.size(28f.dp),
//                    tint = MaterialTheme.colorScheme.primary,
//                )
//                BasicText(
//                    title,
//                    style = TextStyle(contentColor, 12f.sp)
//                )
//            }
//        }
//    }

}


