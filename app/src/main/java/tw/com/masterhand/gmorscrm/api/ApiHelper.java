package tw.com.masterhand.gmorscrm.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tw.com.masterhand.gmorscrm.BuildConfig;
import tw.com.masterhand.gmorscrm.Constants;
import tw.com.masterhand.gmorscrm.model.Approve;
import tw.com.masterhand.gmorscrm.model.ApproveMulti;
import tw.com.masterhand.gmorscrm.model.ModifyApprovers;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.SyncRecord;
import tw.com.masterhand.gmorscrm.model.SyncSetting;
import tw.com.masterhand.gmorscrm.model.UploadRecord;
import tw.com.masterhand.gmorscrm.model.UserPassword;
import tw.com.masterhand.gmorscrm.model.ValidLocation;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.GsonGMTDateAdapter;

public class ApiHelper {
    public interface TranslateApi {
        //        @Headers({"Content-Type: application/json"})
        @POST("https://translation.googleapis.com/language/translate/v2/detect")
        @FormUrlEncoded
        Call<JSONObject> detect(@Field("key") String key, @Field("q") String query);

        //        @Headers({"Content-Type: application/json"})
        @POST("https://www.googleapis.com/language/translate/v2")
        @FormUrlEncoded
        Call<JSONObject> translate(@Field("key") String key, @Field("q") String query, @Field
                ("source") String source, @Field("target") String target);
    }

    public interface NewsApi {
        /**
         * 消息ID總表(APP端會根據ID判斷是否為新消息)
         *
         * @return {
         * "notify":["fklafhlk","asjlsfj"],// 通知ID陣列
         * "announce":["fklafhlk","asjlsfj"],// 公告ID陣列
         * "resource":["fklafhlk","asjlsfj"] // 銷售資源ID陣列
         * }
         */
        @GET("notice/ids")
        Call<JSONObject> getNewsList(@Query("token") String token);

        /**
         * 公告列表
         */
        @GET("notice/announce")
        Call<JSONObject> getAnnounceList(@Query("token") String token);

        /**
         * 通知列表
         */
        @GET("notice/notify")
        Call<JSONObject> getNotifyList(@Query("token") String token);

        /**
         * 公告明細
         *
         * @param id 公告id
         */
        @GET("notice/announce/{id}")
        Call<JSONObject> getAnnounceDetail(@Path("id") String id, @Query("token") String token);

        /**
         * 通知明細
         *
         * @param id 通知id
         */
        @GET("notice/notify/{id}")
        Call<JSONObject> getNotifyDetail(@Path("id") String id, @Query("token") String token);

        /**
         * 銷售資源部門列表
         *
         * @return [
         * {
         * "id":"aksf001" // 部門ID
         * "name":"業務部", // 部門名稱
         * "list":[{SaleResourceList},{SaleResourceList},...] // 銷售資源列表
         * },
         * {...}
         * ]
         */
        @GET("notice/sales_resource/department")
        Call<JSONObject> getResourceDepartment(@Query("token") String token);

        @GET("notice/sales_resource/categories")
        Call<JSONObject> getResourceCategories(@Query("token") String token);

        @GET("notice/sales_resource/file/{department_id}/{categories_id}")
        Call<JSONObject> getResourceById(@Path("department_id")
                                                 String departmentId, @Path("categories_id")
                                                 String categoriesId, @Query("token") String token);

    }

    public interface StatisticApi {
        /**
         * 銷售目標完成情況
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param userId       可能為null，按個人查看範圍時才會傳入
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param checkPoint   考核指標(0-總金額(合同) 1-總金額(發票))
         * @return current_percent:int 本期百分比(ex.9月)
         * prior_percent:int 去年同期百分比(ex.去年9月)
         * current:float 當前數值(依考核指標決定)
         * target:float 目標數值(依考核指標決定)
         */
        @GET("statistics/user/goal")
        Call<JSONObject> getSaleTarget(@Query("period") int period,
                                       @Query("user_id") String userId,
                                       @Query("department_id") String departmentId,
                                       @Query("check_point") int checkPoint,
                                       @Query("token") String token);

        /**
         * 業務拜訪排行(拜訪次數)
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param count        數量(10~50)
         * @return [
         * {
         * "user_id":"abcd123", // 業務id
         * "count":100, // 拜訪次數
         * "last_visit":"2016-04-21/晶光電子" //最後一次拜訪日期/客戶名稱
         * },
         * {...}
         * ]
         */
        @GET("statistics/user/visit_rank")
        Call<JSONObject> getVisitRank(@Query("period") int period
                , @Query("department_id") String departmentId
                , @Query("company_id") String companyId
                , @Query("count") int count
                , @Query("token") String token);

