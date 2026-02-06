package com.example.laba

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.laba.ui.theme.LABATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showToast("onCreate")
        enableEdgeToEdge()
        setContent {
            LABATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MoodApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        showToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        showToast("onResume")
    }

    override fun onPause() {
        super.onPause()
        showToast("onPause")
    }

    override fun onStop() {
        super.onStop()
        showToast("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        showToast("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        showToast("onDestroy")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, "Состояние: $message", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    LABATheme {
        MoodApp()
    }
}