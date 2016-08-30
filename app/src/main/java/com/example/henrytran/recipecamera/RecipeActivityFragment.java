package com.example.henrytran.recipecamera;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeActivityFragment extends Fragment {

    private String[] ingredients;

    public RecipeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the ingredients list from the intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("ingredients")) {
            ingredients = intent.getStringArrayExtra("ingredients");
        }
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, ArrayList<Recipe>> {

        private final String LOG_TAG = FetchRecipeTask.class.getSimpleName();

        // Parse the recipe data from the JSON string


        @Override
        protected ArrayList<Recipe> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String recipesJsonStr = null;

            try {
                //build url to fetch the recipes
                final String FOOD2FORK_BASE_URL = "http://food2fork.com/api/search";
                final String API_PARAM = "key";
                final String SEARCH_PARAM = "q";

                //build the ingredient search query
                StringBuilder ingredientsStrBuilder = new StringBuilder();
                for (String ingredient : ingredients) {
                    ingredientsStrBuilder.append(ingredient + ",");
                }
                ingredientsStrBuilder.deleteCharAt(ingredientsStrBuilder.length() - 1);
                String ingredientQuery = ingredientsStrBuilder.toString();

                Uri builtUri = Uri.parse(FOOD2FORK_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.FOOD2FORK_API_KEY)
                        .appendQueryParameter(SEARCH_PARAM, ingredientQuery)
                        .build();

                URL url = new URL(builtUri.toString());
                //create request to the recipe api and open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                recipesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return  getRecipeDataFromJson(recipesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }
}
