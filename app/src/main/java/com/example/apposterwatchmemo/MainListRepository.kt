package com.example.apposterwatchmemo

import androidx.lifecycle.LiveData

class MainListRepository(private val mainListDao: MainListDao) {
    private lateinit var mainList : LiveData<List<MainListModel>>

    fun getAll() : LiveData<List<MainListModel>>{
        mainList = mainListDao.getAll()
        return mainList
    }

    suspend fun insert(mainList:MainListModel){
        mainListDao.insert(mainList)
    }

    suspend fun update(mainList:MainListModel){
        mainListDao.update(mainList)
    }

    suspend fun delete(mainList:MainListModel){
        mainListDao.delete(mainList)
    }

//    fun deleteAll(){
//        viewModelScope.launch{
//            mainListDao.deleteAll()
//        }
//    }
}