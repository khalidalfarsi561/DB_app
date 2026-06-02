package com.example.model

object DbRepository {

    val seriesList = listOf(
        DragonBallMedia(
            id = "dbs_daima",
            titleAr = "دراغون بول دايما (Daima)",
            titleEn = "Dragon Ball Daima",
            descriptionAr = "الملحمة الأحدث للكاتب الراحل أكيرا تورياما! بسبب مؤامرة غامضة، يتحول غوكو وأصدقاؤه إلى أطفال صغار. ينطلق غوكو برفقة شين وغولاريو في رحلة شيقة إلى عالم غامض ومجهول لإنقاذ الكون واستعادة أحجامهم الطبيعية.",
            descriptionEn = "Due to a conspiracy, Goku and his friends are turned small. What kind of adventure awaits Goku, Supreme Kai, and glorious new companions in this mysterious new world?",
            imageUrl = "daima_cover",
            bannerUrl = "daima_banner",
            type = DbType.ANIME,
            category = DbCategory.DAIMA,
            rating = 9.1,
            year = "2024",
            episodesCount = 20,
            statusAr = "مستمر",
            statusEn = "Ongoing"
        ),
        DragonBallMedia(
            id = "dbs_super",
            titleAr = "دراغون بول سوبر (Super)",
            titleEn = "Dragon Ball Super",
            descriptionAr = "بعد هزيمة ماجين بو، يعم السلام الأرض. ولكن سرعان ما تظهر قوى جديدة تفوق الخيال! ظهور إله الدمار بيروس، وعودة فريزر الذهبي، ملحمة غوكو بلاك والكون السادس وصولاً إلى بطولة القوة الأعظم في تاريخ الأكوان لإنقاذ الكون السابع.",
            descriptionEn = "Following the defeat of Majin Buu, peace has returned to Earth. But entities of cosmic power, like Beerus, God of Destruction, emerge to trigger a tournament of universes.",
            imageUrl = "super_cover",
            bannerUrl = "super_banner",
            type = DbType.ANIME,
            category = DbCategory.SUPER,
            rating = 8.8,
            year = "2015",
            episodesCount = 131,
            statusAr = "مكتمل",
            statusEn = "Completed"
        ),
        DragonBallMedia(
            id = "dbz",
            titleAr = "دراغون بول Z (ملحمة السايان والكون)",
            titleEn = "Dragon Ball Z",
            descriptionAr = "الملحمة الأسطورية الخالدة! نكتشف حقيقة غوكو الفضائية وكونه من عرق السايان الخارق. قتال راديتز، نابا، والامبراطور فريزر على ناميك، ثم غزو الأندرويد وسيل وصولاً إلى مواجهة ماجين بو المدمرة الكونية.",
            descriptionEn = "The masterpiece anime! Goku discovers his extraterrestrial heritage and defends Earth against formidable foes, including Saiyan Prince Vegeta, Frieza, Cell, and Majin Buu.",
            imageUrl = "dbz_cover",
            bannerUrl = "dbz_banner",
            type = DbType.ANIME,
            category = DbCategory.Z,
            rating = 9.2,
            year = "1989",
            episodesCount = 291,
            statusAr = "مكتمل",
            statusEn = "Completed"
        ),
        DragonBallMedia(
            id = "db_classic",
            titleAr = "دراغون بول الكلاسيكي (بداية الأسطورة)",
            titleEn = "Dragon Ball Classic",
            descriptionAr = "بداية كل شيء! مغامرات غوكو الصغير ذو الذيل مع بولما في رحلة البحث عن كرات التنين السبعة. قتال جيش الشريط الأحمر، بيكولو دايماو، وتدريب غوكو الأسطوري مع موتن روشي (غوتين).",
            descriptionEn = "The beginning of the legend! Young Goku on his legendary quest for the seven magical Dragon Balls, training under Master Roshi, and fighting the Red Ribbon Army.",
            imageUrl = "classic_cover",
            bannerUrl = "classic_banner",
            type = DbType.ANIME,
            category = DbCategory.ORIGINAL,
            rating = 8.5,
            year = "1986",
            episodesCount = 153,
            statusAr = "مكتمل",
            statusEn = "Completed"
        ),
        DragonBallMedia(
            id = "db_gt",
            titleAr = "دراغون بول GT (الرحلة الكونية وصيغة السوبر سايان 4)",
            titleEn = "Dragon Ball GT",
            descriptionAr = "جزء غير قانوني ولكنه محبوب جماهيرياً! تحول غوكو مجدداً إلى طفل بواسطة كرات التنين السوداء الغامضة وينطلق في رحلة طويلة مع حفيدته بان وترانكس في الفضاء الخارجي. يتميز بظهور هيئة السوبر سايان 4 الخارقة.",
            descriptionEn = "A cosmic journey through space! Goku is accidentally turned into a child by the Black Star Dragon Balls, unleashing the iconic Super Saiyan 4.",
            imageUrl = "gt_cover",
            bannerUrl = "gt_banner",
            type = DbType.ANIME,
            category = DbCategory.GT,
            rating = 7.4,
            year = "1996",
            episodesCount = 64,
            statusAr = "مكتمل",
            statusEn = "Completed"
        ),
        DragonBallMedia(
            id = "movie_broly",
            titleAr = "فيلم دراغون بول سوبر: برولي الأسطوري",
            titleEn = "Dragon Ball Super: Broly",
            descriptionAr = "أعظم وأقوى قتال بصري في تاريخ السلسلة! قتال ملحمي ومجنون بين غوكو وفيجيتا والسايان المنبوذ والأسطوري برولي بقوته اللانهائية، مما يضطر غوكو وفيجيتا إلى الاندماج والتحول لـ غوجيتا السوبر سايان غود بلو.",
            descriptionEn = "The ultimate animated display of Saiyan power. Goku and Vegeta must face Broly, a Saiyan of unfathomable strength, leading to the return of Gogeta.",
            imageUrl = "broly_cover",
            bannerUrl = "broly_banner",
            type = DbType.MOVIE,
            category = DbCategory.MOVIES,
            rating = 9.0,
            year = "2018",
            statusAr = "متوفر بدقة 4K",
            statusEn = "Available in 4K"
        ),
        DragonBallMedia(
            id = "movie_super_hero",
            titleAr = "فيلم دراغون بول سوبر: سوبر هيرو",
            titleEn = "Dragon Ball Super: Super Hero",
            descriptionAr = "يركز الفيلم على بيكولو وجوهان وتفجير طاقاتهم الكامنة ضد عودة جيش الشريط الأحمر وإصدارهم الخارق الأخير سيل ماكس. نشاهد قوى بيكولو البرتقالي وغوهان الوحش (Beast Gohan).",
            descriptionEn = "While Goku and Vegeta are away training, Gohan and Piccolo must defend Earth from the revived Red Ribbon Army and their ultimate creation, Cell Max.",
            imageUrl = "superhero_cover",
            bannerUrl = "superhero_banner",
            type = DbType.MOVIE,
            category = DbCategory.MOVIES,
            rating = 8.3,
            year = "2022",
            statusAr = "متوفر بدقة 4K",
            statusEn = "Available in 4K"
        ),
        DragonBallMedia(
            id = "manga_super",
            titleAr = "مانغا دراغون بول سوبر (الفصول الأسبوعية)",
            titleEn = "Dragon Ball Super Manga",
            descriptionAr = "المانغا الرسمية التي تستكمل أحداث القصة وتتجاوز أحداث أنمي سوبر التلفزيوني! تحتوي أراكات أسطورية كملحمة مورو أكل العوالم وملحمة غرانولا الأقوى بالكون، وصولاً لطور الغريزة الفائقة لغوكو والغرور الفائق لفيجيتا.",
            descriptionEn = "The continuation of the manga written and overseen by Akira Toriyama! Features legendary arcs like Moro and Granolah the Survivor, continuing Gohan Beast's glory.",
            imageUrl = "manga_super_cover",
            bannerUrl = "manga_super_banner",
            type = DbType.MANGA,
            category = DbCategory.SUPER,
            rating = 8.9,
            year = "2015",
            chaptersCount = 103,
            statusAr = "مستمر هيروها",
            statusEn = "Ongoing / Monthly"
        )
    )

