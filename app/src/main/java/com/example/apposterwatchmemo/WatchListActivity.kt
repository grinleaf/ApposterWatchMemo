package com.example.apposterwatchmemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.apposterwatchmemo.databinding.ActivityWatchListBinding

class WatchListActivity : AppCompatActivity() {
    val binding by lazy { ActivityWatchListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}