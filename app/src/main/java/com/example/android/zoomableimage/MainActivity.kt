package com.example.android.zoomableimage

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.android.zoomableimage.components.ImagePagerScreen
import com.example.android.zoomableimage.ui.theme.ZoomableImageTheme
import com.example.android.zoomableimage.ui.theme.darkNeutral
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChangeSystemBarColor(darkNeutral)
            ZoomableImageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImagePagerScreen(
                        imgList = mutableListOf(
                            "https://images.pexels.com/photos/4156467/pexels-photo-4156467.jpeg?cs=srgb&dl=pexels-breston-kenya-4156467.jpg&fm=jpg",
                            "https://images.unsplash.com/photo-1592313794735-b9558968c0c7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=766&q=80",
                            "https://img.freepik.com/free-photo/vegetables-set-left-black-slate_1220-685.jpg?w=1480&t=st=1672825066~exp=1672825666~hmac=27f7087969290acbd7bc9d0ce28af5c0d381c8d7498708f8d679c8b5137f627f",
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ChangeSystemBarColor(color: Color = darkNeutral) {
    rememberSystemUiController().setSystemBarsColor(color, darkIcons = false)
}
