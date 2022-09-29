package com.example.apposterwatchmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.apposterwatchmemo.databinding.ActivityMainBinding

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


        // * AdapterDataObserver : 어댑터 내의 데이터 변동을 감지하는 옵저버 추상 클래스. 어댑터 내의 아이템 포지션 값을 가져와 이벤트 처리하기 좋을 것 같음. 해당 메소드 외에 값 추가/제거/범위 변경 시점에 대한 이벤트 처리 및 제어도 가능
//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
//            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
//                super.onItemRangeRemoved(positionStart, itemCount)
//                adapter.submitList(viewModel.mainListLiveData.value)
//            }
//        })

        binding.recycler.adapter = adapter


        // 플로팅버튼 클릭 시 편집화면으로 전환
        binding.fab.setOnClickListener { startActivity(Intent(this@MainActivity, EditActivity::class.java)) }

        viewModel.mainListLiveData.observe(this@MainActivity){
            setListToUi(it)
        }
    }

    fun setListToUi(liveData: List<MainListModel>){
        adapter.submitList(liveData)
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
        val item = getItem(position)
        requestManager.load(item.imgUrl).into(holder.img)
        if (item.imgUrl.contains("http")) holder.img.setPadding(16)
        holder.title.text = item.title
        holder.content.text = item.content

        holder.itemView.setOnClickListener {

            // * error : 처음 혹은 중간에 위치한 아이템이 delete 된 후, position 이 갱신되지 않은 상태로 리스너가 추가되므로 IndexOutOfBoundsException 발생

            // 1. 단순히 position 값만 바꿀 수 없음.....position 이 꼬이는뎅 --> 수식 만들면 가능하려남?
//            if(position==itemCount) {
//                onClick.invoke(
//                    getItem(position-1).id,
//                    getItem(position-1).imgUrl,
//                    getItem(position-1).title!!,
//                    getItem(position-1).content!!
//                )
//                return@setOnClickListener
//            }else onClick.invoke(
//                item.id,
//                item.imgUrl,
//                item.title!!,
//                item.content!!
//            )

            // 2. 어댑터 갱신. 두 번 클릭해야 DetailAc 으로 넘어감.. return 빼면 오류
//            if (position == itemCount) {
//                notifyDataSetChanged()
//                return@setOnClickListener
//            }
            onClick.invoke(
                item.id,
                item.imgUrl,
                item.title!!,
                item.content!!,
            )
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