package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import com.example.model.DragonBallMedia
import com.example.model.DbRepository
import com.example.model.DbCategory
import com.example.model.DbType
import com.example.model.DbEpisode
import com.example.model.DbChapter

data class UiState(
    val mediaList: List<DragonBallMedia> = emptyList(),
    val filteredList: List<DragonBallMedia> = emptyList(),
    val categories: List<DbCategory> = DbCategory.values().toList(),
    val selectedCategory: DbCategory? = null,
    val selectedTypeFilter: DbType? = null, // null for both, or ANIME, MANGA
    val searchQuery: String = "",
    val favorites: Set<String> = emptySet(),
    val activeDetailMedia: DragonBallMedia? = null,
    val currentPlayingEpisode: DbEpisode? = null,
    val currentReadingChapter: DbChapter? = null,
    val isAppMuted: Boolean = false,
    val videoPlaybackProgress: Float = 0.35f,
    val mangaPageNumber: Int = 1,
    val totalMangaPages: Int = 45,
    val activeTab: NavTab = NavTab.HOME
)

enum class NavTab {
    HOME, BROWSE, FAVORITES, SETTINGS
}

class DragonBallViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(mediaList = DbRepository.seriesList))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // Initialize dynamic lists
        applyFilters()
    }

    fun selectCategory(category: DbCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun selectTypeFilter(type: DbType?) {
        _uiState.value = _uiState.value.copy(selectedTypeFilter = type)
        applyFilters()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleFavorite(mediaId: String) {
        val currentFavs = _uiState.value.favorites.toMutableSet()
        if (currentFavs.contains(mediaId)) {
            currentFavs.remove(mediaId)
        } else {
            currentFavs.add(mediaId)
        }
        _uiState.value = _uiState.value.copy(favorites = currentFavs)
        applyFilters()
    }

    fun selectMediaDetail(media: DragonBallMedia?) {
        _uiState.value = _uiState.value.copy(activeDetailMedia = media)
    }

    fun playEpisode(episode: DbEpisode) {
        _uiState.value = _uiState.value.copy(
            currentPlayingEpisode = episode,
            videoPlaybackProgress = 0f
        )
    }

    fun stopPlayback() {
        _uiState.value = _uiState.value.copy(currentPlayingEpisode = null)
    }

    fun updatePlaybackProgress(progress: Float) {
        _uiState.value = _uiState.value.copy(videoPlaybackProgress = progress.coerceIn(0f, 1f))
    }

    fun readChapter(chapter: DbChapter) {
        _uiState.value = _uiState.value.copy(
            currentReadingChapter = chapter,
            mangaPageNumber = 1,
            totalMangaPages = chapter.pagesCount
        )
    }

    fun nextMangaPage() {
        val next = _uiState.value.mangaPageNumber + 1
        if (next <= _uiState.value.totalMangaPages) {
            _uiState.value = _uiState.value.copy(mangaPageNumber = next)
        }
    }

    fun prevMangaPage() {
        val prev = _uiState.value.mangaPageNumber - 1
        if (prev >= 1) {
            _uiState.value = _uiState.value.copy(mangaPageNumber = prev)
        }
    }

    fun closeMangaReader() {
        _uiState.value = _uiState.value.copy(currentReadingChapter = null)
    }

    fun changeActiveTab(tab: NavTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isAppMuted = !_uiState.value.isAppMuted)
    }

    private fun applyFilters() {
        val state = _uiState.value
        var source = state.mediaList

        // Filter by Category
        if (state.selectedCategory != null) {
            source = source.filter { it.category == state.selectedCategory }
        }

        // Filter by Media Type (Anime vs Manga)
        if (state.selectedTypeFilter != null) {
            source = source.filter { it.type == state.selectedTypeFilter }
        }

        // Filter by Search Query
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.trim().lowercase()
            source = source.filter {
                it.titleAr.lowercase().contains(q) ||
                it.titleEn.lowercase().contains(q) ||
                it.descriptionAr.lowercase().contains(q) ||
                it.descriptionEn.lowercase().contains(q)
            }
        }

        _uiState.value = state.copy(filteredList = source)
    }
}
