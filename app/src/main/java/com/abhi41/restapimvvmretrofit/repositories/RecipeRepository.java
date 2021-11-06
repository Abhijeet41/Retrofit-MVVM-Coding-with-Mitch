package com.abhi41.restapimvvmretrofit.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.abhi41.restapimvvmretrofit.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private RecipeApiClient mRecipeApiClient;
    private static RecipeRepository instance;
    private int mPageNumber;
    private String mQuery;
    private MutableLiveData<Boolean> mIsqueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();

    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    public RecipeRepository() {
        mRecipeApiClient = RecipeApiClient.getInstance();
        initMediators();
    }

    private void initMediators() {
        LiveData<List<Recipe>> recipeListApiSource = mRecipeApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                } else {
                    //search database cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<Recipe> list) {
        if (list != null) {
            if (list.size() % 30 !=0) {
                mIsqueryExhausted.setValue(true);
            }
        } else {
            mIsqueryExhausted.setValue(true);
        }
    }

    public LiveData<Boolean> isQueryExhausted() {
        return mIsqueryExhausted;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public LiveData<Recipe> getRecipeDetails() {
        return mRecipeApiClient.getRecipeDetails();
    }

    public LiveData<Boolean> getNetworkTimeout() {
        return mRecipeApiClient.getNetworkTimeout();
    }

    public void searchRecipiesApi(String query, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        mQuery = query;
        mPageNumber = pageNumber;
        mIsqueryExhausted.setValue(false);
        mRecipeApiClient.searchRecipiesApi(query, pageNumber);
    }

    public void searchNextpage() {
        searchRecipiesApi(mQuery, mPageNumber + 1);
    }

    public void searchByrepository(String rId) {
        mRecipeApiClient.searchRecipeById(rId);
    }

    public void cancleRequest() {
        mRecipeApiClient.cancleRequest();
    }

}
