package com.example.henrytran.recipecamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by henrytran on 16-08-29.
 */
public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context mContext;
    private ArrayList<Recipe> mRecipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        super(context, 0, recipes);
        mContext = context;
        mRecipes = new ArrayList<Recipe>(recipes);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {


        ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_recipe, null);
            // cache view fields into holder
            viewHolder = new ViewHolder();
            viewHolder.recipeImageView = (ImageView) v.findViewById(R.id.recipe_image);
            viewHolder.recipeTitleView =  (TextView) v.findViewById(R.id.recipe_title);
        } else {
            // view already exists
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.recipeTitleView.setText(mRecipes.get(position).title);
        Picasso.with(getContext())
                .load(mRecipes.get(position).image)
                .into(viewHolder.recipeImageView);

        return v;
    }

    /**
     * Cache of the children views for a recipe list item
     */
    public static class ViewHolder {
        ImageView recipeImageView;
        TextView recipeTitleView;
    }

}
