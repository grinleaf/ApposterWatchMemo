package com.example.apposterwatchmemo

import android.app.Application

class WatchMemoApplication:Application() {
    val database: MainDatabase by lazy { MainDatabase.getDatabase(this) }
    //요렇게 사용하면 매우 위험함 ~! 싱글톤 싱글톤
    val repository by lazy { MainListRepository(database!!.mainListDao()) }
}