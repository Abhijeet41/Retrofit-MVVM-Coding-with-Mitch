package com.abhi41.restapimvvmretrofit.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abhi41.restapimvvmretrofit.util.BaseActivity;
import com.abhi41.restapimvvmretrofit.R;
import com.abhi41.restapimvvmretrofit.ViewModels.RecipeDetailsViewmodel;
import com.abhi41.restapimvvmretrofit.databinding.ActivityRecipeBinding;
import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class RecipeActivity extends BaseActivity {
    private static final String TAG = "RecipeActivity";
    ActivityRecipeBinding binding;
    private RecipeDetailsViewmodel recipeDetailsViewmodel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe);
        recipeDetailsViewmodel = new RecipeDetailsViewmodel(getApplication());
        recipeDetailsViewmodel = new ViewModelProvider(this).get(RecipeDetailsViewmodel.class);
        getIncomingIntent();
        subscribeObserver();
        showProgressBar(true,this);
    }

    private void getIncomingIntent() {

        if (getIntent().hasExtra("recipe")) {
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            recipeDetailsViewmodel.searchRepositoryById(recipe.getRecipe_id());
            Log.d(TAG, "getIncomingIntent " + recipe.getTitle());
        }
    }

    private void subscribeObserver() {
        recipeDetailsViewmodel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                if (recipe != null) {
                    Log.d(TAG, "onChanged:-------------------------------------");
                    Log.d(TAG, "onChanged:" + recipe.getTitle());

                    if (recipe.getRecipe_id().equals(recipeDetailsViewmodel.getRecipeId())) {
                        setRecipeProperties(recipe);
                        recipeDetailsViewmodel.setDidRetriveData(true);
                    }
                }
            }
        });
      recipeDetailsViewmodel.getNetworkTimeout().observe(this, new Observer<Boolean>() {
          @Override
          public void onChanged(Boolean aBoolean) {
              if (aBoolean && !recipeDetailsViewmodel.getDidRetriveData())
              {
                  Log.d(TAG, "getNetworkTimeout: ");
                  displayErrorScreen("Error retriving data. check network connection");
              }
          }
      });
    }

    private void setRecipeProperties(Recipe recipe) {
        if (recipe != null) {
            binding.parent.setVisibility(View.VISIBLE);
            showProgressBar(false,this);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(recipe.getImage_url())
                    .into(binding.recipeImage);

            binding.recipeTitle.setText(recipe.getTitle());
            binding.recipeSocialScore.setText(String.valueOf(Math.round(recipe.getSocial_rank())));
            binding.ingredientsContainer.removeAllViews();

            for (String strIngredient : recipe.getIngredients()) {
                TextView txtIngredient = new TextView(this);
                txtIngredient.setText(strIngredient);
                txtIngredient.setTextSize(15);
                txtIngredient.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                binding.ingredientsContainer.addView(txtIngredient);
            }

        }
    }

    private void displayErrorScreen(String errorMessage){
        binding.parent.setVisibility(View.VISIBLE);
        binding.recipeTitle.setText("error retriveing recipe...");
        binding.recipeSocialScore.setText("");
        binding.ingredientsTitle.setText(errorMessage);
        TextView textView = new TextView(this);
        if (!errorMessage.equals(""))
        {
            textView.setText(errorMessage);
        }else {
            textView.setText("Error");
        }
        textView.setTextSize(15);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.ic_launcher_background)
                .into(binding.recipeImage);
        showProgressBar(false,this);

    }
}
