package com.example.apposterwatchmemo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WatchListAPI {

    // 프리미엄 인기순
    @GET("/api/watch-sells/popular/weekly")
    fun getWatchList(
        @Query("skip") skip:Int,
        @Query("limit") limit:Int,
        @Query("withoutFree") withoutFree:Boolean
    ): WatchListResponse

    // 프리미엄 신규
    @GET("api/watch-sells")
    suspend fun  getNewWatchList(
        @Query("limit") limit:Int,
        @Query("page") page:Int,
        @Query("includeEvent") includeEvent:Boolean
    ) : WatchListResponse

}