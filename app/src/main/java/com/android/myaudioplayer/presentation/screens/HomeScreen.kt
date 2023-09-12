package com.android.myaudioplayer.presentation.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.components.CustomTopBar
import com.android.myaudioplayer.presentation.components.getAlbumArt
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavController, mediaPlayerViewModel: MediaPlayerViewModel) {
    val pagerState = rememberPagerState(3)
    val searchIconClicked = remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            CustomTopBar(searchIconClicked, mediaPlayerViewModel)
        }
    ) {
        Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            AnimatedVisibility(visible = !searchIconClicked.value) {
                Tabs(pagerState = pagerState)
            }
            TabsContent(
                pagerState = pagerState, navController = navController,
                mediaPlayerViewModel
            )
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent(
    pagerState: PagerState,
    navController: NavController,
    mediaPlayerViewModel: MediaPlayerViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true ){
        if (mediaPlayerViewModel.audioList.value.isEmpty()) {
            mediaPlayerViewModel.getSongsFromDevice(context)
        }
    }

    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> SongsScreen(navController, mediaPlayerViewModel)
            1 -> AlbumsScreen(mediaPlayerViewModel)
            2 -> AlbumsScreen(mediaPlayerViewModel)
            3 -> AlbumsScreen(mediaPlayerViewModel)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val tabList: List<Pair<String, ImageVector>> = listOf(
        "Songs" to Icons.Default.Home,
        "Albums" to Icons.Default.Favorite,
        "Videos" to Icons.Default.Favorite
    )
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 4.dp,
                color = Color.White
            )
        }
    ) {
        tabList.forEachIndexed { index, pair ->
            Tab(
                selected = pagerState.currentPage == index, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                /*icon = {
                    Icon(
                        imageVector = tabList[index].second, contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                },*/
                text = {
                    Text(
                        tabList[index].first,
                        // on below line we are specifying the text color
                        // for the text in that tab
                        color = if (pagerState.currentPage == index) Color.White else Color.White.copy(
                            .8f
                        ),
                        fontSize = 16.sp
                    )
                }
            )
        }
    }
}

data class AudioData(
    val path: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val uri: Uri,
    val albumData: Bitmap?
)

