package com.example.apposterwatchmemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

class WatchListViewModel(): ViewModel() {
    val watchListRepository = WatchListRepository()

    fun getImage() : Flow<PagingData<Preview>> {
        return watchListRepository.getWatchList().cachedIn(viewModelScope)
    }
}