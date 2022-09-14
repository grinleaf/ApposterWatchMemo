package com.example.apposterwatchmemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainListViewModel(private val mainListDao: MainListDao):ViewModel() {

    private fun insertItem(mainListModel: MainListModel){
        viewModelScope.launch {
            mainListDao.insert(mainListModel)
        }
    }

    private fun getNewItemEntry(imgUrl:String, title:String, content:String) : MainListModel{
        return MainListModel(
            imgUrl = imgUrl,
            title = title,
            content = content
        )
    }

    fun addNewItem(imgUrl:String, title:String, content:String) {
        val newItem = getNewItemEntry(imgUrl,title, content)
        insertItem(newItem)
    }

    fun isEntryValid(imgUrl:String, title:String, content:String) : Boolean{
        if(imgUrl.isBlank() || title.isBlank() || content.isBlank()){
            return false
        }
        return true
    }

    fun deleteItem(mainListModel: MainListModel){
        viewModelScope.launch {
            mainListDao.delete(mainListModel)
        }
    }

    fun selectAllItem() : List<MainListModel>{
        return mainListDao.getAll()
    }

    fun updateItem(mainListModel: MainListModel){
        viewModelScope.launch {
            mainListDao.update(mainListModel)
        }
    }
}

class MainListViewModelFactory(private val mainListDao: MainListDao) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainListViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainListViewModel(mainListDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}