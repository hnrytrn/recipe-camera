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

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        super(context, 0, recipes);
        mContext = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        Recipe recipeItem = getItem(position);
        ViewHolder viewHolder;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_recipe, parent, false);

            // cache view fields into holder
            viewHolder = new ViewHolder();
            viewHolder.recipeImageView = (ImageView) v.findViewById(R.id.recipe_image);
            viewHolder.recipeTitleView =  (TextView) v.findViewById(R.id.recipe_title);
            v.setTag(viewHolder);
        } else {
            // view already exists
            viewHolder =  (ViewHolder) v.getTag();
        }

        viewHolder.recipeTitleView.setText(recipeItem.title);
        Picasso.with(getContext())
                .load(recipeItem.image)
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
