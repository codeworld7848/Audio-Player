package com.android.myaudioplayer.presentation.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyTabLayout(tabIndex: MutableState<Int> = mutableStateOf(0)) {
    val tabs = listOf("Songs", "Albums")
    TabRow(selectedTabIndex = tabIndex.value) {
        tabs.forEachIndexed { index, item ->
            Tab(
                text = {
                    Text(text = item)
                },
                selected = tabIndex.value == index,
                onClick = {
                    tabIndex.value = index
                }
            )
        }
    }
}