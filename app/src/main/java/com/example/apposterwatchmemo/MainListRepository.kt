package com.example.apposterwatchmemo

class MainListRepository(private val mainListDao: MainListDao) {
    private val mainList : List<MainListModel> = mainListDao.getAll()

    fun getAll() : List<MainListModel>{
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