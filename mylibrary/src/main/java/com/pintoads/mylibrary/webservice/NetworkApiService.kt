package com.pintoads.himasdk.webservice

import com.pintoads.himasdk.webservice.models.AppKysResponse
import com.pintoads.himasdk.webservice.models.BaseResponse
import com.pintoads.himasdk.webservice.models.ReportClickRequest
import com.pintoads.himasdk.webservice.models.RequestAdModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

internal interface NetworkApiService {

    @GET("/api/user_info/")
    fun getUserInfo(): Observable<BaseResponse<Any>>

    @POST("/api/ads/requests/")
    @FormUrlEncoded
    fun getAd(@Field("ads_type") adType: Int): Observable<BaseResponse<RequestAdModel>>

    @POST("/api/ads/redis/clicks/")
    fun reportForClick(@Body reportClickRequest: ReportClickRequest): Observable<BaseResponse<Any>>

    @POST("/api/ads/redis/seens/")
    fun reportForSeens(@Body reportClickRequest: ReportClickRequest): Observable<BaseResponse<Any>>


    /** getAppKys **/
    @POST("/api/ads/app_keys/")//todo save in base64
    @FormUrlEncoded
    fun oOoo(@Field("app_key") app_key: String): Observable<BaseResponse<AppKysResponse>>


    @GET("/api/user_info/")
    fun getUserInfo2(): Call<BaseResponse<Any>>

}