        /**
         * 業務員銷售業績(時間範圍內銷售金額)
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param count        數量(10~50)
         * @param checkPoint   考核指標(0-總金額(合同) 1-總金額(發票))
         * @return [
         * {
         * "user_id":"abcd123", // 業務id
         * "amount":100, // 時間範圍內銷售金額
         * "year_amount":3000 // 當年度累積銷售金額
         * },
         * {...}
         * ]
         */
        @GET("statistics/user/performance")
        Call<JSONObject> getPerformance(@Query("period") int period,
                                        @Query("department_id") String departmentId,
                                        @Query("company_id") String companyId,
                                        @Query("count") int count,
                                        @Query("check_point") int checkPoint,
                                        @Query("token") String token);

        /**
         * 預估簽約商機排行(專案金額)
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param userId       可能為null，按個人查看範圍時才會傳入
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param industry     可能為null，行業id
         * @param count        數量(10~50)
         * @return [
         * {
         * "project":{Project}, // 工作項目物件
         * "customer":{Customer}, // 客戶物件
         * "percent":70 // 銷售階段百分比
         * },
         * {...}
         * ]
         */
        @GET("statistics/project/sales_opportunity")
        Call<JSONObject> getSaleSign(@Query("period") int period,
                                     @Query("user_id") String userId,
                                     @Query("department_id") String departmentId,
                                     @Query("company_id") String companyId,
                                     @Query("industry_id") String industry,
                                     @Query("count") int count,
                                     @Query("token") String token);

        /**
         * 銷售機會贏單(合約總金額)
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param userId       可能為null，按個人查看範圍時才會傳入
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param industry     可能為null，行業id
         * @param count        數量(10~50)
         * @return [
         * {
         * "customer":{Customer}, // 客戶物件
         * "total":8000000 // 合約總金額
         * },
         * {...}
         * ]
         */
        @GET("statistics/customer/sales_opportunity_rank")
        Call<JSONObject> getSaleWin(@Query("period") int period,
                                    @Query("user_id") String userId,
                                    @Query("department_id") String departmentId,
                                    @Query("company_id") String companyId,
                                    @Query("industry_id") String industry,
                                    @Query("count") int count,
                                    @Query("token") String token);

        /**
         * 各銷售階段總數
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param userId       可能為null，按個人查看範圍時才會傳入
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param industry      可能為null，行業id
         * @return {
         * "stage_1":{
         * "count":3, //工作項目總數
         * "name":"設計選型", //工作階段名稱
         * "total":6000000 //工作項目總金額
         * },
         * {...}
         * }
         */
        @GET("statistics/sales_opportunity/stage_count")
        Call<JSONObject> getSaleCount(@Query("period") int period,
                                      @Query("user_id") String userId,
                                      @Query("department_id") String departmentId,
                                      @Query("company_id") String companyId,
                                      @Query("industry_id") String industry,
                                      @Query("token") String token);

        /**
         * 工作項目列表
         *
         * @param period       時間範圍(0-本月 1-上月 2-本季度 3-上季度 4-本年 5-去年)
         * @param userId       可能為null，按個人查看範圍時才會傳入
         * @param departmentId 可能為null，按部門查看範圍時才會傳入
         * @param industry      可能為null，行業id
         * @param stage        銷售階段(1~6)
         * @return [
         * {
         * "project":{Project}, // 工作項目物件
         * "customer":{Customer}, // 客戶物件
         * "stage":"報價選型", // 銷售階段名稱
         * "percent":70 // 銷售階段百分比
         * },
         * {...}
         * ]
         */
        @GET("statistics/sales_opportunity/project")
        Call<JSONObject> getSaleList(@Query("period") int period,
                                     @Query("user_id") String userId,
                                     @Query("department_id") String departmentId,
                                     @Query("company_id") String companyId,
                                     @Query("industry_id") String industry,
                                     @Query("stage") int stage,
                                     @Query("token") String token);
    }

    public interface ApprovalApi {
        @GET("approval/trip_config")
        Call<JSONObject> getApprovalConfig(@Query("token") String token);

        @GET("approval/index/waiting")
        Call<JSONObject> getApprovalRecord(@Query("token") String token);

        @GET("approval/index/approvable")
        Call<JSONObject> getApprovable(@Query("token") String token);

        @GET("approval/stage")
        Call<JSONObject> getApprovableStage(@Query("token") String token, @Query("trip_id")
                String tripId);

        @GET("approval/available_approvers")
        Call<JSONObject> getAvailableApprovers(@Query("token") String token, @Query("user_id")
                String userId);

        @Headers({"Content-Type: application/json"})
        @POST("approval/approve")
        Call<JSONObject> executeApprove(@Query("token") String token, @Body Approve approve);

        @Headers({"Content-Type: application/json"})
        @POST("approval/multiple_approve")
        Call<JSONArray> executeApprove(@Query("token") String token, @Body ApproveMulti approve);

        @Headers({"Content-Type: application/json"})
        @POST("approval/modify_approvers")
        Call<JSONObject> modifyApprovers(@Query("token") String token, @Body ModifyApprovers data);
    }

    public interface SampleApi {
        @GET("sample")
        Call<JSONObject> getSampleList(@Query("keyword") String keyword);

