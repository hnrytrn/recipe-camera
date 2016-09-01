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
    public View getView(int position, View convertView, ViewGroup parent) {

        Recipe recipeItem = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_recipe, parent, false);

            // cache view fields into holder
            viewHolder = new ViewHolder();
            viewHolder.recipeImageView = (ImageView) convertView.findViewById(R.id.recipe_image);
            viewHolder.recipeTitleView =  (TextView) convertView.findViewById(R.id.recipe_title);
            convertView.setTag(viewHolder);
        } else {
            // view already exists
            viewHolder =  (ViewHolder) convertView.getTag();
        }

        viewHolder.recipeTitleView.setText(recipeItem.title);
        Picasso.with(getContext())
                .load(recipeItem.image)
                .resize(500, 500)
                .into(viewHolder.recipeImageView);


        return convertView;
    }

    /**
     * Cache of the children views for a recipe list item
     */
    public static class ViewHolder {
        ImageView recipeImageView;
        TextView recipeTitleView;
    }

}
