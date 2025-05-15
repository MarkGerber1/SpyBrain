package com.example.spybrain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ReminderService : Service() {
    companion object {
        const val ACTION_SHOW_REMINDER = "com.example.spybrain.ACTION_SHOW_REMINDER"
        const val EXTRA_REMINDER_ID = "com.example.spybrain.EXTRA_REMINDER_ID"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
} 