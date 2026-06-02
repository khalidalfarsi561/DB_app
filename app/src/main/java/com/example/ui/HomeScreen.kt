package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.DragonBallViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.UiState

/**
 * Main Entry for our Dragon Ball Universe UI.
 * Multi-pane responsive design. Supports adaptive window size columns and interaction details.
 */
@Composable
fun DragonBallHomeScreen(
    viewModel: DragonBallViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DbBackgroundDark)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // 1. Navigation Element for Large Screens (Tablet / Foldable Landscape)
            if (isWideScreen) {
                SideNavigationRailComponent(
                    activeTab = state.activeTab,
                    onTabSelect = { viewModel.changeActiveTab(it) }
                )
            }

            // 2. Main Content Feed Pane (Fits remaining space responsive)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top App Header
                    AppHeader(
                        activeTab = state.activeTab,
                        searchQuery = state.searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) }
                    )

                    // Navigation Sub-pane corresponding to the selected navigation tab
                    Box(modifier = Modifier.weight(1f)) {
                        when (state.activeTab) {
                            NavTab.HOME -> HomeTabContent(
                                state = state,
                                viewModel = viewModel,
                                isWideScreen = isWideScreen
                            )
                            NavTab.BROWSE -> BrowseTabContent(
                                state = state,
                                viewModel = viewModel,
                                isWideScreen = isWideScreen
                            )
                            NavTab.FAVORITES -> FavoritesTabContent(
                                state = state,
                                viewModel = viewModel,
                                isWideScreen = isWideScreen
                            )
                            NavTab.SETTINGS -> SettingsTabContent()
                        }
                    }

                    // 3. Bottom Navigation Element for Small Screens (Phones, Portrait)
                    if (!isWideScreen) {
                        BottomNavigationBarComponent(
                            activeTab = state.activeTab,
                            onTabSelect = { viewModel.changeActiveTab(it) }
                        )
                    }
                }
            }
        }

        // --- OVERLAY LAYER 1: Interactive Detail Bottom Sheet or Dialog Modal ---
        AnimatedVisibility(
            visible = state.activeDetailMedia != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            state.activeDetailMedia?.let { media ->
                MediaDetailOverlay(
                    media = media,
                    state = state,
                    onClose = { viewModel.selectMediaDetail(null) },
                    onToggleFavorite = { viewModel.toggleFavorite(media.id) },
                    onPlayEpisode = { viewModel.playEpisode(it) },
                    onReadChapter = { viewModel.readChapter(it) }
                )
            }
        }

        // --- OVERLAY LAYER 2: Immersive Cinema Theatre Player Mode ---
        AnimatedVisibility(
            visible = state.currentPlayingEpisode != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            state.currentPlayingEpisode?.let { episode ->
                ImmersiveTheaterPlayer(
                    episode = episode,
                    state = state,
                    onClose = { viewModel.stopPlayback() },
                    onProgressChange = { viewModel.updatePlaybackProgress(it) },
                    onToggleMute = { viewModel.toggleMute() }
                )
            }
        }

        // --- OVERLAY LAYER 3: Immersive Manga Chapter Reading View ---
        AnimatedVisibility(
            visible = state.currentReadingChapter != null,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            state.currentReadingChapter?.let { chapter ->
                MangaReaderOverlay(
                    chapter = chapter,
                    state = state,
                    onClose = { viewModel.closeMangaReader() },
                    onNextPage = { viewModel.nextMangaPage() },
                    onPrevPage = { viewModel.prevMangaPage() }
                )
            }
        }
    }
}

// ==========================================
// SUB-COMPONENTS: NAVIGATION RAMPARTS
// ==========================================

