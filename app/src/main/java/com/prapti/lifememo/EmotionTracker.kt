package com.prapti.lifememo

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.prapti.lifememo.database.Emotion
import com.prapti.lifememo.viewmodel.AppViewModelFactory
import com.prapti.lifememo.viewmodel.EmotionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
                val donutChartData = PieChartData(
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
                val donutChartConfig = PieChartConfig(
                    strokeWidth = 120f,
                    activeSliceAlpha = .9f,
                    isAnimationEnable = true,
                    sliceLabelTextSize = 20.sp,
                    sliceLabelTextColor = Color.White,
                    labelVisible = true,
                    labelColor = Color.Black,
                    isEllipsizeEnabled = true,
                    chartPadding = 25,
                    labelFontSize = 42.sp,
                    isClickOnSliceEnabled = true,
                    showSliceLabels = true
                )
                if (emotionCounts.isNotEmpty()) {
                    DonutPieChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        donutChartData,
                        donutChartConfig
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

data class Slice(
    val label: String,
    val value: Float,
    val color: Color
)

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
