package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a highly polished, 3D-looking 4-Star Dragon Ball using Compose Canvas.
 * Complete with radial glowing gradients, glossy highlights, and red stars.
 */
@Composable
fun DragonBallSphere(
    modifier: Modifier = Modifier,
    starCount: Int = 4,
    pulse: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_pulse")
    val glowScale by if (pulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = glowScale, scaleY = glowScale),
        contentAlignment = Alignment.Center
    ) {
        // Base glassmorphic glowing circle with 3D gradient shadows
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Glowing yellow-orange core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFEA7A), // Hot bright yellow center
                        DbGoldTertiary,     // Golden core
                        DbOrangePrimary,    // Warm energetic orange body
                        Color(0xFFB32400)   // Dark deep orange rim
                    ),
                    center = Offset(center.x - radius * 0.15f, center.y - radius * 0.15f),
                    radius = radius * 1.1f
                ),
                radius = radius,
                center = center
            )

            // Outer golden glow line
            drawCircle(
                color = Color(0xFFFFB74D).copy(alpha = 0.5f),
                radius = radius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Glossy crescent highlights for glass reflection
            drawPath(
                path = Path().apply {
                    addArc(
                        oval = androidx.compose.ui.geometry.Rect(
                            radius * 0.1f,
                            radius * 0.1f,
                            size.width - radius * 0.1f,
                            size.height - radius * 0.1f
                        ),
                        startAngleDegrees = -120f,
                        sweepAngleDegrees = 60f
                    )
                },
                color = Color.White.copy(alpha = 0.7f),
                style = Stroke(width = radius * 0.08f)
            )

            // Bottom subtle counter-reflection shadow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(center.x + radius * 0.3f, center.y + radius * 0.3f),
                    radius = radius * 0.5f
                ),
                radius = radius * 0.5f,
                center = Offset(center.x + radius * 0.2f, center.y + radius * 0.2f)
            )
        }

        // Star-mesh layout drawn in red in the ball core
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.48f)
                .offset(y = (-2).dp)
        ) {
            val w = size.width
            val h = size.height
            val starRadius = w * 0.14f

            // Red star paint color
            val starColor = Color(0xFFFF1744)

            // Draw stars based on the count requested (we focus on the iconic 4 stars layout by default)
            when (starCount) {
                1 -> {
                    drawFivePointStar(Offset(w / 2, h / 2), starRadius, starColor)
                }
                2 -> {
                    drawFivePointStar(Offset(w * 0.35f, h / 2), starRadius, starColor)
                    drawFivePointStar(Offset(w * 0.65f, h / 2), starRadius, starColor)
                }
                3 -> {
                    drawFivePointStar(Offset(w / 2, h * 0.3f), starRadius, starColor)
                    drawFivePointStar(Offset(w * 0.28f, h * 0.68f), starRadius, starColor)
                    drawFivePointStar(Offset(w * 0.72f, h * 0.68f), starRadius, starColor)
                }
                4 -> {
                    // Iconic Diamond/Z-shaped 4 stars layout
                    drawFivePointStar(Offset(w / 2, h * 0.25f), starRadius, starColor) // Top
                    drawFivePointStar(Offset(w * 0.25f, h / 2), starRadius, starColor) // Left
                    drawFivePointStar(Offset(w * 0.75f, h / 2), starRadius, starColor) // Right
                    drawFivePointStar(Offset(w / 2, h * 0.75f), starRadius, starColor) // Bottom
                }
                else -> {
                    // Standard vertical layout
                    for (i in 0 until starCount) {
                        val angle = (i * 2 * PI / starCount) - PI / 2
                        val x = (w / 2) + cos(angle).toFloat() * (w * 0.3f)
                        val y = (h / 2) + sin(angle).toFloat() * (h * 0.3f)
                        drawFivePointStar(Offset(x, y), starRadius * 0.9f, starColor)
                    }
                }
            }
        }
    }
}

/**
 * Extension for DrawScope to Draw a star path.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFivePointStar(
    center: Offset,
    outerRadius: Float,
    color: Color
) {
    val innerRadius = outerRadius * 0.45f
    val starPath = Path()
    val totalPoints = 5
    var currentAngle = -PI / 2 // Start pointing up

    for (i in 0 until totalPoints * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + cos(currentAngle).toFloat() * r
        val y = center.y + sin(currentAngle).toFloat() * r
        if (i == 0) {
            starPath.moveTo(x, y)
        } else {
            starPath.lineTo(x, y)
        }
        currentAngle += PI / totalPoints
    }
    starPath.close()
    drawPath(starPath, color)
}

/**
 * Super Saiyan Energy Aura effect that loops in the background.
 */
