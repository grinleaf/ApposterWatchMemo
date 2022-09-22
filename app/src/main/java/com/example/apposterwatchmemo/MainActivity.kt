package com.example.apposterwatchmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.apposterwatchmemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var testlist = listOf<MainListModel>()
    private val adapter by lazy { MainListAdapter(Glide.with(this)) }


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
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.recycler.adapter = adapter

        // 플로팅버튼 클릭 시 편집화면으로 전환
        binding.fab.setOnClickListener { startActivity(Intent(this@MainActivity, EditActivity::class.java)) }
    }
    fun getAllItem(){
        testlist = viewModel.selectAllItem()
        adapter.submitList(testlist)

        if(testlist.isEmpty()){
            binding.noItemMain.visibility = View.VISIBLE
            binding.recycler.visibility = View.GONE
        } else {
            binding.noItemMain.visibility = View.GONE
            binding.recycler.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getAllItem()
        adapter.submitList(testlist)
    }
}

class MainListAdapter(private val requestManager: RequestManager): ListAdapter<MainListModel, MainListAdapter.VH>(diffUtil){
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
        holder.title.text = getItem(position).title
        holder.content.text = getItem(position).content

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("main_id", getItem(position).id)
            intent.putExtra("main_imgUri", getItem(position).imgUrl)
            intent.putExtra("main_title", getItem(position).title)
            intent.putExtra("main_content", getItem(position).content)
            it.context.startActivity(intent)
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