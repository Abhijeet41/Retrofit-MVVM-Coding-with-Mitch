package com.abhi41.restapimvvmretrofit.util;

import android.util.Log;

import com.abhi41.restapimvvmretrofit.models.Recipe;

import java.util.List;

public class Testing {
    public static void printRecipes(List<Recipe> recipes,String TAG)
    {
        for (Recipe recipe : recipes) {
            Log.d(TAG, "onChanged: " + recipe.getTitle());
        }
    }
}
