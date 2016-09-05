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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static RecipeAdapter mRecipeAdapter;

    public RecipeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        mRecipeAdapter = new RecipeAdapter(getActivity(), new ArrayList<Recipe>());

        //set recipe adapter to list view
        ListView listView = (ListView)  rootView.findViewById(R.id.listview_recipe);
        listView.setAdapter(mRecipeAdapter);
        // Start detail activity for the recipe clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe recipe = mRecipeAdapter.getItem(position);

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("recipeTag", recipe.id);
                startActivity(detailIntent);
            }
        });

        // Get the ingredients list from the intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("ingredients")) {
            ingredients = intent.getStringArrayExtra("ingredients");
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRecipes();
    }

    private void loadRecipes() {
        if (ingredients.length == 0) {
            //No ingredients found in picture
            Toast.makeText(getContext(), "No ingredients found", Toast.LENGTH_SHORT).show();
            // Return to main activity
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        } else {
            FetchRecipeTask fetchRecipeTask = new FetchRecipeTask();
            fetchRecipeTask.execute(ingredients);
        }
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, ArrayList<Recipe>> {

        private final String LOG_TAG = FetchRecipeTask.class.getSimpleName();

        // Parse the recipe data from the JSON string
        private ArrayList<Recipe> getRecipeDataFromJson(String recipeJsonStr)
            throws JSONException {
            //JSON objects that need to be extracted
            final String RECIPES = "recipes";
            final String TITLE = "title";
            final String ID = "recipe_id";
            final String IMAGE = "image_url";

            JSONObject recipeJsonObj = new JSONObject(recipeJsonStr);
            JSONArray recipesJson = recipeJsonObj.getJSONArray(RECIPES);

            int recipesLength = recipesJson.length();

            ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
            //add Recipes to the recipe list
            for (int i = 0; i < recipesLength; i++) {
                JSONObject recipe = recipesJson.getJSONObject(i);

                String title = recipe.getString(TITLE);
                String id = recipe.getString(ID);
                String image = recipe.getString(IMAGE);

                recipeList.add(new Recipe(id, title, image));
            }
            return recipeList;
        }

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
                final String SORT_PARAM = "sort";

                String sort = "r"; //sort based off of rating
                //build the ingredient search query
                StringBuilder ingredientsStrBuilder = new StringBuilder();
//                for (String ingredient : ingredients) {
//                    ingredientsStrBuilder.append(ingredient + ",");
//                }
//                ingredientsStrBuilder.deleteCharAt(ingredientsStrBuilder.length() - 1);
//                String ingredientQuery = ingredientsStrBuilder.toString();
                String ingredientQuery = ingredients[0] + "," + ingredients[1] + "," + ingredients[2];

                Uri builtUri = Uri.parse(FOOD2FORK_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.FOOD2FORK_API_KEY)
                        .appendQueryParameter(SEARCH_PARAM, ingredientQuery)
                        .appendQueryParameter(SORT_PARAM, sort)
                        .build();

                Log.e(LOG_TAG, builtUri.toString());

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
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            if (!recipes.isEmpty()) {
                mRecipeAdapter.clear();

                //add recipes to the recipe adapter
                Recipe[] recipeArr = new Recipe[recipes.size()];
                recipes.toArray(recipeArr);
                for (Recipe recipe : recipeArr) {
                    mRecipeAdapter.add(recipe);
                }
            }
        }
    }
}
