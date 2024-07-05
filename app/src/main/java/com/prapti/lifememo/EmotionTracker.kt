// EmotionTracker UI
package com.prapti.lifememo

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prapti.lifememo.database.Emotion
import com.prapti.lifememo.viewmodel.AppViewModelFactory
import com.prapti.lifememo.viewmodel.EmotionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.res.Resources
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionTracker(
    emotionViewModel: EmotionViewModel = viewModel(factory = AppViewModelFactory.Factory)
) {
    val emotionTimes by emotionViewModel.emotionTimes.observeAsState(Pair(null, null))
    val emotions by emotionViewModel.allEmotions.observeAsState(initial = emptyList())
    val emotionCounts by emotionViewModel.getEmotionsCount().observeAsState(initial = emptyMap())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State to hold the currently selected emotion for animation
    var selectedEmotion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emotion Tracker", fontFamily = FontFamily.Serif) },
                Modifier.background(Color(0xFF6200EA)),
            )
        }, contentColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "Your current feeling:",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Serif,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .fillMaxSize()) {
                    Row(modifier = Modifier.padding(horizontal = 50.dp)) {
                        EmojiButton("😊", Color(0xFF76FF03)) {
                            emotionViewModel.insert(
                                Emotion(
                                    emotion = "happy",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                        EmojiButton("😢", Color(0xFF03A9F4)) {
                            emotionViewModel.insert(
                                Emotion(
                                    emotion = "sad",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                        EmojiButton("😡", Color(0xFFFF1744)) {
                            emotionViewModel.insert(
                                Emotion(
                                    emotion = "angry",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    Row(modifier = Modifier.padding(horizontal = 50.dp)) {
                        EmojiButton("😱", Color(0xFFFFEA00)) {
                            emotionViewModel.insert(
                                Emotion(
                                    emotion = "surprised",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                        EmojiButton("😴", Color(0xFF7C4DFF)) {
                            emotionViewModel.insert(
                                Emotion(
                                    emotion = "sleepy",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                        EmojiButton("😐", Color(0xFF009688)) {
                            emotionViewModel.insert(
                                Emotion(
                                    emotion = "neutral",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            }
            if (emotionCounts.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Emotion Analysis:",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        color = Color.Blue,
                        fontWeight = FontWeight.W100,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                item {
                    Text(
                        text = "Nothing to Show",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W100,
                        fontFamily = FontFamily.Serif,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            item {
                val pieChartData = PieChartData(
                    slices = emotionCounts.map { (emotion, count) ->
                        val color = when (emotion) {
                            "happy" -> Color(0xFF76FF03)
                            "sad" -> Color(0xFF03A9F4)
                            "angry" -> Color(0xFFFF1744)
                            "surprised" -> Color(0xFFFFEA00)
                            "sleepy" -> Color(0xFF7C4DFF)
                            "neutral" -> Color(0xFF009688)
                            else -> Color(0xFFEC9F05) // Default color for other emotions
                        }
                        PieChartData.Slice("$emotion ${getEmoji(emotion)}", count.toFloat(), color)
                    },
                    plotType = PlotType.Pie
                )
                val pieChartConfig = PieChartConfig(
                    percentVisible = true,
                    isAnimationEnable = true,
                    showSliceLabels = false,
                    animationDuration = 1500
                )
                if (emotionCounts.isNotEmpty()) {
                    PieChart(
                        modifier = Modifier
                            .width(300.dp) // Medium size
                            .height(300.dp),
                        pieChartData,
                        pieChartConfig
                    ) { slice ->
                        // Set the selected emotion for animation
                        selectedEmotion = slice.label
                    }
                } else {
                    Text(text = "No emotion data found")
                }
            }

            item {
                if (emotionCounts.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        EmotionSummary(emotionTimes)
                    }
                }
            }
            item {
                // Refresh Button
                if (emotionCounts.isNotEmpty()) {
                    Button(onClick = {
                        scope.launch {
                            emotionViewModel.clearEmotions()
                            Toast.makeText(context, "Emotion data cleared", Toast.LENGTH_SHORT).show()
                        }
                    },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Refresh Data")
                    }
                } else {
                    Text(text = "")
                }
            }
        }

        // Animated display of selected emotion
        selectedEmotion?.let { emotion ->
            AnimatedEmotionLabel(
                emotion = emotion,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.LightGray)
            )
        }
    }
}


@Composable
fun EmotionSummary(emotionTimes: Pair<Long?, Long?>) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
    val startTime = emotionTimes.first?.let { dateFormat.format(Date(it)) } ?: "N/A"
    val endTime = emotionTimes.second?.let { dateFormat.format(Date(it)) } ?: "N/A"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Start Time: $startTime")
            Text("End Time: $endTime")
        }
    }
}

@Composable
fun EmojiButton(emoji: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        shape = CircleShape,
        modifier = Modifier
            .size(80.dp)
            .background(backgroundColor)
    ) {
        Text(emoji, fontSize = 30.sp)
    }
}

@Composable
fun AnimatedEmotionLabel(emotion: String, modifier: Modifier) {
    // Example animation
    Text(
        text = emotion,
        fontSize = 24.sp,
        color = Color.Black,
        modifier = modifier
            .padding(16.dp)
            .animateContentSize() // Animates the size change
    )
}

data class PieChartData(
    val slices: List<Slice>,
    val plotType: PlotType = PlotType.Pie
) {
    data class Slice(
        val label: String,
        val value: Float,
        val color: Color
    )
}

data class PieChartConfig(
    val strokeWidth: Float = 0f,
    val activeSliceAlpha: Float = 1f,
    val isAnimationEnable: Boolean,
    val sliceLabelTextSize: TextUnit = 16.sp,
    val sliceLabelTextColor: Color = Color.Black,
    val labelVisible: Boolean = true,
    val labelColor: Color = Color.Black,
    val isEllipsizeEnabled: Boolean = false,
    val chartPadding: Int = 8,
    val labelFontSize: TextUnit = 12.sp,
    val isClickOnSliceEnabled: Boolean = true,
    val showSliceLabels: Boolean,
    val percentVisible: Boolean,
    val animationDuration: Int
)

enum class PlotType {
    Pie
}

fun getEmoji(emotion: String): String {
    return when (emotion) {
        "happy" -> "😊"
        "sad" -> "😢"
        "angry" -> "😡"
        "surprised" -> "😱"
        "sleepy" -> "😴"
        "neutral" -> "😐"
        else -> "❓"
    }
}
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: PieChartData,
    config: PieChartConfig,
    onSliceClick: (PieChartData.Slice) -> Unit = {}
) {
    val total = data.slices.sumOf { it.value.toDouble() }.toFloat()
    var startAngle = 0f
    val radius = 150.dp.toPx() // Medium size radius

    Box(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 50.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Handle slice click events
                    var currentStartAngle = 0f
                    for (slice in data.slices) {
                        val sweepAngle = (slice.value / total) * 360f
                        val bounds = Rect(
                            offset = Offset.Zero,
                            size = Size(300.dp.toPx(), 300.dp.toPx())
                        )
                        if (isPointInArc(bounds, offset, currentStartAngle, sweepAngle)) {
                            onSliceClick(slice)
                            break
                        }
                        currentStartAngle += sweepAngle
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            data.slices.forEach { slice ->
                val sweepAngle = (slice.value / total) * 360f
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }
        }

        if (config.percentVisible) {
            var currentStartAngle = 0f
            data.slices.forEach { slice ->
                val sweepAngle = (slice.value / total) * 360f
                val percentage = (slice.value / total) * 100
                val angle = currentStartAngle + (sweepAngle / 2)
                val centerX = (radius + radius * 0.7f * cos(Math.toRadians(angle.toDouble()))).toFloat()
                val centerY = (radius + radius * 0.7f * sin(Math.toRadians(angle.toDouble()))).toFloat()

                BasicText(
                    text = "${percentage.toInt()}%",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = config.sliceLabelTextSize,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .offset(centerX.toDp(), centerY.toDp())
                        .padding(4.dp)
                )

                currentStartAngle += sweepAngle
            }
        }
    }
}

fun isPointInArc(bounds: Rect, point: Offset, startAngle: Float, sweepAngle: Float): Boolean {
    val centerX = bounds.center.x
    val centerY = bounds.center.y
    val radius = bounds.width / 2

    val dx = point.x - centerX
    val dy = point.y - centerY

    val distance = sqrt(dx * dx + dy * dy)

    if (distance > radius) {
        return false
    }

    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    val normalizedAngle = (angle + 360) % 360

    val start = (startAngle + 360) % 360
    val end = (start + sweepAngle) % 360

    return if (start < end) {
        normalizedAngle in start..end
    } else {
        normalizedAngle in start..360f || normalizedAngle in 0f..end
    }
}

fun Float.toDp(): Dp = (this / Resources.getSystem().displayMetrics.density).dp
fun Dp.toPx(): Float = (this.value * Resources.getSystem().displayMetrics.density)
