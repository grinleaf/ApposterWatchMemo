package com.example.apposterwatchmemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.apposterwatchmemo.databinding.ActivityWatchListBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WatchListActivity : AppCompatActivity() {
    val binding by lazy { ActivityWatchListBinding.inflate(layoutInflater) }
    private val adapter by lazy{ WatchListPagingAdapter(Glide.with(this@WatchListActivity)) }
    private val watchListViewModel by lazy {
        ViewModelProvider(this).get(WatchListViewModel::class.java)
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

        binding.recycler.adapter = adapter

        lifecycleScope.launch{
            watchListViewModel.getImage().collectLatest { pagingData ->
                adapter.run {
                    Log.i("aaa","${adapter.itemCount }} --> onCreate lifecycleScope area")
                    submitData(pagingData)
                    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
                        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                            super.onItemRangeChanged(positionStart, itemCount)
                            if(adapter.itemCount == itemCount)
                                binding.recycler.smoothScrollToPosition(adapter.itemCount-1)
                        }
                    })
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}

const val STARTING_PAGE_INDEX = 1

class WatchListPagingSource(
    val watchListAPI : WatchListAPI,
    val skip :Int,
    val limit :Int,
    val withoutFree :Boolean
): PagingSource<Int, Preview>(){

    override fun getRefreshKey(state: PagingState<Int, Preview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(STARTING_PAGE_INDEX) ?: anchorPage?.nextKey?.minus(STARTING_PAGE_INDEX)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Preview> {
        return try{
            val position = params.key?: STARTING_PAGE_INDEX
            val watchList = watchListAPI.getWatchList(skip,limit, withoutFree).watchSells.map { it.watch.images }
            Log.i("aaa", "$watchList --> PagingSource area")

            LoadResult.Page(
                data = watchList,
                prevKey = if(position == STARTING_PAGE_INDEX) null else position-1,
                nextKey = if(watchList.isEmpty()) null else position+1
            )
        } catch (exception : Exception){
            LoadResult.Error(exception)
        }
    }
}

class WatchListPagingAdapter(val requestManager: RequestManager): PagingDataAdapter<Preview, WatchListPagingAdapter.VH>(diffUtil){
    inner class VH(view: View):RecyclerView.ViewHolder(view){
        val iv:ImageView by lazy { view.findViewById(R.id.iv_watchlist) }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val imgUrl = WatchListRepository().baseUrl + getItem(position)?.preview

        requestManager.load(imgUrl).into(holder.iv)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.list_item_watchlist, parent, false))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Preview>(){
            override fun areItemsTheSame(oldItem: Preview, newItem: Preview): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: Preview, newItem: Preview): Boolean = oldItem == newItem
        }
    }
}