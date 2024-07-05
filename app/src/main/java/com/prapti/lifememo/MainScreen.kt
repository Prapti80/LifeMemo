package com.prapti.lifememo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController


@Composable
fun HomeScreen(navController: NavController) {
    val backgroundImage: Painter = painterResource(id = R.drawable.main) // replace with your background image resource

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Welcome to LifeMemo",
            fontSize = 40.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp),
            fontFamily = FontFamily.Cursive
        )

        val appLogo: Painter = painterResource(id = R.drawable.app_logo) // replace with your logo resource
        Image(
            painter = appLogo,
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 32.dp)
        )
        Text(
            text = "What do you want now?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .padding(bottom = 32.dp),
            fontFamily = FontFamily.Serif,
        )

                ToolButton(
                    text = "Emotion Tracking",
                    backgroundColor = Color(0xFFE88D67),
                    onClick = { navController.navigate("emotion_tracker") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ToolButton(
                    text = "Note journal",
                    backgroundColor = Color(0xFF667BC6),
                    onClick = { navController.navigate("journal_app") }
                )


    }
}
}
@Composable
fun ToolButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun home() {
    HomeScreen(navController = rememberNavController())
}