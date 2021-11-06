package com.abhi41.restapimvvmretrofit.requests.responses;

import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class  RecipeResponse {

    @SerializedName("recipe")
    @Expose() //gson converter will serialized and deserialized
    private Recipe recipe;

    public Recipe getRecipe()
    {
        return recipe;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                '}';
    }
}
