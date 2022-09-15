package com.example.apposterwatchmemo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WatchListRepository {
    val baseUrl = "http://mtm-api.apposter.com:7777"
    val skip = 9
    val limit = 8
    val withoutFree = true

    val retrofit by lazy {
        Retrofit.Builder().baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder().addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                ).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WatchListAPI::class.java)
    }

    fun getWatchList(): Flow<PagingData<Preview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
            ),
            pagingSourceFactory = { WatchListPagingSource(retrofit, skip, limit, withoutFree) }
        ).flow
    }
}