package com.example.apposterwatchmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.apposterwatchmemo.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }

    val id by lazy { intent.getIntExtra("main_id",0) }
    val imgUri by lazy { intent.getStringExtra("main_imgUri").toString() }
    val title by lazy { intent.getStringExtra("main_title").toString() }
    val content by lazy { intent.getStringExtra("main_content").toString() }

    // Room 라이브러리 적용 : MainListViewModel
    private val viewModel by lazy { ViewModelProvider(this@DetailActivity, ViewModelProvider.AndroidViewModelFactory(application))[MainListViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }

        // 화면 진입 시 데이터 이전
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    // 모어버튼 메뉴 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 편집 : 해당 리스트 데이터를 EditActivity 화면(데이터 채워진 상태)에서 변경
            R.id.edit_memo -> {
                val intent = Intent(this@DetailActivity, EditActivity::class.java)
                intent.putExtra("detail_id", id)
                intent.putExtra("detail_imgUri", imgUri)
                intent.putExtra("detail_title", title)
                intent.putExtra("detail_content", content)
                startActivity(intent)
                finish()
            }

            // 삭제 : 해당 리스트 데이터를 MainActivity 의 리스트에서 제거 + MainActivity 리스트 갱신
            R.id.delete_memo -> {
                val deleteItem = MainListModel(id,imgUri,title,content)
                viewModel.deleteItem(deleteItem)
                finish()
            }

            // 백버튼 설정
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    fun initData(){
        if(imgUri.isNotBlank()) Glide.with(this@DetailActivity).load(imgUri).into(binding.ivEdit)
        if(title.isNotBlank()) binding.tvTitleEdit.text = title
        if(content.isNotBlank()) binding.tvContentEdit.text = content
    }
}