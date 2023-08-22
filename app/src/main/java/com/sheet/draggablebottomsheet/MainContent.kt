package com.sheet.draggablebottomsheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.draggablebottomsheet.R
import kotlinx.coroutines.launch

private val tabMenus = listOf(
    "Tab1", "Tab2", "Tab3"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent() {
    val list = listOf(
        Color(0xFFD8BFD8),
        Color(0xFFD8BFD8),
        Color(0xFFEEE8AA),
        Color(0xFFEEE8AA),
        Color(0xFFAFEEEE),
        Color(0xFFAFEEEE)
    )

    var isShowBottomSheet by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    val tabStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp
    )
    var tabTextStyle by remember { mutableStateOf(tabStyle) }

    Column(
        Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = {
                        isShowBottomSheet = true
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 3.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color(0xFF222222)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_menu),
                        contentDescription = "menu"
                    )
                }
            }
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp), columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp), userScrollEnabled = false
        ) {
            itemsIndexed(items = list, key = { index, item -> index }) { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = item)
                ) {}
            }
        }
    }

    if (isShowBottomSheet) {
        val rightDirection = SwipeBottomSheetDirection.RIGHT
        val leftDirection = SwipeBottomSheetDirection.LEFT
        val bottomDirection = SwipeBottomSheetDirection.BOTTOM

        val currentDirection = bottomDirection

        val paddingValues =
            when (currentDirection) {
                rightDirection -> {
                    PaddingValues(top = 15.dp, end = 20.dp, bottom = 15.dp)
                }
                leftDirection -> {
                    PaddingValues(top = 15.dp, start = 20.dp, bottom = 15.dp)
                }
                else -> {
                    PaddingValues(start = 20.dp, end = 20.dp, bottom = 15.dp)
                }
            }

        val surfaceRoundedCornerShape =
            when (currentDirection) {
                rightDirection -> {
                    RoundedCornerShape(
                        topStart = 24.dp,
                        bottomStart = 24.dp
                    )
                }
                leftDirection -> {
                    RoundedCornerShape(
                        topEnd = 24.dp,
                        bottomEnd = 24.dp
                    )
                }
                else -> {
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                }
            }

        BackHandler(enabled = isShowBottomSheet) {
            isShowBottomSheet = false
        }

        DraggableBottomSheet(
            direction = currentDirection,
            surfaceRoundedCornerShape = surfaceRoundedCornerShape,
            onDismiss = {
                isShowBottomSheet = false
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TabRow(
                            modifier = Modifier,
                            selectedTabIndex = pagerState.currentPage,
                            containerColor = Color.Transparent,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    height = 1.5.dp,
                                    color = Color(0xFF000000)
                                )
                            },
                            divider = { Divider() }
                        ) {

                            tabMenus.forEachIndexed { index, tabData ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                    selectedContentColor = Color(0xFF000000),
                                    unselectedContentColor = Color(0xFFDDDDDD),
                                    text = {
                                        Text(
                                            text = tabData,
                                            style = tabStyle,
                                            onTextLayout = { textLayoutResult ->
                                                if (textLayoutResult.didOverflowHeight) {
                                                    tabTextStyle =
                                                        tabTextStyle.copy(fontSize = tabTextStyle.fontSize * 0.9)
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        HorizontalPager(
                            modifier = Modifier
                                .fillMaxWidth(),
                            pageCount = tabMenus.size,
                            state = pagerState,
                            verticalAlignment = Alignment.Top,
                            beyondBoundsPageCount = tabMenus.size,
                        ) { page ->
                            Column {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 20.dp, bottom = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    for (i in 1..15) {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .clip(shape = RoundedCornerShape(10.dp))
                                                    .background(
                                                        color =
                                                        when (page) {
                                                            0 -> Color(0xFFD8BFD8)
                                                            1 -> Color(0xFFEEE8AA)
                                                            else -> Color(0xFFAFEEEE)
                                                        }
                                                    )
                                                    .padding(start = 20.dp),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = "Content$i")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val count = pagerState.currentPage + 1

                        for (i in 1..count) {
                            TextButton(modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.5.dp, Color(0xFFDDDDDD),
                                ),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFF000000)
                                ),
                                onClick = {
                                }) {
                                Text(
                                    text = "Button$i",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    lineHeight = 10.sp
                                )
                            }
                        }
                    }
                }
            })
    }
}
