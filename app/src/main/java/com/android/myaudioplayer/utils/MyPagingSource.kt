/*
package com.android.myaudioplayer.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaginatedLazyColumn() {
    var items by remember { mutableStateOf((1..20).toList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val isScrolledToEnd by remember(scrollState) {
        derivedStateOf {
            scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size >= items.size - 5
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        state = scrollState
    ) {
        items(items) { item ->
            // Display your list item here
        }
    }

    if (isScrolledToEnd && !isLoading) {
        // Load more data when user reaches the end of the list
        loadMoreData(items)
    }
}

private fun loadMoreData(currentList: List<Int>) {
    // Simulate loading more data
    // You can fetch and append new data to the list
    // In this example, we're adding more integers
    val newData = (currentList.size until currentList.size + 20).toList()
    items.addAll(newData)
    isLoading = false
}*/
