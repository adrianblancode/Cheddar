package co.adrianblan.hackernews

import co.adrianblan.hackernews.api.HackerNewsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
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

    private val json = Json {
        ignoreUnknownKeys = true
        // Disabling alternative names to generate correct serializer for ApiComment
        // https://github.com/Kotlin/kotlinx.serialization/issues/1512
        useAlternativeNames = false
    }

    @Provides
    @Singleton
    @HackerNewsInternal
    fun provideHackerNewsApi(
        okHttpClient: Lazy<OkHttpClient>
    ): HackerNewsApi =
        Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(
                @OptIn(ExperimentalSerializationApi::class)
                json.asConverterFactory("application/json".toMediaType())
            )
            .callFactory { request -> okHttpClient.get().newCall(request) }
            .build()
            .create(HackerNewsApi::class.java)
}

@Qualifier
@Retention
internal annotation class HackerNewsInternal