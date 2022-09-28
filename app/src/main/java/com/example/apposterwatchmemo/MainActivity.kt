package com.example.apposterwatchmemo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.apposterwatchmemo.databinding.ActivityMainBinding
import okhttp3.internal.notify

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter by lazy { MainListAdapter(Glide.with(this)){ id,imgUrl,title,content ->
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("main_id", id)
        intent.putExtra("main_imgUri", imgUrl)
        intent.putExtra("main_title", title)
        intent.putExtra("main_content", content)
        startActivity(intent)
    }}

    // 뷰모델 초기화의 다른 방법. 해당 viewModel 이 초기화되는 Activity 혹은 Fragment 의 생명주기에 종속됨
    private val viewModel by lazy { ViewModelProvider(this@MainActivity, ViewModelProvider.AndroidViewModelFactory(application))[MainListViewModel::class.java]}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.recycler.adapter = adapter

        // 플로팅버튼 클릭 시 편집화면으로 전환
        binding.fab.setOnClickListener { startActivity(Intent(this@MainActivity, EditActivity::class.java)) }

        viewModel.mainListLiveData.observe(this@MainActivity){
            adapter.submitList(it)
            setUiVisibility()
        }
    }

    private fun setUiVisibility(){
        if(adapter.itemCount == 0){
            binding.noItemMain.visibility = View.VISIBLE
            binding.recycler.visibility = View.GONE
        } else {
            binding.noItemMain.visibility = View.GONE
            binding.recycler.visibility = View.VISIBLE
        }
    }
}

class MainListAdapter(private val requestManager: RequestManager,val onClick: (Int,String,String,String)->Unit): ListAdapter<MainListModel, MainListAdapter.VH>(diffUtil){
    inner class VH(itemView:View) : RecyclerView.ViewHolder(itemView){
        val img by lazy { itemView.findViewById<ImageView>(R.id.iv_main_list) }
        val title by lazy { itemView.findViewById<TextView>(R.id.tv_title_main_list) }
        val content by lazy { itemView.findViewById<TextView>(R.id.tv_content_main_list) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        requestManager.load(getItem(position).imgUrl).into(holder.img)
        if (getItem(position).imgUrl.contains("http")) holder.img.setPadding(16, 16, 16, 16)
        holder.title.text = getItem(position).title
        holder.content.text = getItem(position).content

        holder.itemView.setOnClickListener {

            // * error : 처음 혹은 중간에 위치한 아이템이 delete 된 후, position 이 갱신되지 않은 상태로 리스너가 추가되므로 IndexOutOfBoundsException 발생

            // 1. 단순 분기처리로 할수 없음.....position 이 꼬이는뎅
//            if(position==itemCount) {
//                onClick.invoke(
//                    getItem(position-1).id,
//                    getItem(position-1).imgUrl,
//                    getItem(position-1).title!!,
//                    getItem(position-1).content!!
//                )
//                return@setOnClickListener
//            }else onClick.invoke(
//                getItem(position).id,
//                getItem(position).imgUrl,
//                getItem(position).title!!,
//                getItem(position).content!!
//            )

            // 2. 어댑터 갱신. 두 번 클릭해야 DetailAc 으로 넘어감.. return 빼면 오류
            if (position == itemCount) {
                notifyDataSetChanged()
                return@setOnClickListener
            }else{
                onClick.invoke(
                    getItem(position).id,
                    getItem(position).imgUrl,
                    getItem(position).title!!,
                    getItem(position).content!!
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MainListModel>(){
            override fun areItemsTheSame(oldItem: MainListModel, newItem: MainListModel): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: MainListModel, newItem: MainListModel): Boolean =
                oldItem == newItem
        }
    }

}