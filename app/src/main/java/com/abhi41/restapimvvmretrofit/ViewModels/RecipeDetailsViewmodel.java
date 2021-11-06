package com.abhi41.restapimvvmretrofit.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.abhi41.restapimvvmretrofit.repositories.RecipeRepository;

public class RecipeDetailsViewmodel extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;
    private String mRecipeId;
    private boolean didRetriveData;

    public RecipeDetailsViewmodel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance();

    }

    public LiveData<Recipe> getRecipe(){

        return mRecipeRepository.getRecipeDetails();
    }

    public boolean getDidRetriveData() {
        return didRetriveData;
    }

    public void setDidRetriveData(boolean didRetriveData) {
        this.didRetriveData = didRetriveData;
    }

    public void searchRepositoryById(String rId) {
        mRecipeId = rId;
        mRecipeRepository.searchByrepository(rId);
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public LiveData<Boolean> getNetworkTimeout(){
        return mRecipeRepository.getNetworkTimeout();
    }
}
