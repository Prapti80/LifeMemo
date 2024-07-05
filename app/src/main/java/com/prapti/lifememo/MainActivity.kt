package com.prapti.lifememo


import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.prapti.lifememo.ui.theme.LifeMemoTheme
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


setContent {
     LifeMemoTheme {

         navController = rememberNavController()
         // A surface container using the 'background' color from the theme
         Surface(
             modifier = Modifier.fillMaxSize()
         ) {
             SetupNavGraph()
         }
     }}
}
}