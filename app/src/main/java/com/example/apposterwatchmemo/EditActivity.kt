package com.example.apposterwatchmemo

import android.Manifest
import android.content.Intent
import android.content.LocusId
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.apposterwatchmemo.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    private var itemState = false
    private var isWatchList = false

    // DetailActivity 에서 편집버튼으로 진입 시
    val detailId by lazy { intent.getIntExtra("detail_id", 0) }
    lateinit var detailImgUrl:String
    val detailTitle by lazy { intent.getStringExtra("detail_title").toString() }
    val detailContent by lazy { intent.getStringExtra("detail_content").toString() }

    lateinit var imgUri: String
    private val requestGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imgUri = result.data?.data!!.toString()
                Glide.with(this@EditActivity).load(imgUri).into(binding.ivEdit)
            }
        }

    // Room 라이브러리 적용 : MainListViewModel
    private val viewModel: MainListViewModel by viewModels {
        MainListViewModelFactory(
            (application as WatchMemoApplication).database.mainListDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }

        // ADD or UPDATE 여부 확인 : 기본키가 default 값이면 데이터 없음
        itemState = if(detailId==0) ADD_DATE else UPDATE_DATE
        if(itemState== UPDATE_DATE) initData() else return

        // '내용' EditText 입력옵션 설정
        binding.tvContentEdit.run {
            addTextChangedListener {
                if(lineCount>6) setLines(lineCount) else setLines(6)
            }
        }
    }

    private fun isEntryValid(imgUri: String):Boolean{
        return viewModel.isEntryValid(
            imgUri, //이미지 URL 가져오는 코드로 수정
            binding.tvTitleEdit.text.toString(),
            binding.tvContentEdit.text.toString()
        )
    }

    private fun addNewItem(imgUri:String){
        if(isEntryValid(imgUri)){
            viewModel.addNewItem(
                imgUri, //이미지 URL 가져오는 코드로 수정
                binding.tvTitleEdit.text.toString(),
                binding.tvContentEdit.text.toString()
            )
        }
    }

    private fun updateItem(id:Int, imgUri: String, title: String, content:String){
        return viewModel.updateItem(MainListModel(id,imgUri,title,content))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    // 모어버튼 메뉴 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 1. 내 폰에서 추가 : 디바이스 갤러리 앱에서 데이터 선택 후 돌아오기
            R.id.add_my_image -> {
                // 갤러리 앱 접근 권한 요청
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),1)
                }else{
                    // 권한이 이미 있을 경우, 갤러리 앱 실행
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
                    requestGalleryLauncher.launch(intent)
                }
            }

            // 2. 페이스 리스트에서 추가 : WatchListActivity 에서 데이터 선택 후 돌아오기
            R.id.add_face_list -> {
                startActivity(Intent(this@EditActivity, WatchListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }

            // 3. 저장하기 : MainActivity 로 전환 및 리스트 갱신
            R.id.save_memo -> {
                if(binding.tvTitleEdit.text.isBlank()){
                    Toast.makeText(this@EditActivity, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }else if(binding.tvContentEdit.text.isBlank()){
                    Toast.makeText(this@EditActivity, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }

                when (itemState) {
                    ADD_DATE -> {
                        // 새로운 아이템 저장 분기 : DB에 아이템 저장
                        addNewItem(imgUri)
                        startActivity(Intent(this@EditActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    }
                    UPDATE_DATE -> {
                        // 기존 아이템 편집 분기 : DB 아이템 수정
                        val title = binding.tvTitleEdit.text.toString()
                        val content = binding.tvContentEdit.text.toString()
                        val updateItem = MainListModel(detailId, imgUri, title, content)
                        viewModel.updateItem(updateItem)
                        startActivity(Intent(this@EditActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    }
                    else -> {
                        Toast.makeText(this@EditActivity, "예외 발생 !", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 백버튼 설정
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    private fun initData(){
        detailImgUrl = intent.getStringExtra("detail_imgUri").toString()
        isWatchList = intent.getBooleanExtra("by_watchlist", BY_MAIN)
        if(isWatchList == BY_WATCHLIST) {
            Glide.with(this@EditActivity).load(detailImgUrl).into(binding.ivEdit)
            return
        }
        else {
            Glide.with(this@EditActivity).load(detailImgUrl).into(binding.ivEdit)
            if (!binding.tvTitleEdit.text.isBlank()) binding.tvTitleEdit.setText(detailTitle)
            if (!binding.tvContentEdit.text.isBlank()) binding.tvContentEdit.setText(detailContent)
        }
    }

    companion object {
        const val ADD_DATE = false
        const val UPDATE_DATE = true
        const val BY_MAIN = false
        const val BY_WATCHLIST = true
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
}