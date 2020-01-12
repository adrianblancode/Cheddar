package co.adrianblan.hackernews

import co.adrianblan.hackernews.api.HackerNewsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
object HackerNewsModule {

    @Provides
    @Singleton
    @HackerNewsInternal
    fun provideHackerNewsApi(
        okHttpClient: Lazy<OkHttpClient>
    ): HackerNewsApi =
        Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .callFactory(
                object : Call.Factory {
                    override fun newCall(request: Request): Call =
                        okHttpClient.get().newCall(request)
                }
            )
            .build()
            .create(HackerNewsApi::class.java)
}

@Qualifier
@Retention
internal annotation class HackerNewsInternal