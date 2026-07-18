package com.vishalsharma.whatwewear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vishalsharma.whatwewear.ui.theme.WhatWeWearTheme
import com.vishalsharma.whatwewear.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    }

