package com.example.laba

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Без разрешения мотивационные цитаты не придут!", Toast.LENGTH_LONG).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Уведомления Дневника"
            val descriptionText = "Канал для мотивационных цитат и системных сообщений"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel("MOOD_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showToast("onCreate")
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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