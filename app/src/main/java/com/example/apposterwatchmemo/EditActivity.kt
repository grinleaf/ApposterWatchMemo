package com.example.apposterwatchmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.apposterwatchmemo.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    lateinit var getResultImage: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }

        // EditText 키보드 옵션 설정


        //
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    // 모어버튼 메뉴 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 내 폰에서 추가
            R.id.add_my_image -> {
                // 갤러리 앱 접근 권한 요청
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),200)
                }else{
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.run {
                        data =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        type = "image/*"
                    }
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                        if(result.resultCode == RESULT_OK){
                            val img = result.data?.data
                            Log.i("aaa", "$img")
                        }
                    }.launch(intent)
                }
            }

            // 페이스 리스트에서 추가
            R.id.add_face_list -> {

            }

            // 저장하기
            R.id.save_memo -> {

            }

            // 백버튼 설정
            android.R.id.home -> onBackPressed()
        }
    }
}