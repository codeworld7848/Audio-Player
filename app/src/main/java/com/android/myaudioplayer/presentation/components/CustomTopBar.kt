package com.android.myaudioplayer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.myaudioplayer.MediaPlayerViewModel

@Composable
fun CustomTopBar(
    searchIconClicked: MutableState<Boolean>,
    mediaPlayerViewModel: MediaPlayerViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                if (searchIconClicked.value) Color.White else MaterialTheme.colorScheme.onSecondary
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = !searchIconClicked.value) {
            IconButton(onClick = {
            }) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = searchIconClicked.value,
                enter = expandHorizontally(
                    animationSpec = tween(
                        1000,
                        delayMillis = 200
                    )
                )
            ) {
                OutlinedTextField(
                    value = mediaPlayerViewModel.searchedMusic.value, onValueChange = {
                        mediaPlayerViewModel.searchedMusic.value = it
                    },
                    placeholder = {
                        Text(
                            text = "Search Music",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White

                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                    /* .graphicsLayer {
                                 this.scaleY = .8f
                             }*/,
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (mediaPlayerViewModel.searchedMusic.value.isEmpty()) {
                                searchIconClicked.value = false
                            } else {
                                mediaPlayerViewModel.searchedMusic.value = ""
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "",
                                tint = Color.Black
                            )
                        }
                    },
                    leadingIcon = {
                        IconButton(onClick = {
                            searchIconClicked.value = false
                            mediaPlayerViewModel.searchedMusic.value = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
            AnimatedVisibility(
                visible = !searchIconClicked.value
            ) {
                IconButton(onClick = {
                    searchIconClicked.value = !searchIconClicked.value
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    }
}