@Composable
fun SuperSaiyanAura(
    modifier: Modifier = Modifier,
    auraColor: Color = DbGoldTertiary,
    isPulsing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val alphaAnim by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 0.40f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
    } else {
        remember { mutableStateOf(0.3f) }
    }

    val blurRadiusFloat by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 16f,
            targetValue = 28f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blur"
        )
    } else {
        remember { mutableStateOf(20f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .blur(blurRadiusFloat.dp)
            .graphicsLayer(alpha = alphaAnim)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        auraColor,
                        auraColor.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Renders an abstract representation of the specific anime series/movie as premium background
 * graphics in Compose. This ensures we show beautiful Saiyan-themed graphics without network image fails.
 * E.g., Super Saiyan Aura for DBZ, Galactic Aura for DB Super, and Retro grids for Classic.
 */
@Composable
fun CharacterBackground(
    categoryTitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0E14))
    ) {
        when (categoryTitle) {
            "dbs_daima" -> {
                // Energetic sunset red and infant clouds representation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB74D), Color(0xFFC2185B).copy(alpha = 0.7f)),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        )
                )
                // Cute aura sparkles
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                        .blur(30.dp)
                        .background(Color.White.copy(alpha = 0.6f), CircleShape)
                )
            }
            "dbs_super" -> {
                // Super Saiyan Royal Blue & Ultra Instinct silver
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF03A9F4).copy(alpha = 0.8f),
                                    Color(0xFF3F51B5),
                                    Color(0xFF9C27B0).copy(alpha = 0.6f),
                                    Color(0xFF03A9F4).copy(alpha = 0.8f)
                                )
                            )
                        )
                )
                SuperSaiyanAura(auraColor = Color(0xFF00E5FF), isPulsing = true)
            }
            "dbz" -> {
                // Pure Super Saiyan Golden aura and battle dust
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFE65100), Color(0xFFFFB300), Color(0xFF1A0A00))
                            )
                        )
                )
                SuperSaiyanAura(auraColor = Color(0xFFFFEB3B), isPulsing = true)
            }
            "db_classic" -> {
                // Retro sky-blue nostalgia with yellow flying nimbus cloud layout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF00ACC1), Color(0xFF80DEEA), Color(0xFFFFCC80))
                            )
                        )
                )
                // Flying nimbus representations (soft yellow circles)
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = 30.dp)
                        .size(100.dp, 60.dp)
                        .blur(15.dp)
                        .background(Color(0xFFFFD54F), CircleShape)
                )
            }
            "db_gt" -> {
                // Crimson dark fusion representing SSJ4 wild fur (Deep maroon & black electric aura)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF26081C), Color(0xFF8E0E29), Color(0xFF000000))
                            )
                        )
                )
                SuperSaiyanAura(auraColor = Color(0xFFFF5252), isPulsing = true)
            }
            "manga_super" -> {
                // High-contrast manga screentones with dramatic diagonal action lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw clean dark background
                    drawRect(color = Color(0xFFECEFF1))

                    // Draw abstract manga panel borders
                    drawLine(
                        color = Color(0xFF263238),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF263238),
                        start = Offset(size.width * 0.7f, 0f),
                        end = Offset(size.width * 0.3f, size.height),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw speed line vectors
                    for (i in 0..12) {
                        val offset = i * (size.width / 12)
                        drawLine(
                            color = Color(0xFF37474F).copy(alpha = 0.25f),
                            start = Offset(offset, 0f),
                            end = Offset(offset + 100f, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }
            else -> {
                // Cosmic dark universe gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(DbSurfaceVariantDark, DbBackgroundDark, Color.Black)
                            )
                        )
                )
            }
        }

        // Top-left brand watermark
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.6f), MaterialTheme.shapes.extraSmall)
                .padding(vertical = 2.dp, horizontal = 6.dp)
        ) {
            Text(
                text = "DB UNIVERSE ENGINE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = DbGoldTertiary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
