package com.example.apposterwatchmemo

import android.app.Application

class WatchMemoApplication:Application() {
    val database: MainDatabase by lazy { MainDatabase.getDatabase(this) }
}