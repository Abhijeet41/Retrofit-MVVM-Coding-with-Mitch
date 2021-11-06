package com.abhi41.restapimvvmretrofit.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.abhi41.restapimvvmretrofit.R;
import com.abhi41.restapimvvmretrofit.ViewModels.RecipeListviewModel;
import com.abhi41.restapimvvmretrofit.adapters.RecipeRecyclerAdapter;
import com.abhi41.restapimvvmretrofit.databinding.ActivityRecipeListBinding;
import com.abhi41.restapimvvmretrofit.interfaces.OnRecipeListener;
import com.abhi41.restapimvvmretrofit.models.Recipe;
import com.abhi41.restapimvvmretrofit.requests.responses.RecipeResponse;
import com.abhi41.restapimvvmretrofit.screens.RecipeActivity;
import com.abhi41.restapimvvmretrofit.util.Testing;
import com.abhi41.restapimvvmretrofit.util.VerticalSpacingItemDecorator;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity implements OnRecipeListener {

    ActivityRecipeListBinding binding;
    private static final String TAG = "RecipeListActivity";
    private RecipeListviewModel mRecipeListViewModel;
    private RecipeRecyclerAdapter mRecipeRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_list);
        setSupportActionBar(binding.toolbar);

        mRecipeListViewModel = new RecipeListviewModel(getApplication());
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListviewModel.class);

        initRecyclerView();
        subscribeObservers();
        initSearchView();
        if (!mRecipeListViewModel.isViewingRecipes()) {
            //display search categories
            displaySearchCategories();
        }


    }

    private void subscribeObservers() {


        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    if (mRecipeListViewModel.isViewingRecipes()) {
                        //  Testing.printRecipes(recipes, TAG);
                        mRecipeListViewModel.setmIsperformingQuery(false);
                        mRecipeRecyclerAdapter.setRecipes(recipes);


                    }
                }
            }
        });

        mRecipeListViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    Log.d(TAG, "query is Exhausted" + aBoolean);

                    mRecipeRecyclerAdapter.setQueryExhausted();
                }
            }
        });

        mRecipeListViewModel.getNetworkTimeout().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "getNetworkTimeout: ");
                }
            }
        });
    }

    private void initRecyclerView() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        binding.recyRecipeList.addItemDecoration(itemDecorator);
        binding.recyRecipeList.setAdapter(mRecipeRecyclerAdapter);
        binding.recyRecipeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!binding.recyRecipeList.canScrollVertically(1)) {
                    // search next page

                        mRecipeListViewModel.searchNextPage();

                }
            }
        });
    }

    private void initSearchView() {
        binding.searchView.setIconified(false);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mRecipeRecyclerAdapter.displayLoading();
                mRecipeListViewModel.searchRecipiesApi(query, 1);
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mRecipeRecyclerAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        mRecipeRecyclerAdapter.displayLoading();
        mRecipeListViewModel.searchRecipiesApi(category, 1);
        binding.searchView.clearFocus();
    }

    private void displaySearchCategories() {
        mRecipeListViewModel.setIsViewingRecipes(false);
        mRecipeRecyclerAdapter.displaySearchCategories();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_categories) {
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.isBackPressed()) {
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }
    }

}