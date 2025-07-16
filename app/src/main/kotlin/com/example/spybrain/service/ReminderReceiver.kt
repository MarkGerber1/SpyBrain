package com.example.spybrain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.spybrain.service.ReminderService.Companion.ACTION_SHOW_REMINDER
import com.example.spybrain.service.ReminderService.Companion.EXTRA_REMINDER_ID
import android.util.Log

/**
 * BroadcastReceiver РґР»СЏ РѕР±СЂР°Р±РѕС‚РєРё РЅР°РїРѕРјРёРЅР°РЅРёР№, Р·Р°РїСѓСЃРєР°РµРјС‹С… С‡РµСЂРµР· AlarmManager
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Reminder received: ${intent.action}")

        when (intent.action) {
            ACTION_SHOW_REMINDER -> {
                val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID)
                if (reminderId != null) {
                    // Р—Р°РїСѓСЃРєР°РµРј СЃРµСЂРІРёСЃ РґР»СЏ РѕР±СЂР°Р±РѕС‚РєРё РЅР°РїРѕРјРёРЅР°РЅРёСЏ
                    val serviceIntent = Intent(context, ReminderService::class.java).apply {
                        action = ACTION_SHOW_REMINDER
                        putExtra(EXTRA_REMINDER_ID, reminderId)
                    }
                    context.startService(serviceIntent)
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Перезапускаем все запланированные напоминания
                // после перезагрузки устройства
                val serviceIntent = Intent(context, ReminderService::class.java).apply {
                    action = ReminderService.ACTION_SCHEDULE_REMINDERS
                }
                context.startService(serviceIntent)
            }
        }
    }

    companion object {
        private const val TAG = "ReminderReceiver"
    }
}