@Composable
fun SideNavigationRailComponent(
    activeTab: NavTab,
    onTabSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = DbSurfaceDark,
        contentColor = DbTextLight,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                DragonBallSphere(modifier = Modifier.size(54.dp), starCount = 4)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DB SAGA",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = DbOrangePrimary,
                    letterSpacing = 1.sp
                )
            }
        },
        modifier = modifier.testTag("side_navigation_rail")
    ) {
        Spacer(modifier = Modifier.weight(1f))

        NavigationRailItem(
            selected = activeTab == NavTab.HOME,
            onClick = { onTabSelect(NavTab.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("rail_tab_home")
        )

        Spacer(modifier = Modifier.height(16.dp))

        NavigationRailItem(
            selected = activeTab == NavTab.BROWSE,
            onClick = { onTabSelect(NavTab.BROWSE) },
            icon = { Icon(Icons.Filled.Search, contentDescription = "تصفح") },
            label = { Text("تصفح", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("rail_tab_browse")
        )

        Spacer(modifier = Modifier.height(16.dp))

        NavigationRailItem(
            selected = activeTab == NavTab.FAVORITES,
            onClick = { onTabSelect(NavTab.FAVORITES) },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "المكتبة") },
            label = { Text("مفضلتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("rail_tab_favorites")
        )

        Spacer(modifier = Modifier.height(16.dp))

        NavigationRailItem(
            selected = activeTab == NavTab.SETTINGS,
            onClick = { onTabSelect(NavTab.SETTINGS) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "الضبط") },
            label = { Text("الضبط", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("rail_tab_settings")
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun BottomNavigationBarComponent(
    activeTab: NavTab,
    onTabSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = DbSurfaceDark,
        contentColor = DbTextLight,
        modifier = modifier
            .testTag("bottom_nav_bar")
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = activeTab == NavTab.HOME,
            onClick = { onTabSelect(NavTab.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("bottom_tab_home")
        )

        NavigationBarItem(
            selected = activeTab == NavTab.BROWSE,
            onClick = { onTabSelect(NavTab.BROWSE) },
            icon = { Icon(Icons.Filled.Search, contentDescription = "تصفح") },
            label = { Text("تصفح", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("bottom_tab_browse")
        )

        NavigationBarItem(
            selected = activeTab == NavTab.FAVORITES,
            onClick = { onTabSelect(NavTab.FAVORITES) },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "المفضلة") },
            label = { Text("المفضلة", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("bottom_tab_favorites")
        )

        NavigationBarItem(
            selected = activeTab == NavTab.SETTINGS,
            onClick = { onTabSelect(NavTab.SETTINGS) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "الضبط") },
            label = { Text("الضبط", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = DbOrangePrimary,
                indicatorColor = DbOrangePrimary,
                unselectedIconColor = DbTextMuted,
                unselectedTextColor = DbTextMuted
            ),
            modifier = Modifier.testTag("bottom_tab_settings")
        )
    }
}

// ==========================================
// CENTRAL APP HEADER
// ==========================================

@Composable
fun AppHeader(
    activeTab: NavTab,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        color = DbSurfaceDark,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App branding name (Arabic text styling)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "كوكب دراغون بول",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DbOrangePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "يونيفرس",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DbGoldTertiary
                    )
                }
                Text(
                    text = "بوابتك لكل الحلقات والمانجا بدقة خارقة",
                    fontSize = 10.sp,
                    color = DbTextMuted,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            // Real-time Search Panel for immediate interactive response
            Box(
                modifier = Modifier
                    .widthIn(max = 240.dp)
                    .background(DbSurfaceVariantDark, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "بحث",
                        tint = DbOrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                "ابحث عن فيلم، حلقة، مانغا...",
                                fontSize = 11.sp,
                                color = DbTextMuted,
                                textAlign = TextAlign.End
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = DbOrangePrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        textStyle = LocalTextStyle.current.copy(
                            color = DbTextLight,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .padding(0.dp)
                            .testTag("search_input_field")
                    )

                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "مسح",
                            tint = DbTextMuted,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onSearchChange("") }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1 CONTENT: HOME TAB (THE MASTER DASHBOARD)
// ==========================================

@Composable
fun HomeTabContent(
    state: UiState,
    viewModel: DragonBallViewModel,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // A. Spacing before Hero Billboard
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // B. Hero Featured Billboard (Dragon Ball Daima Promo)
        item {
            val featuredDaima = state.mediaList.firstOrNull { it.id == "dbs_daima" }
            featuredDaima?.let { media ->
                HeroBillboardCard(
                    media = media,
                    onExploreClick = { viewModel.selectMediaDetail(media) },
                    onQuickWatchClick = {
                        val eps = DbRepository.episodesMap["dbs_daima"] ?: emptyList()
                        if (eps.isNotEmpty()) viewModel.playEpisode(eps.first())
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // C. Filter category buttons carousel
        item {
            Text(
                text = "اختر الحقبة أو الأجـزاء الخاصة بك",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = DbGoldTertiary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CategoryCarouselRow(
                selectedCategory = state.selectedCategory,
                onSelectCategory = { viewModel.selectCategory(it) }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // D. Quick media type togglers (Anime Movies vs Manga Chapters)
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // All Toggle
                FilterChipComponent(
                    label = "الكل",
                    isSelected = state.selectedTypeFilter == null,
                    onClick = { viewModel.selectTypeFilter(null) }
                )
                // Anime Filter
                FilterChipComponent(
                    label = "حلقات وأفلام الأنمي",
                    isSelected = state.selectedTypeFilter == DbType.ANIME,
                    onClick = { viewModel.selectTypeFilter(DbType.ANIME) }
                )
                // Manga Filter
                FilterChipComponent(
                    label = "فصول وقراءة المانغا",
                    isSelected = state.selectedTypeFilter == DbType.MANGA,
                    onClick = { viewModel.selectTypeFilter(DbType.MANGA) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // E. Dynamic Grid displaying all matching DB products responsive
        item {
            val countColumns = if (isWideScreen) 3 else 2
            val chunkedList = state.filteredList.chunked(countColumns)

            if (chunkedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Tv,
                            contentDescription = "لا يوجد",
                            tint = DbTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "لم نجد تطابق لبحثك! جرب مصطلح آخر لمجرة دراغون بول",
                            color = DbTextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    chunkedList.forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowItems.forEach { item ->
                                Box(modifier = Modifier.weight(1f)) {
                                    DragonMediaCard(
                                        media = item,
                                        isBookmarked = state.favorites.contains(item.id),
                                        onCardClick = { viewModel.selectMediaDetail(item) },
                                        onBookmarkToggle = { viewModel.toggleFavorite(item.id) }
                                    )
                                }
                            }
                            // Fill blank weights in row if remaining chunk not full
                            if (rowItems.size < countColumns) {
                                repeat(countColumns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // F. Manga Showcase Highlight
        item {
            val featuredManga = state.mediaList.find { it.type == DbType.MANGA }
            if (featuredManga != null) {
                MangaExclusiveRow(
                    media = featuredManga,
                    onExploreClick = { viewModel.selectMediaDetail(featuredManga) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

// ==========================================
// HERO BILLBOARD SPECIAL PROMO CARD
// ==========================================

@Composable
fun HeroBillboardCard(
    media: DragonBallMedia,
    onExploreClick: () -> Unit,
    onQuickWatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DbSurfaceDark),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .border(1.dp, DbOrangePrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Custom Animated background drawn behind
            CharacterBackground(categoryTitle = media.id)

            // Dynamic cosmic gradient overlays for dark gorgeous cinematic blend
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x33000000),
                                DbSurfaceDark.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Golden Glow Stars elements floating
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "تصنيف",
                        tint = DbGoldTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${media.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DbTextLight
                    )
                    Text(
                        text = "الأعلى تقييماً",
                        fontSize = 9.sp,
                        color = DbGoldTertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Text contents inside the Billboard (Arabic localized text)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                // Daima capsule
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(DbOrangePrimary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "أحدث إصدار",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "عالم المملكة الشيطانية الجديد",
                        fontSize = 10.sp,
                        color = DbGoldTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = media.titleAr,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = media.descriptionAr,
                    fontSize = 11.sp,
                    color = DbTextMuted,
                    maxLines = 3,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action controls play episode 1 immediately
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Button(
                        onClick = onQuickWatchClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DbOrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("quick_watch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "شاهد",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مشاهدة الحلقة الأولى", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("explore_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tv,
                            contentDescription = "تفاصيل",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تفاصيل السلسلة كاملة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// CAROUSEL FILTER CAPSULE ROW
// ==========================================

@Composable
fun CategoryCarouselRow(
    selectedCategory: DbCategory?,
    onSelectCategory: (DbCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // Option 'All'
        item {
            Box(
                modifier = Modifier
                    .background(
                        if (selectedCategory == null) DbOrangePrimary else DbSurfaceVariantDark,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectCategory(null) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "جميع الأجزاء",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCategory == null) Color.White else DbTextLight
                )
            }
        }

        items(DbCategory.values()) { category ->
            val isSelected = selectedCategory == category
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) DbOrangePrimary else DbSurfaceVariantDark,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectCategory(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category.titleAr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else DbTextLight
                )
            }
        }
    }
}

@Composable
fun FilterChipComponent(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) DbOrangePrimary.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isSelected) DbOrangePrimary else Color(0x33FFFFFF),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(if (isSelected) DbOrangePrimary else DbTextMuted, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) DbOrangePrimary else DbTextLight
            )
        }
    }
}

// ==========================================
// INDIVIDUAL SERIES/MOVIE MEDIA CARD (GRID)
// ==========================================

@Composable
fun DragonMediaCard(
    media: DragonBallMedia,
    isBookmarked: Boolean,
    onCardClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DbSurfaceDark),
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { onCardClick() }
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Visual dynamic banner representing the series
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
            ) {
                CharacterBackground(categoryTitle = media.id)

                // Visual Dark blur bottom gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x99000000))
                            )
                        )
                )

                // High intensity type caps (Anime / Manga Badge)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .background(
                            if (media.type == DbType.MANGA) DbGoldDark else DbOrangePrimary,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (media.type == DbType.MANGA) "مانغا" else if (media.type == DbType.MOVIE) "فيلم" else "أنمي",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Heart favorite selector
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "تفضيل",
                            tint = if (isBookmarked) DbOrangePrimary else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Bottom Overlay showing items count
                Text(
                    text = if (media.type == DbType.MANGA) "${media.chaptersCount} فصل" else "${media.episodesCount} حلقة",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DbTextLight,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // Info Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = media.titleAr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = media.descriptionAr,
                    fontSize = 9.sp,
                    color = DbTextMuted,
                    maxLines = 2,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "نجم",
                            tint = DbGoldTertiary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${media.rating}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DbTextLight
                        )
                    }

                    // Release Year
                    Text(
                        text = media.year,
                        fontSize = 9.sp,
                        color = DbTextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    // Compact Indicator check status
                    Text(
                        text = media.statusAr,
                        fontSize = 8.sp,
                        color = if (media.statusAr.contains("مستمر")) DbOrangePrimary else DbTextMuted,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

// ==========================================
// NOSTALGIC/EXCLUSIVE MANGA ROW HIGHLIGHT
// ==========================================

@Composable
fun MangaExclusiveRow(
    media: DragonBallMedia,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DbSurfaceDark),
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .border(1.dp, DbGoldTertiary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Left Custom Screentone Graphic
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
            ) {
                CharacterBackground(categoryTitle = "manga_super")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, DbSurfaceDark)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = "مانغا",
                        tint = DbGoldTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "مانغا يابانية",
                        fontSize = 9.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(DbGoldTertiary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Right Info Localized
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "قراءة من اليمين لليسار",
                                fontSize = 8.sp,
                                color = DbGoldTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "الرسام: تويوتارو",
                            fontSize = 8.sp,
                            color = DbTextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = media.titleAr,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = media.descriptionAr,
                        fontSize = 10.sp,
                        color = DbTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "يتوفر ${media.chaptersCount} فصل مترجم بالكامل",
                        fontSize = 9.sp,
                        color = DbTextLight,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DbGoldDark),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("ادخل الأرشيف", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

// ==========================================
// BROWSE DATABASE TAB IN ALL PARTS
// ==========================================

@Composable
fun BrowseTabContent(
    state: UiState,
    viewModel: DragonBallViewModel,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Active Subtitle
        Text(
            text = "تصفح الأرشيف الكامل والملاحم",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = "يمكنك النقر على العناوين لفتح الحلقات والأفلام والمانجا ومتابعتها فوراً",
            fontSize = 11.sp,
            color = DbTextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        CategoryCarouselRow(
            selectedCategory = state.selectedCategory,
            onSelectCategory = { viewModel.selectCategory(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large Flexible Grid list
        val cols = if (isWideScreen) 4 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(cols),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.filteredList) { media ->
                DragonMediaCard(
                    media = media,
                    isBookmarked = state.favorites.contains(media.id),
                    onCardClick = { viewModel.selectMediaDetail(media) },
                    onBookmarkToggle = { viewModel.toggleFavorite(media.id) }
                )
            }
        }
    }
}

// ==========================================
// FAVORITES & PERSISTED LIBRARY TAB
// ==========================================

@Composable
fun FavoritesTabContent(
    state: UiState,
    viewModel: DragonBallViewModel,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val favoritedItems = state.mediaList.filter { state.favorites.contains(it.id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "مـكتبتـي الـخاصـة (المفضلة)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = DbGoldTertiary
        )
        Text(
            text = "قائمتك الشخصية للوصول السريع إلى فصول المانغا وحلقات الأنمي",
            fontSize = 11.sp,
            color = DbTextMuted,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoritedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                        DragonBallSphere(starCount = 1, pulse = false)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "مكتبتك فارغة حالياً!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "انقر فوق علامة القلب في أي غلاف لإضافته إلى مكتبتك الخاصة لسهولة المتابعة",
                        fontSize = 11.sp,
                        color = DbTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            val cols = if (isWideScreen) 4 else 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(favoritedItems) { media ->
                    DragonMediaCard(
                        media = media,
                        isBookmarked = true,
                        onCardClick = { viewModel.selectMediaDetail(media) },
                        onBookmarkToggle = { viewModel.toggleFavorite(media.id) }
                    )
                }
            }
        }
    }
}

// ==========================================
// SETTINGS TAB EXPLAIN
// ==========================================

@Composable
fun SettingsTabContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "إعدادات تطبيق دراغون بول",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = DbOrangePrimary
        )
        Text(
            text = "تعديل خيارات التشغيل ومزامنة السحابة والتحميلات",
            fontSize = 11.sp,
            color = DbTextMuted,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingRowComponent(
                icon = Icons.Filled.Tv,
                title = "جودة تشغيل الميديا المفضلة",
                subtitle = "جودة تلقائية (4K بحد أقصى للأجهزة الداعمة)"
            )
            SettingRowComponent(
                icon = Icons.Filled.Book,
                title = "طريقة عرض صفحات المانغا",
                subtitle = "صفحة كاملة (من اليمين إلى اليسار تلقائياً)"
            )
            SettingRowComponent(
                icon = Icons.Filled.Share,
                title = "مزامنة سحابة الأنيميشن",
                subtitle = "مفعلة تلقائياً لمزامنة تقدم الحلقات المسجلة"
            )
            SettingRowComponent(
                icon = Icons.Filled.VolumeUp,
                title = "نظام الصوت المحيطي",
                subtitle = "مفعل (يعتمد على مسارات الصوت اليابانية الأصلية بدقة Hi-Res)"
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Decorative disclaimer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DbSurfaceVariantDark, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "حقوق الملكية الفكرية",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DbGoldTertiary
                )
                Text(
                    text = "جميع حقوق المواد المكتوبة والمرئية للأنمي والمانغا تعود للكاتب أكيرا تورياما، شركة Toei Animation، وسلسلة Shueisha اليابانية. هذا التطبيق يعد منصة تصفح متفاعلة تم تجميعها بكل شغف للمعجبين العربي بمهارة عالية.",
                    fontSize = 9.sp,
                    color = DbTextMuted,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun SettingRowComponent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DbSurfaceDark, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(DbOrangePrimary.copy(alpha = 0.15f), CircleShape)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DbOrangePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = DbTextMuted
            )
        }

        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            tint = DbTextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

// ==========================================
// OVERLAY 1: INTERACTIVE MEDIA DETAIL DIALOG
// ==========================================

@Composable
fun MediaDetailOverlay(
    media: DragonBallMedia,
    state: UiState,
    onClose: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayEpisode: (DbEpisode) -> Unit,
    onReadChapter: (DbChapter) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onClose() } // Click outside to close
    ) {
        // Core Sheet Body
        Surface(
            color = DbSurfaceDark,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .align(Alignment.BottomCenter)
                .border(2.dp, DbOrangePrimary.copy(alpha = 0.3f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(enabled = false) { } // Prevent closing when tapping inside
                .testTag("media_detail_sheet")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header drag-looking node
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(48.dp, 4.dp)
                        .background(DbTextMuted.copy(alpha = 0.5f), CircleShape)
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (media.type == DbType.MANGA) "تفاصيل المانغا" else "تفاصيل الأنمي",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = DbGoldTertiary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    // A. Top Billboard banner presentation
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            CharacterBackground(categoryTitle = media.id)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, DbBackgroundDark.copy(alpha = 0.9f))
                                        )
                                    )
                            )

                            // Title Overlay Inside
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    media.titleAr,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    media.titleEn,
                                    fontSize = 10.sp,
                                    color = DbGoldTertiary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // B. Statistics Summary Pill row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(label = "التقييم", value = "${media.rating} ★", modifier = Modifier.weight(1f))
                            StatBox(label = "سنة الإنتاج", value = media.year, modifier = Modifier.weight(1f))
                            StatBox(
                                label = if (media.type == DbType.MANGA) "الفصول" else "الحلقات",
                                value = if (media.type == DbType.MANGA) "${media.chaptersCount}" else "${media.episodesCount}",
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(label = "الحالة", value = media.statusAr, modifier = Modifier.weight(1f))
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // C. Original Author, Publisher
                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DbSurfaceVariantDark, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text("المؤلف الأصلي:", fontSize = 11.sp, color = DbTextMuted)
                            Text(media.author, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // D. Story Description
                    item {
                        Text(
                            text = "قصة الملحمة الأسطورية",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DbGoldTertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = media.descriptionAr,
                            fontSize = 11.sp,
                            color = DbTextLight,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // E. Content Action Grid (List of Episodes or Chapters)
                    item {
                        Text(
                            text = if (media.type == DbType.MANGA) "فصـول الـقـراءة المتوفرة" else "حـلـقـات الأنمي لمشاهدتها فورا",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DbOrangePrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (media.type == DbType.MANGA) {
                        // Display Chapters List
                        val chapters = DbRepository.chaptersMap[media.id] ?: emptyList()
                        if (chapters.isEmpty()) {
                            item {
                                Text("جاري فحص فصول المانغا وترجمتها م رتوش... ابق على اتصال!", fontSize = 11.sp, color = DbTextMuted)
                            }
                        } else {
                            items(chapters) { chapter ->
                                InteractiveItemRow(
                                    title = chapter.titleAr,
                                    subtitle = "يتألف من ${chapter.pagesCount} صفحة عالية الجودة • للبدء التلقائي",
                                    icon = Icons.Filled.Book,
                                    actionLabel = "ابدأ القراءة",
                                    onClick = { onReadChapter(chapter) }
                                )
                            }
                        }
                    } else {
                        // Display Episodes List
                        val episodes = DbRepository.episodesMap[media.id] ?: emptyList()
                        if (episodes.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DbSurfaceVariantDark)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Filled.Tv, contentDescription = null, tint = DbOrangePrimary, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            "هذا الفيلم متوفر مباشرة بالكامل عبر خيار المشاهدة الفورية",
                                            fontSize = 11.sp,
                                            color = DbTextLight,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                // Start simulated movie play
                                                onPlayEpisode(
                                                    DbEpisode(
                                                        id = "movie_play",
                                                        seriesId = media.id,
                                                        titleAr = "فيلم ${media.titleAr} بالكامل",
                                                        titleEn = media.titleEn,
                                                        episodeNumber = 1,
                                                        durationAr = "1:45:00",
                                                        durationEn = "1:45:00",
                                                        releaseDate = media.year
                                                    )
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DbOrangePrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("شاهد الفيلم الآن", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            items(episodes) { episode ->
                                InteractiveItemRow(
                                    title = "الحلقة ${episode.episodeNumber}: ${episode.titleAr}",
                                    subtitle = "مدة العرض: ${episode.durationAr} دقيقة • تاريخ العرض: ${episode.releaseDate}",
                                    icon = Icons.Filled.PlayArrow,
                                    actionLabel = "تشغيل الآن",
                                    onClick = { onPlayEpisode(episode) }
                                )
                            }
                        }
                    }

                    // Bottom padding inside scroll
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }

                // Bottom Core Button bookmark
                Surface(
                    color = DbSurfaceVariantDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isBookmarked = state.favorites.contains(media.id)
                        OutlinedButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .weight(0.4f)
                                .height(44.dp),
                            border = BorderStroke(1.dp, if (isBookmarked) DbOrangePrimary else Color.White.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isBookmarked) DbOrangePrimary else Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBookmarked) "بالمكتبة" else "تفضيل", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        Button(
                            onClick = {
                                if (media.type == DbType.MANGA) {
                                    val chapters = DbRepository.chaptersMap[media.id] ?: emptyList()
                                    if (chapters.isNotEmpty()) onReadChapter(chapters.first())
                                } else {
                                    val episodes = DbRepository.episodesMap[media.id] ?: emptyList()
                                    if (episodes.isNotEmpty()) {
                                        onPlayEpisode(episodes.first())
                                    } else {
                                        onPlayEpisode(
                                            DbEpisode(
                                                id = "movie_play",
                                                seriesId = media.id,
                                                titleAr = "فيلم ${media.titleAr} بالكامل",
                                                titleEn = media.titleEn,
                                                episodeNumber = 1,
                                                durationAr = "1:45:00",
                                                durationEn = "1:45:00",
                                                releaseDate = media.year
                                            )
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DbOrangePrimary),
                            modifier = Modifier
                                .weight(0.6f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (media.type == DbType.MANGA) "بدء قراءة أول فصل" else "بدء تشغيل وتدفق",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DbSurfaceVariantDark),
        modifier = modifier.border(0.5.dp, Color(0x2BFFFFFF), RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = DbTextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InteractiveItemRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(DbSurfaceVariantDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(DbOrangePrimary.copy(alpha = 0.15f), CircleShape)
                .padding(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DbOrangePrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = DbTextMuted
            )
        }

        Box(
            modifier = Modifier
                .background(DbOrangePrimary, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = actionLabel,
                fontSize = 8.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
    }
}

// ==========================================
// OVERLAY 2: IMMERSIVE THEATER MEDIA PLAYER
// ==========================================

@Composable
fun ImmersiveTheaterPlayer(
    episode: DbEpisode,
    state: UiState,
    onClose: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenProgress = state.videoPlaybackProgress

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("immersive_cinema_player")
    ) {
        // Upper back and info panel controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "مسرح دراغون بول السينمائي",
                    fontSize = 10.sp,
                    color = DbOrangePrimary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = episode.titleAr,
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Top resolution specifier
            Box(
                modifier = Modifier
                    .background(DbGoldDark, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "Ultra HD 4K",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }

        // Center simulated Player Screen Canvas!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.Center)
                .background(Color(0xFF07070F))
        ) {
            // Draw custom energy flares charging up with player progress!
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val chargeRadius = (size.width * 0.2f) * (screenProgress + 0.4f)

                // Draw central ki aura energy blast
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x9900E5FF),
                            Color(0x331E56B1),
                            Color.Transparent
                        )
                    ),
                    radius = chargeRadius,
                    center = center
                )

                // Kamehameha firing line
                if (screenProgress > 0.8f) {
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFF00E5FF), Color.Transparent)
                        ),
                        start = center,
                        end = Offset(size.width, center.y),
                        strokeWidth = (20f * screenProgress)
                    )
                }
            }

            // Central Pulsing Play Overlay
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "كاميهاميها قيد الشحن: ${(screenProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = DbGoldTertiary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "تشغيل",
                    tint = Color.White.copy(alpha = 0.82f),
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        // Bottom cinematic playback manager panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            // Progress Bar Slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "08:12",
                    fontSize = 10.sp,
                    color = DbTextMuted,
                    fontFamily = FontFamily.Monospace
                )

                Slider(
                    value = screenProgress,
                    onValueChange = onProgressChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = DbOrangePrimary,
                        activeTrackColor = DbOrangePrimary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )

                Text(
                    text = episode.durationAr,
                    fontSize = 10.sp,
                    color = DbTextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Core playback items row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleMute) {
                    Icon(
                        imageVector = if (state.isAppMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                        contentDescription = "كتم",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        val prev = (screenProgress - 0.1f).coerceAtLeast(0f)
                        onProgressChange(prev)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close, // Skip back mock
                        contentDescription = "تراجع",
                        tint = Color.White
                    )
                }

                // Center main play action button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(DbOrangePrimary, CircleShape)
                        .clickable { onProgressChange(if (screenProgress >= 0.99f) 0f else screenProgress + 0.15f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (screenProgress >= 0.99f) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = "إيقاف",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = {
                        val next = (screenProgress + 0.1f).coerceAtMost(1f)
                        onProgressChange(next)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow, // Fast forward mock
                        contentDescription = "أمام",
                        tint = Color.White
                    )
                }

                // Aspect ratio / Zoom
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Tv,
                        contentDescription = "ملء الشاشة",
                        tint = DbGoldTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ==========================================
// OVERLAY 3: HIGH-FIDELITY MANGA READER VIEW
// ==========================================

@Composable
fun MangaReaderOverlay(
    chapter: DbChapter,
    state: UiState,
    onClose: () -> Unit,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageNum = state.mangaPageNumber
    val totalPages = state.totalMangaPages

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF15151A))
            .testTag("immersive_manga_reader")
    ) {
        // A. Header Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.8f))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "إغلاق",
                    tint = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = chapter.titleAr,
                    fontSize = 12.sp,
                    color = DbGoldTertiary,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "تصفح مريح • قراءة من اليمين إلى اليسار ◀◀",
                    fontSize = 9.sp,
                    color = DbTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Display page counter
            Box(
                modifier = Modifier
                    .background(DbOrangePrimary, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "صفحة $pageNum / $totalPages",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // B. Main Comic Canvas page representation (Custom manga drawing with action scenes!)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .align(Alignment.Center)
                .padding(24.dp)
                .shadow(16.dp, RoundedCornerShape(12.dp))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background comic screentones
                CharacterBackground(categoryTitle = "manga_super")

                // Draw specific stylized Saiyan elements based on the page number
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw manga panels boundaries
                    drawLine(Color.Black, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = 4f)
                    drawLine(Color.Black, Offset(0f, h * 0.6f), Offset(w, h * 0.6f), strokeWidth = 4f)

                    // Draw an abstract vector of Goku's charging blast or hair spikes!
                    val pathGoku = Path().apply {
                        moveTo(w * 0.2f, h * 0.3f)
                        lineTo(w * 0.25f, h * 0.2f)
                        lineTo(w * 0.3f, h * 0.32f)
                        lineTo(w * 0.36f, h * 0.18f)
                        lineTo(w * 0.42f, h * 0.32f)
                        lineTo(w * 0.48f, h * 0.15f)
                        lineTo(w * 0.5f, h * 0.35f)
                    }
                    drawPath(pathGoku, color = Color.Black, style = Stroke(width = 6f))
                }

                // Arabic speech text balloon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 10.dp, y = (-20).dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (pageNum % 2 == 1) "كاميهاميهاااااا!!!" else "لن تستطيع هزيمة قوة السايان الغريزية!",
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                }

                // Page dynamic illustration title in Arabic
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "الفصل المـوجز • الصفحة $pageNum",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DbGoldTertiary
                        )
                        Text(
                            text = "انظر إلى قتال صراع جوهان بيست وغوكو الغريزة الفائقة لمستقبل أفضل للكون!",
                            fontSize = 9.sp,
                            color = DbTextLight,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // C. Bottom Navigation arrows (Arabic localized right-to-left swipe triggers)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.8f))
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Next Page" in Arabic manga slides to the LEFT (Since they read RTL)
            Button(
                onClick = onNextPage,
                enabled = pageNum < totalPages,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DbOrangePrimary,
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(44.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("الصفحة التالية (يسار)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // "Previous Page" in Arabic manga slides to the RIGHT
            Button(
                onClick = onPrevPage,
                enabled = pageNum > 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DbSurfaceVariantDark,
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .height(44.dp)
            ) {
                Text("الصفحة السابقة (يمين)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}
