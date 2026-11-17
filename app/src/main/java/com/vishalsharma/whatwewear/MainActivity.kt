package com.vishalsharma.whatwewear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vishalsharma.whatwewear.ui.theme.WhatWeWearTheme
import com.vishalsharma.whatwewear.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        hideSystemNavigationBar()
        setContent {
            WhatWeWearTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ){
                        AppNavGraph()
                    }
                }
            }
        }
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        // Hide it again when you come back from the camera, a picker or a dialog
//        if (hasFocus) hideSystemNavigationBar()
//    }
//
//    private fun hideSystemNavigationBar() {
//        val controller = WindowCompat.getInsetsController(window, window.decorView)
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        controller.hide(WindowInsetsCompat.Type.navigationBars())
//    }
    }

