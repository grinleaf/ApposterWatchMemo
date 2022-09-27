package com.example.apposterwatchmemo

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MainListViewModel(private val a_repository: MainListRepository):ViewModel() {
    private val repository = MainListRepository(
        WatchMemoApplication().database.mainListDao()
    )
    val mainListLiveData = MutableLiveData<List<MainListModel>>()

    init {
        mainListLiveData.value = repository.getAll()
    }

    private fun insertItem(mainListModel: MainListModel){
        viewModelScope.launch {
            repository.insert(mainListModel)
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
            repository.delete(mainListModel)
        }
    }

    fun updateItem(mainListModel: MainListModel){
        viewModelScope.launch{
            repository.update(mainListModel)
        }
    }
}

class MainListViewModelFactory(private val repository: MainListRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainListViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}