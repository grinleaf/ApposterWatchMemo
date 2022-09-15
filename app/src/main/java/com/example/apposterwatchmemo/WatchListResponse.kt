package com.example.apposterwatchmemo

//data class WatchListResponse(val images:MutableList<Images>)
//
//data class Images(val images:Preview)
//
//data class Preview(val preview:String)


data class WatchListResponse(val count: Int, val watchSells: MutableList<Watch>)

data class Watch(val watch:Images)

data class Images(val images:Preview)

data class Preview(val preview:String)