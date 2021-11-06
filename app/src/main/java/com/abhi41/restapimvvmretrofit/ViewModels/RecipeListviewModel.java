package com.abhi41.restapimvvmretrofit.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.abhi41.restapimvvmretrofit.repositories.RecipeRepository;

import java.util.List;

public class RecipeListviewModel extends AndroidViewModel {
    private static final String TAG = "RecipeListviewModel";
    private RecipeRepository mRecipesRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsperformingQuery;


    public RecipeListviewModel(Application application) {
        super(application);
        // mIsViewingRecipes = false;
        mIsperformingQuery = false;
        mRecipesRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipesRepository.getRecipes();
    }

    public LiveData<Boolean> getNetworkTimeout(){
        return mRecipesRepository.getNetworkTimeout();
    }

    public LiveData<Boolean> isQueryExhausted (){
        return mRecipesRepository.isQueryExhausted();
    }

    public void searchRecipiesApi(String query, int pageNumber) {
        mIsViewingRecipes = true;
        mIsperformingQuery = true;
        mRecipesRepository.searchRecipiesApi(query, pageNumber);
    }
    public void searchNextPage(){
        if (!ismIsperformingQuery() && mIsViewingRecipes && !isQueryExhausted().getValue()) {
            mRecipesRepository.searchNextpage();
        }
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes) {
        mIsViewingRecipes = isViewingRecipes;
    }

    public boolean isBackPressed() {
        if (mIsperformingQuery) {
            Log.d(TAG, "isBackPressed");
            mRecipesRepository.cancleRequest();
            mIsViewingRecipes = false;
        }
        if (mIsViewingRecipes) {
            mIsViewingRecipes = false;
            return false;
        }
        return true;
    }

    public boolean ismIsperformingQuery() {
        return mIsperformingQuery;
    }

    public void setmIsperformingQuery(boolean mIsperformingQuery) {
        this.mIsperformingQuery = mIsperformingQuery;
    }
}
