package com.example.apposterwatchmemo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WatchListAPI {

    // 홈 인기순 주간 워치 페이스 리스트 API
//    @GET("/api/watches/popular/weekly")
//    fun getWatchList()
//    : WatchListResponse

    // 프리미엄 인기순
    @GET("/api/watch-sells/popular/weekly")
    fun getWatchList(
        @Query("skip") skip:Int,
        @Query("limit") limit:Int,
        @Query("withoutFree") withoutFree:Boolean
    ): WatchListResponse

}