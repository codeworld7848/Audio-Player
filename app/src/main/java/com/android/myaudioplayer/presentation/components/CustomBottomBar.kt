package com.android.myaudioplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomBottomBar(
    isEnable: Boolean,
    playlistBtnClicked: () -> Unit,
    favClicked: () -> Unit,
    deleteClicked: () -> Unit,
    shareClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = if (isEnable) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSecondary.copy(
                    .6f
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = {
            playlistBtnClicked.invoke()
        }, enabled = isEnable) {
            Icon(
                imageVector = Icons.Default.List, contentDescription = "fav",
                tint = Color.White
            )
        }
        IconButton(onClick = {
            favClicked.invoke()
        }, enabled = isEnable) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "fav",
                tint = Color.White
            )
        }
        IconButton(onClick = {
            shareClicked.invoke()
        }, enabled = isEnable) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "fav", tint = Color.White)
        }
        IconButton(onClick = { deleteClicked.invoke() }, enabled = isEnable) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "fav", tint = Color.White)
        }
    }
}