    val episodesMap = mapOf(
        "dbs_daima" to listOf(
            DbEpisode("daima_1", "dbs_daima", "المؤامرة المجهولة - غوكو يتحول لطفل!", "The Conspiracy - Goku Becomes Kid", 1, "24:12", "24:12", "11/10/2024", "تبدأ المؤامرة الغامضة في العالم السفلي وتحويل جميع الأبطال لأطفال صغار."),
            DbEpisode("daima_2", "dbs_daima", "البطل الأسطوري الجديد - غولاريو يدخل الميدان", "The New Companion - Glorio Enters", 2, "23:45", "23:45", "18/10/2024", "غوكو يحاول الاعتياد على جسده الصغير بينما يعرض عليه غولاريو مرافقتهم للعالم الثالث للمملكة السفلى."),
            DbEpisode("daima_3", "dbs_daima", "الرحلة إلى عالم الشياطين الثالث", "Flight to the Third Demon World", 3, "24:00", "24:00", "25/10/2024", "طيران المغامرة وبداية استكشاف الأجواء البرتقالية الغريبة للمملكة الثالثة."),
            DbEpisode("daima_4", "dbs_daima", "مواجهة اللصوص في بلدة الثلج", "Encounter in Ice Town", 4, "23:10", "23:10", "01/11/2024", "مجموعة من قطاع الطرق تعترض غوكو وشين وغولاريو، وغوكو يستعرض مهارات العصا السحرية.")
        ),
        "dbs_super" to listOf(
            DbEpisode("super_110", "dbs_super", "استيقاظ الغريزة الفائقة لغوكو ضد جيرن الأقوى!", "Awakening of Gokus Ultra Instinct", 110, "25:30", "25:30", "08/10/2017", "غوكو يواجه جيرن الذي لا يقهر ويطلق جينكي داما عظيمة ترتد ليمتصها غوكو ويحصل غامضاً على الغريزة الفائقة!"),
            DbEpisode("super_129", "dbs_super", "تجاوز الحدود وتجلي الغريزة الفائقة المكتملة!", "Mastered Ultra Instinct Manifests", 129, "25:00", "25:00", "04/03/2018", "غوكو ينفجر بهالة فضية ساطعة محققاً السيطرة الكاملة على الغريزة الفائقة ضد جيرن الثائر."),
            DbEpisode("super_130", "dbs_super", "القتال الكوني الأعظم - غوكو ضد جيرن بكل طاقتهم", "The Ultimate Showdown - Goku vs Jiren", 130, "24:50", "24:50", "18/03/2018", "قتال جنوني يقسم الحلبة، غوكو يهزم جيرن بالكامل ولكن جسده ينهار في اللحظات الأخيرة ليدخل فريزر."),
            DbEpisode("super_131", "dbs_super", "النهاية المعجزة! غوكو وفريزر وأندرويد 17 ينقذون الكون!", "The Miraculous Conclusion! Goku and Frieza Join Hands", 131, "26:15", "26:15", "25/03/2018", "غوكو بهيئة القاعدة العادية يتعاون بملحمية وتنسيق أسطوري مع فريزر الذهبي للإطاحة بجيرن خارج الحلبة ليفوز الاندرويد 17 برغبة الكرات الكونية.")
        ),
        "dbz" to listOf(
            DbEpisode("dbz_95", "dbz", "انفجار الأسطورة! غوكو السوبر سايان الذهبي ضد فريزر", "Goku Transforms to Legendary Super Saiyan", 95, "24:30", "24:30", "12/06/1991", "بعد تصفية فريزر لـ كريلين، صديق طفولة غوكو، يغضب غوكو غضباً يزلزل الكوكب ويتحول للسوبر سايان الذهبي الأسطوري لأول مرة في تاريخ السلسلة."),
            DbEpisode("dbz_229", "dbz", "صراع الكبرياء الساياني - ماجين فيجيتا ضد غوكو", "Pride Renewed - Majin Vegeta vs Goku", 229, "24:00", "24:00", "14/09/1994", "فيجيتا يستسلم لوخز بابيدي الشرير لكي يفتح قوته الكامنة ويجبر غوكو على قتال كبرياء حاسم.")
        ),
        "db_classic" to listOf(
            DbEpisode("classic_1", "db_classic", "غوكو وبولما - انطلاق أعظم رحلة بالوجود!", "Goku and Bulma - The Magical Mission Starts", 1, "24:12", "24:12", "26/02/1986", "غوكو يربي نفسه بالغابة ويلتقي بالفتاة بولما ذات العشرة والمخترعة الذكية ويبدأن الرحلة للبحث عن الدارغون بولز.")
        ),
        "db_gt" to listOf(
            DbEpisode("gt_35", "gt_classic", "التحول الأقوى بجري روج! السوبر سايان 4 يتجلى", "Super Saiyan 4 Appears", 35, "24:00", "24:00", "11/12/1996", "غوكو يعود لوعيه بعد رؤية الأرض كهيئة البدر في شكل القرد الذهبي ليتحول لهيئة السوبر سايان 4 الأيقونية ذات الفراء الأحمر.")
        )
    )

    val chaptersMap = mapOf(
        "manga_super" to listOf(
            DbChapter("manga_103", "manga_super", "الفصل 103: إرث وعهد جوهان بيست ضد غوكو الغريزة الفائقة", "Chapter 103: Inherited Future - Gohan Beast vs UI Goku", 103, 45, "21/04/2024"),
            DbChapter("manga_102", "manga_super", "الفصل 102: صراع الابن والوالد - طاقة الوحش ضد الغريزة الكونية", "Chapter 102: Father vs Son - Beast Gohan vs Ultra Instinct", 102, 42, "20/03/2024"),
            DbChapter("manga_101", "manga_super", "الفصل 101: كارمين يختطف القتال - كارثة الاندرويد الجديدة", "Chapter 101: Carmine and Soldier Red - Android Redux", 101, 39, "18/02/2024"),
            DbChapter("manga_85", "manga_super", "الفصل 85: تمرد فيجيتا والغرور الفائق (Ultra Ego)", "Chapter 85: Ego Unleashed vs Gas of Heaters", 85, 41, "20/06/2022")
        )
    )
}
