package com.example.apposterwatchmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.apposterwatchmemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val testlist = mutableListOf<MainItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.recycler.adapter = MainListAdapter(Glide.with(this))

        //TODO: ViewModel, LiveData
        val testurl = "http://mtm-api.apposter.com:7777/watches/vymocpcorc/preview?updatedat=1658217014647"
//        testlist.submitList(Mainitem(testurl,"첫번째","첫번째메모내용 첫번째메모내용 첫번째메모내용 첫번째메모내용 첫번째메모내용 첫번째메모내용 첫번째메모내용"))

        // 플로팅버튼 클릭 시 편집화면으로 전환
        binding.fab.setOnClickListener { startActivity(Intent(this@MainActivity, EditActivity::class.java)) }
    }
}

class MainListAdapter(val requestManager: RequestManager): ListAdapter<String, MainListAdapter.VH>(diffUtil){
    inner class VH(itemView:View) : RecyclerView.ViewHolder(itemView){
        val img by lazy { itemView.findViewById<ImageView>(R.id.iv_main_list) }
        val title by lazy { itemView.findViewById<TextView>(R.id.tv_title_main_list) }
        val content by lazy { itemView.findViewById<TextView>(R.id.tv_content_main_list) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //TODO: ViewModel, LiveData 전달
//        requestManager.load(getItem(position).imgUrl).into(holder.img)
//        holder.title.text = getItem(position).title
//        holder.content.text = getItem(position).content
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>(){
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }

}