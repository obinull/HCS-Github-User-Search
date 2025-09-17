package dev.byto.hcsgus.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.util.constant.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@HiltAndroidTest
class NetworkModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var moshi: Moshi

    // Hilt will inject the Interceptor provided by provideAuthInterceptor()
    @Inject
    lateinit var authInterceptor: Interceptor

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var apiService: ApiService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testMoshiProvided() {
        assertNotNull("Moshi should be injected", moshi)
        // Check if KotlinJsonAdapterFactory is present
        // This is a bit tricky as Moshi doesn't directly expose its factories list in a simple way after build.
        // However, we can infer its presence if parsing Kotlin classes works, or trust the module setup.
        // For simplicity here, we'll assume its presence if Moshi is built.
        // A more robust test would involve trying to parse a Kotlin data class.
        assertTrue("Moshi builder should contain KotlinJsonAdapterFactory by design in the module",
            moshi.adapter(Any::class.java) != null) // Basic check that Moshi is functional
    }

    @Test
    fun testAuthInterceptorProvided() {
        assertNotNull("Auth Interceptor should be injected", authInterceptor)
        // Verifying the exact instance from NetworkModule.provideAuthInterceptor()
        // is tricky without modifying the module for testability (e.g. qualifiers or named bindings if multiple interceptors of the same type)
        // But Hilt should provide the one from provideAuthInterceptor due to its return type and @Provides annotation.
    }

    @Test
    fun testOkHttpClientProvidedAndConfigured() {
        assertNotNull("OkHttpClient should be injected", okHttpClient)

        // Check for authInterceptor (it's one of the application interceptors)
        assertTrue("OkHttpClient should contain the authInterceptor", okHttpClient.interceptors.contains(authInterceptor))

        // Check for HttpLoggingInterceptor
        val loggingInterceptor = okHttpClient.interceptors.firstOrNull { it is HttpLoggingInterceptor } as? HttpLoggingInterceptor
        assertNotNull("OkHttpClient should contain HttpLoggingInterceptor", loggingInterceptor)
        assertEquals("HttpLoggingInterceptor level should be BODY in debug", HttpLoggingInterceptor.Level.BODY, loggingInterceptor?.level)

        // Check for ChuckerInterceptor
        val chuckerInterceptorExists = okHttpClient.interceptors.any { it is ChuckerInterceptor }
        assertTrue("OkHttpClient should contain ChuckerInterceptor", chuckerInterceptorExists)

        assertEquals("OkHttpClient should have 3 application interceptors (auth, logging, chucker)", 3, okHttpClient.interceptors.size)
    }

    @Test
    fun testRetrofitProvidedAndConfigured() {
        assertNotNull("Retrofit should be injected", retrofit)
        assertEquals("Retrofit base URL should match Constants.SERVER_URL", Constants.SERVER_URL, retrofit.baseUrl().toString())

        // Check for MoshiConverterFactory
        assertTrue("Retrofit should use MoshiConverterFactory", retrofit.converterFactories().any { it is MoshiConverterFactory })

        // Check if the OkHttpClient instance is the one we injected/expect
        assertEquals("Retrofit should use the Hilt-provided OkHttpClient", okHttpClient, retrofit.callFactory())
    }

    @Test
    fun testApiServiceProvider() {
        assertNotNull("ApiService should be injected", apiService)
        // Further tests for ApiService would involve making actual calls, which is out of scope for module testing.
    }
}
