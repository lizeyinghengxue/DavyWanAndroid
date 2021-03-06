package com.davy.davy_wanandroid.core.http.api;

import com.davy.davy_wanandroid.app.Constants;
import com.davy.davy_wanandroid.bean.BaseGankResponse;
import com.davy.davy_wanandroid.bean.BaseResponse;
import com.davy.davy_wanandroid.bean.girls.GirlsImageData;
import com.davy.davy_wanandroid.bean.knowledgehierarchy.KnowledgeHierarchyData;
import com.davy.davy_wanandroid.bean.main.BannerData;
import com.davy.davy_wanandroid.bean.main.LoginData;
import com.davy.davy_wanandroid.bean.main.TopSearchData;
import com.davy.davy_wanandroid.bean.main.UsefulSiteData;
import com.davy.davy_wanandroid.bean.main.WanAndroidArticleListData;
import com.davy.davy_wanandroid.bean.navigation.NavigationListData;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * author: Davy
 * date: 18/9/17
 */
public interface WanAndroidApi {

    /**
     * 注册
     *
     * @param username
     * @param password
     * @param repassword
     * @return 注册数据
     */
    @POST("user/register")
    @FormUrlEncoded
    Observable<BaseResponse<LoginData>> getRegisterData(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword );

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return 登录数据
     */
    @POST("user/login")
    @FormUrlEncoded
    Observable<BaseResponse<LoginData>> getLoginData(@Field("username") String username, @Field("password") String password);

    /**
     * 轮播图
     *
     * @return 轮播图数据
     */
    @GET("banner/json")
    Observable<BaseResponse<List<BannerData>>> getBannerData();

    /**
     * 获取文章列表
     *
     * @param num 页数
     * @return 文章列表数据
     */
    @GET("article/list/{num}/json")
    Observable<BaseResponse<WanAndroidArticleListData>> getWanAndroidArticleListData(@Path("num") int num);

    /**
     * 收藏站内文章
     *
     * @param id 文章id
     * @return 收藏id文章数据
     */
    @POST("lg/collect/{id}/json")
    Observable<BaseResponse<WanAndroidArticleListData>> addCollectArticle(@Path("id") int id);

    /**
     * 取消站内收藏文章
     *
     * @param id
     * @param originId
     * @return 取消收藏id文章数据
     */
    @POST("lg/uncollect_originId/{id}/json")
    @FormUrlEncoded
    Observable<BaseResponse<WanAndroidArticleListData>> cancelCollectArticle(@Path("id") int id, @Field("originId") int originId);

    /**
     * 知识体系
     *
     * @return 知识体系数据
     */
    @GET("tree/json")
    Observable<BaseResponse<List<KnowledgeHierarchyData>>> getKnowledgeHierarchyData();

    /**
     * 知识体系下的详细文章列表
     *
     * @param page page页数
     * @param id 文章id
     * @return 纤细文章列表
     */
    @GET("article/list/{page}/json")
    Observable<BaseResponse<WanAndroidArticleListData>> getKnowledgeHierarchyDetailData(@Path("page") int page, @Query("cid") int id);

    /**
     * 取消收藏列表文章
     *
     * @param id
     * @param originId
     * @return 收藏id文章的数据
     */
    @POST("lg/uncollect/{id}/json")
    @FormUrlEncoded
    Observable<BaseResponse<WanAndroidArticleListData>> cancelCollectPageArticle(@Path("id") int id, @Field("originId") int originId);

    /**
     * 收藏列表
     *
     * @param page
     * @return 收藏列表数据
     */
    @GET("lg/collect/list/{page}/json")
    Observable<BaseResponse<WanAndroidArticleListData>> getCollectArticleList(@Path("page") int page);

    /**
     * 导航
     *
     * @return 导航列表数据
     */
    @GET("navi/json")
    Observable<BaseResponse<List<NavigationListData>>> getNavigationListData();

    /**
     * 妹子福利
     *
     * @param type
     * @param count
     * @param pageIndex
     * @return 妹子图片数据
     */
    @GET(Constants.URL_GANK + "data/{type}/{count}/{pageIndex}")
    Observable<BaseGankResponse<List<GirlsImageData>>> getGirlsListData(@Path("type") String type,
                                                                        @Path("count") int count,
                                                                        @Path("pageIndex") int pageIndex);

    /**
     *常用网站
     *
     * @return 常用网站数据
     */
    @GET("friend/json")
    Observable<BaseResponse<List<UsefulSiteData>>> getUsefulSiteData();

    /**
     * 搜索数据
     *
     * @param page
     * @param k
     * @return 搜索数据
     */
    @POST("article/query/{page}/json")
    @FormUrlEncoded
    Observable<BaseResponse<WanAndroidArticleListData>> getSearchList(@Path("page") int page, @Field("k") String k);

    /**
     * 热搜
     *
     * @return 热搜数据
     */
    @GET("hotkey/json")
    @Headers("Cache-Control: public, max-age=36000")
    Observable<BaseResponse<List<TopSearchData>>> getTopSearchData();
}
