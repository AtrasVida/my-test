package com.pintoads.himasdk.webservice

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.pintoads.himasdk.BuildConfig
import com.pintoads.himasdk.webservice.models.AppKysResponse
import com.pintoads.himasdk.webservice.models.BaseResponse
import com.pintoads.himasdk.webservice.models.ReportClickRequest
import com.pintoads.himasdk.webservice.models.RequestAdModel
import com.pintoads.mylibrary.BuildConfig
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Consumer
import kotlin.math.pow

object ApiClient : Consumer<Throwable> {
    private var networkApiService: NetworkApiService? = null
    private val ContentType = "application/json"
    private const val TAG = "API_CLIENT"

    @Throws(Exception::class)
    override fun accept(throwable: Throwable) {
        val throwableClass: Class<*> = throwable.javaClass
        if (throwableClass == SocketTimeoutException::class.java || UnknownHostException::class.java == throwableClass) {
            Log.e(TAG, "accept: UnknownHostException")
        } else if (JsonSyntaxException::class.java.isAssignableFrom(throwableClass)) {
            Log.e(TAG, "accept: JsonSyntaxException ")
        }
    }


    private val apiCallTransformer: ObservableTransformer<*, *> =
        ObservableTransformer<Any, Any> { observable: Observable<*> ->
            observable.map { appResponse -> appResponse }
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay()).doOnError(this)
                .observeOn(AndroidSchedulers.mainThread())
        }

    private fun <T> configureApiCallObserver(): ObservableTransformer<T, T> {
        return apiCallTransformer as ObservableTransformer<T, T>
    }


    init {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        var okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        val token = getToken()
        if (token != null) {
            okHttpClientBuilder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                request.addHeader("Authorization", "Bearer $token")
                chain.proceed(request.build())
            }
        }

        // Config Gson
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        gsonBuilder.registerTypeAdapter(BaseResponse::class.java, Deserializer<BaseResponse<Any>>())

        // Init Retrofit
        networkApiService = Retrofit.Builder()
            .baseUrl(URL.BASE_URL)
            .client(okHttpClientBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build()
            .create(NetworkApiService::class.java)
    }

    private fun getToken(): String? {
        //   if (AppDataManager.usersInfo != null)
        //       return AppDataManager.usersInfo?.token
        //   else
        //       if (AppDatabases.getInstance() != null)
        //           if (AppDatabases.getInstance()!!.userDao().getOneById() != null)
        //               return AppDatabases.getInstance()!!.userDao().getOneById()!!.token
        return null

    }

    internal class RetryWithDelay :
        Function<Observable<out Throwable?>, ObservableSource<*>> {
        private val maxRetries = 3
        private var retryCount = 0
        @Throws(Exception::class)
        override fun apply(attempts: Observable<out Throwable?>): Observable<*> {
            return attempts
                .flatMap { throwable: Throwable? ->
                    if (throwable is TimeoutException || throwable is SocketTimeoutException) {
                        if (++retryCount < maxRetries) {
                            return@flatMap Observable.timer(
                                2.0.pow(retryCount.toDouble()).toLong(),
                                TimeUnit.SECONDS
                            )
                        }
                    }
                    Observable.error<Any?>(throwable)
                }
        }
    }


    fun getUsersInfo(onSuccess: (BaseResponse<Any>) -> Unit) = networkApiService!!
        .getUserInfo()
        .compose(configureApiCallObserver())
        .subscribeWith(object : MyDisposableObserver<BaseResponse<Any>>(onSuccess) {})


    internal fun getAd(ad_type: Int, onSuccess: (BaseResponse<RequestAdModel>) -> Unit) =
        networkApiService!!
            .getAd(ad_type)
            .compose(configureApiCallObserver())
            .subscribeWith(object :
                MyDisposableObserver<BaseResponse<RequestAdModel>>(onSuccess) {})


    internal fun reportForClick(
        reportClickRequest: ReportClickRequest,
        onSuccess: (BaseResponse<Any>) -> Unit
    ) = networkApiService!!
        .reportForClick(reportClickRequest)
        .compose(configureApiCallObserver())
        .subscribeWith(object : MyDisposableObserver<BaseResponse<Any>>(onSuccess) {})

    internal fun reportForSeens(
        reportClickRequest: ReportClickRequest,
        onSuccess: (BaseResponse<Any>) -> Unit
    ) = networkApiService!!
        .reportForSeens(reportClickRequest)
        .compose(configureApiCallObserver())
        .subscribeWith(object : MyDisposableObserver<BaseResponse<Any>>(onSuccess) {})

    /** getAppKys **/
    internal fun oOoo(
        app_key: String,
        onSuccess: (BaseResponse<AppKysResponse>) -> Unit
    ) = networkApiService!!
        .oOoo(app_key)
        .compose(configureApiCallObserver())
        .subscribeWith(object : MyDisposableObserver<BaseResponse<AppKysResponse>>(onSuccess) {})


}


