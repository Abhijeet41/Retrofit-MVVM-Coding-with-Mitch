package com.abhi41.restapimvvmretrofit.adapters.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi41.restapimvvmretrofit.R;
import com.abhi41.restapimvvmretrofit.interfaces.OnRecipeListener;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView title, publisher,scocialScore;
    public AppCompatImageView imageView;
    OnRecipeListener onRecipeListener;

    public RecipeViewHolder(@NonNull View itemView,OnRecipeListener onRecipeListener) {
        super(itemView);
        this.onRecipeListener = onRecipeListener;

        title = itemView.findViewById(R.id.recipe_title);
        publisher = itemView.findViewById(R.id.recipe_publisher);
        scocialScore =  itemView.findViewById(R.id.recipe_social_score);
        imageView =  itemView.findViewById(R.id.recipe_image);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        onRecipeListener. onRecipeClick(getAdapterPosition());
    }
}
