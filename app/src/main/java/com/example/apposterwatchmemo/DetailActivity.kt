package com.example.apposterwatchmemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.apposterwatchmemo.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}