package com.example.spybrain.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spybrain.R
import java.util.Calendar
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size

/**
 * Р­РЅСЂР°РЅ СўРјРЅРѕРіРѕ РїСЂРёРІРµС‚СЃС‚РІРёСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 * @param isOffline РџСЂРёР·РЅР°Рє РѕС„С„Р»Р°Р№РЅ-СЂРµР¶РёРјР°.
 * @param onQuickAction Callback РґР»СЏ Р±С‹СЃС‚СЂС‹С… РґРµР№СЃС‚РІРёР№.
 */
@Composable
fun SmartWelcomeScreen(
    isOffline: Boolean = false,
    onQuickAction: (String) -> Unit = {}
) {
    // РџСЂРѕРІРµСЂСЏРµРј РїРѕРґРєР»СЋС‡РµРЅРёРµ Рє РёРЅС‚РµСЂРЅРµС‚Сў
    val context = LocalContext.current
    var isNetworkAvailable by remember { mutableStateOf(!isOffline) }

    // РџРµСЂРёРѕРґРёС‡РµСЃРєРё РїСЂРѕРІРµСЂСЏРµРј СЃРѕСЃС‚РѕСЏРЅРёРµ СЃРµС‚Рё
    LaunchedEffect(Unit) {
        while (true) {
            isNetworkAvailable = isNetworkConnected(context)
            delay(5000) // РџСЂРѕРІРµСЂРєР° РєР°Р¶РґС‹Рµ 5 СЃРµРєСўРґ
        }
    }

    // РћРїСЂРµРґРµР»СЏРµРј РІСЂРµРјСЏ СЃСўС‚РѕРє
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val (greeting, bgRes) = when (hour) {
        in 5..11 -> "Р”РѕР±СЂРѕРµ СўС‚СЂРѕ!" to com.example.spybrain.R.drawable.bg_water
        in 12..17 -> "Р”РѕР±С‹Р№ РґРµРЅСЊ!" to com.example.spybrain.R.drawable.bg_nature
        in 18..22 -> "Р”РѕР±С‹Р№ РІРµС‡РµСЂ!" to com.example.spybrain.R.drawable.bg_space
        else -> "Р”РѕР±СЂРѕР№ РЅРѕС‡Рё!" to com.example.spybrain.R.drawable.bg_air
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(greeting, style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            if (!isNetworkAvailable) {
                Text("РќРµС‚ СЃРѕРµРґРёРЅРёСЏ СЃ СЃРµС‚СЊСЋ", color = Color.Red)
            } else {
                // Р‘С‹СЃС‚СЂС‹Рµ РґРµР№СЃС‚РІРёСЏ
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onQuickAction("breathing") }) { Text("Р‘С‹СЃС‚СЂРѕРµ РґС‹С…РЅРёРµ") }
                    Button(onClick = { onQuickAction("meditation") }) { Text("Р‘С‹СЃС‚СЂР°СЏ РјРµРґРёС‚РёСЏ") }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            // РђРЅРёРјРёСЂРѕРІР°РЅРЅС‹Р№ РєСЂСўРіРѕРІРѕР№ РёРЅРґРёРєР°С‚РѕСЂ (РЅР°РїСЂРёРјРµСЂ, TTS)
            var progress by remember { mutableStateOf(0f) }

            // РђРЅРёРјРёСЂСўРµРј РїСЂРѕРіСЂРµСЃСЃ
            LaunchedEffect(Unit) {
                while(true) {
                    for (i in 0..100) {
                        progress = i / 100f
                        delay(50)
                    }
                    delay(1000) // Р—Р°РґРµСЂР¶РєР° РїРµСЂРµРґ РїРµСЂРµР·Р°РїСўСЃРѕРј Р°РЅРёРјР°С‚РёСЏ
                }
            }

            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(120.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * РџСЂРѕРІРµСЂСЏРµС‚ РЅР°Р»РёС‡РёРµ РёРЅС‚РµСЂРЅРµС‚-СЃРѕРµРґРёРЅРёСЏ.
 * @param context РљРѕРЅС‚РµРєСЃС‚.
 * @return true, РµСЃР»Рё РµСЃС‚СЊ РёРЅС‚РµСЂРЅРµС‚.
 */
fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
           capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
