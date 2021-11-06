package com.abhi41.restapimvvmretrofit.requests;

import com.abhi41.restapimvvmretrofit.requests.responses.RecipeResponse;
import com.abhi41.restapimvvmretrofit.requests.responses.RecipeSearchResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    //SEARCH
    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipe(
            //@Query("key") String key,
            @Query("q") String query,
            @Query("page") String page
    );

    //GET Recipe Request
    @GET("api/get")
    Call<RecipeResponse> getRecipe(
           // @Query("key") String key,
            @Query("rId") String recipe_id
    );


    @GET("api/get")
    Observable<RecipeResponse> getRecipieClone(@Query("rId") String recipe_id);

}
