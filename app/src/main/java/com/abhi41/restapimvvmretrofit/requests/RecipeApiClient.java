package com.abhi41.restapimvvmretrofit.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abhi41.restapimvvmretrofit.AppExecutors;
import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.abhi41.restapimvvmretrofit.requests.responses.RecipeResponse;
import com.abhi41.restapimvvmretrofit.requests.responses.RecipeSearchResponse;
import com.abhi41.restapimvvmretrofit.util.Constants;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeApiClient {
    private static final String TAG = "RecipeApiClient";
    private MutableLiveData<List<Recipe>> mRecipes;
    private MutableLiveData<Recipe> mRecipeDetails;
    private MutableLiveData<Boolean> mNetworkTimeout;
    private static RecipeApiClient instance;
    private RetriveRecipesRunnable mRetriveRecipesRunnable;
    private RetriveRecipesDetailsRunnable mRetriveRecipesDetailsRunnable;
    private RecipeApi apiService;

    public static RecipeApiClient getInstance() {
        if (instance == null) {
            instance = new RecipeApiClient();
        }
        return instance;
    }

    private RecipeApiClient() {
        mRecipes = new MutableLiveData<>();
        mRecipeDetails = new MutableLiveData<>();
        mNetworkTimeout = new MutableLiveData<>();
        apiService = ServiceGenerator.getRecipeApi();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public LiveData<Recipe> getRecipeDetails() {
        return mRecipeDetails;
    }

    public LiveData<Boolean> getNetworkTimeout(){
        return mNetworkTimeout;
    }

    public void searchRecipiesApi(String query, int pageNumber) {

        if (mRetriveRecipesRunnable != null) {
            mRetriveRecipesRunnable = null;
        }
        mRetriveRecipesRunnable = new RetriveRecipesRunnable(query, pageNumber);

        final Future handler = AppExecutors.getInstance().getmNetworkIO().submit(mRetriveRecipesRunnable);

        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know its timedout
               // mNetworkTimeout.postValue(true);
                handler.cancel(true);
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);

    }

    //getting detail recipe page
    public void searchRecipeById(String recipeId){
        if (mRetriveRecipesDetailsRunnable != null)
        {
            mRetriveRecipesDetailsRunnable = null;
        }
        mRetriveRecipesDetailsRunnable = new RetriveRecipesDetailsRunnable(recipeId);

        final Future handler = AppExecutors.getInstance().getmNetworkIO().submit(mRetriveRecipesDetailsRunnable);
        mNetworkTimeout.setValue(false);
        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
                mNetworkTimeout.postValue(true);
            }
        },Constants.NETWORK_TIMEOUT,TimeUnit.MILLISECONDS);

    }

    private class RetriveRecipesRunnable implements Runnable {
        private String query;
        private int pageNumber;
        private boolean cancaleRequest;

        public RetriveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancaleRequest = false;
        }

        @Override
        public void run() {
            try {
                Response<RecipeSearchResponse> response = getRecipes(query, pageNumber).execute();
                if (cancaleRequest) {
                    return;
                }
                if (response.code() == 200) {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse) response.body()).getRecipes());
                    if (pageNumber == 1) {
                        mRecipes.postValue(list);
                        //note postvalue we used on background thread and setvalue in ui thread
                    } else {
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(list);
                        mRecipes.postValue(currentRecipes);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }

        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber) {
            return ServiceGenerator.getRecipeApi().searchRecipe(
                    query,
                    String.valueOf(pageNumber)
            );
        }

        private void cancaleRequest() {
            Log.d(TAG, "cancaleRequest: cancleing the search request");
            cancaleRequest = true;
        }
    }


    private class RetriveRecipesDetailsRunnable implements Runnable{

        String recipeId;
        boolean cancleRequest;
        public RetriveRecipesDetailsRunnable(String recipeId) {
            this.recipeId = recipeId;
            cancleRequest = false;
        }

        @Override
        public void run() {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(apiService.getRecipieClone(recipeId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<RecipeResponse>() {
                        @Override
                        public void onNext(@NonNull RecipeResponse recipeResponse) {

                            Recipe recipe = recipeResponse.getRecipe();
                            mRecipeDetails.postValue(recipe);

                        }

                        @Override
                        public void onError(@NonNull Throwable error) {
                            mRecipes.postValue(null);
                            Log.d(TAG, "onFailure: ");
                            if (error instanceof SocketTimeoutException) {
                                Log.d(TAG, "connection timeout");
                            } else if (error instanceof IOException) {
                                Log.d(TAG, "timeout");
                            } else {
                                //Call was cancelled by user
                                Log.d(TAG, "Call was cancelled forcefully");

                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        }
    }


    public void recipeDetail(String rId) {
     /*   Call<RecipeResponse> call = ServiceGenerator.getRecipeApi().getRecipe(rId);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.code() == 200) {
                    Recipe recipe = response.body().getRecipe();
                    mRecipeDetails.postValue(recipe);
                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.d(TAG, "onResponse: " + error);
                        mRecipes.postValue(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                        mRecipes.postValue(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable error) {
                mRecipes.postValue(null);
                Log.d(TAG, "onFailure: ");
                if (error instanceof SocketTimeoutException) {
                    Log.d(TAG, "connection timeout");
                } else if (error instanceof IOException) {
                    Log.d(TAG, "timeout");
                } else {
                    //Call was cancelled by user
                    if (call.isCanceled()) {
                        System.out.println("Call was cancelled forcefully");
                        Log.d(TAG, "Call was cancelled forcefully");
                    } else {
                        //Generic error handling
                        System.out.println("Network Error :: " + error.getLocalizedMessage());
                        Log.d(TAG, "Network Error :: " + error.getLocalizedMessage());
                    }
                }
            }
        });*/

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(apiService.getRecipieClone(rId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RecipeResponse>() {
                    @Override
                    public void onNext(@NonNull RecipeResponse recipeResponse) {

                        Recipe recipe = recipeResponse.getRecipe();
                        mRecipeDetails.postValue(recipe);

                    }

                    @Override
                    public void onError(@NonNull Throwable error) {
                        mRecipes.postValue(null);
                        Log.d(TAG, "onFailure: ");
                        if (error instanceof SocketTimeoutException) {
                            Log.d(TAG, "connection timeout");
                        } else if (error instanceof IOException) {
                            Log.d(TAG, "timeout");
                        } else {
                            //Call was cancelled by user
                            Log.d(TAG, "Call was cancelled forcefully");

                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                }));



    }


    public void cancleRequest() {
        if (mRetriveRecipesRunnable != null) {
            mRetriveRecipesRunnable.cancaleRequest();
        }
    }
}