        @GET("sample/{id}")
        Call<JSONObject> getSample(@Path("id") String id);
    }

    public interface UserApi {
        @GET("user/login")
        Call<JSONObject> login(@Query("account") String email, @Query("password") String pwd);

        @GET("user/get_by_token")
        Call<JSONObject> getUser(@Query("token") String token);

        @Headers({"Content-Type: application/json"})
        @POST("user/update")
        Call<JSONObject> updateUser(@Query("token") String token, @Body User user);

        @Headers({"Content-Type: application/json"})
        @POST("user/update")
        Call<JSONObject> updatePassword(@Query("token") String token, @Body UserPassword
                userPassword);
    }

    public interface TripApi {
        @Headers({"Content-Type: application/json"})
        @POST("trip/signin/valid_location")
        Call<JSONObject> validLocation(@Query("token") String token, @Body ValidLocation location);

        @GET("trip/approval/description")
        Call<JSONObject> getApprovalDescription(@Query("token") String token, @Query("id") String
                tripId);
    }

    public interface OptionApi {
        @GET("option/after_login")
        Call<JSONObject> afterLogin(@Query("token") String token);
    }

    public interface OnlineResourceApi {
        /**
         * 取得線上資源列表
         *
         * @param tripId 行程id
         */
        @GET("online_resource")
        Call<JSONObject> getResource(@Query("token") String token, @Query
                ("online_resource_parent_id") String
                tripId);
    }

    public interface SystemApi {
        /**
         * 檢查現有版號
         *
         * @param platform 1:iOS 2:Android
         * @param version  版號，格式(major.minor.build)
         */
        @GET("version/check")
        Call<JSONObject> versionCheck(@Query("platform") int platform, @Query("version") String version);
    }

    public interface SubmitApi {
        /**
         * 執行複數提交
         */
        @Headers({"Content-Type: application/json"})
        @POST("submit/multiple_submit")
        Call<JSONObject> submit(@Query("token") String token, @Body MultipleSubmit data);
    }

    public interface SyncApi {
        /**
         * 抓取同步資料，不含檔案，檔案同步請呼叫 /sync/file
         *
         * @param date 上次更新的日期，格式 YYYY-MM-DD HH:mm:SS
         */
        @GET("sync/data?type=1")
        Single<SyncSetting> getSettingData(@Query("platform") String platform, @Query("token") String
                token, @Query("date") String date);

        /**
         * 抓取同步資料，不含檔案，檔案同步請呼叫 /sync/file
         *
         * @param date 上次更新的日期，格式 YYYY-MM-DD HH:mm:SS
         */
        @GET("sync/data?type=2")
        Single<SyncRecord> getRecordData(@Query("platform") String platform, @Query("token") String
                token, @Query("date") String date);

        /**
         * 上傳同步資料
         */
        @Headers({"Content-Type: application/json"})
        @POST("sync/data")
        Call<JSONObject> postData(@Query("platform") String platform, @Query("token") String
                token, @Body UploadRecord data);

        /**
         * 取得同步狀態
         *
         * @param syncId 同步ID
         * @return status 0:尚未同步 1:同步成功 2:同步失敗
         */
        @GET("sync/status")
        Call<JSONObject> getSyncStatus(@Query("token") String token, @Query("id") String syncId);
    }

    final String BASE_URL = Constants.ROOT_URL + "api/";
    public final static String IMAGE_URL = Constants.ROOT_URL + "file/image/";
    public final static int ERROR_TOKEN_EXPIRED = 404;
    private static ApiHelper singleton = new ApiHelper();

    Retrofit retrofit;

    private ApiHelper() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonGMTDateAdapter())
                .create();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS);
//        if (BuildConfig.IS_DEBUG) {
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(interceptor);
//        }
        final OkHttpClient okHttpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JSONConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public static ApiHelper getInstance() {
        return singleton;
    }

    public TranslateApi getTranslateApi() {
        return retrofit.create(TranslateApi.class);
    }

    public UserApi getUserApi() {
        return retrofit.create(UserApi.class);
    }

    public SyncApi getSyncApi() {
        return retrofit.create(SyncApi.class);
    }

    public ApprovalApi getApprovalApi() {
        return retrofit.create(ApprovalApi.class);
    }

    public SampleApi getSampleApi() {
        return retrofit.create(SampleApi.class);
    }

    public TripApi getTripApi() {
        return retrofit.create(TripApi.class);
    }

    public OptionApi getOptionApi() {
        return retrofit.create(OptionApi.class);
    }

    public StatisticApi getStatisticApi() {
        return retrofit.create(StatisticApi.class);
    }

    public NewsApi getNewsApi() {
        return retrofit.create(NewsApi.class);
    }

    public SystemApi getSystemApi() {
        return retrofit.create(SystemApi.class);
    }

    public SubmitApi getSubmitApi() {
        return retrofit.create(SubmitApi.class);
    }

    public OnlineResourceApi getOnlineResourceApi() {
        return retrofit.create(OnlineResourceApi.class);
    }
}
