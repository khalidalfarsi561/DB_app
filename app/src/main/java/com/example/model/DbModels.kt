package com.example.model

enum class DbType {
    ANIME, MANGA, MOVIE, SPECIAL
}

enum class DbCategory(val titleAr: String, val titleEn: String) {
    ORIGINAL("دراغون بول الكلاسيكي", "Dragon Ball Classic"),
    Z("دراغون بول Z", "Dragon Ball Z"),
    KAI("دراغون بول كاي", "Dragon Ball Kai"),
    GT("دراغون بول GT", "Dragon Ball GT"),
    SUPER("دراغون بول سوبر", "Dragon Ball Super"),
    DAIMA("دراغون بول دايما", "Dragon Ball Daima"),
    MOVIES("الأفلام والخاصة", "Movies & Specials")
}

data class DragonBallMedia(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val imageUrl: String, // We'll render premium custom gradient layouts & canvas/drawables if no internet
    val bannerUrl: String,
    val type: DbType,
    val category: DbCategory,
    val rating: Double,
    val year: String,
    val episodesCount: Int = 0,
    val chaptersCount: Int = 0,
    val author: String = "Akira Toriyama & Toyotarou",
    val statusAr: String = "مكتمل",
    val statusEn: String = "Completed"
)

data class DbEpisode(
    val id: String,
    val seriesId: String,
    val titleAr: String,
    val titleEn: String,
    val episodeNumber: Int,
    val durationAr: String,
    val durationEn: String,
    val releaseDate: String,
    val descriptionAr: String = ""
)

data class DbChapter(
    val id: String,
    val seriesId: String,
    val titleAr: String,
    val titleEn: String,
    val chapterNumber: Int,
    val pagesCount: Int,
    val releaseDate: String
)
