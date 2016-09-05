package com.example.henrytran.recipecamera;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class DetailActivity extends AppCompatActivity {

    String rId;
    static ArrayAdapter<String> ingredientsAdapter;
    ArrayList<String> ingredientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ingredientsList = new ArrayList<String>();

        // Set ingredients list adapter to the list view
        ingredientsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_ingredients);
        ListView listView = (ListView) findViewById(R.id.detail_recipe_listview);
        listView.setAdapter(ingredientsAdapter);

        // Get the recipe id from the intent sent
        Intent intent = getIntent();
        if (intent.hasExtra("recipeTag") && intent != null) {
            rId = intent.getStringExtra("recipeTag");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FetchDetailRecipeTask fetchDetailRecipeTask = new FetchDetailRecipeTask();
        fetchDetailRecipeTask.execute(rId);
    }

    private class FetchDetailRecipeTask extends AsyncTask<String, Void, Void> {
        final String LOG_TAG = FetchDetailRecipeTask.class.getSimpleName();

        String source;
        String title;
        String image;
        private void getRecipeFromJson(String recipeJsonStr)
            throws JSONException {

            // Information that needs to be retrieved
            final String RECIPE = "recipe";
            final String INGREDIENTS = "ingredients";
            final String SOURCE = "source_url";
            final String TITLE = "title";
            final String IMAGE = "image_url";

            // Parse through JSON
            JSONObject jsonObj= new JSONObject(recipeJsonStr);
            JSONObject recipeJson = jsonObj.getJSONObject(RECIPE);
            JSONArray ingredientsJson = recipeJson.getJSONArray(INGREDIENTS);

            // Copy Json array to ingredients list
            int ingLength = ingredientsJson.length();
            for (int i = 0; i < ingLength; i++) {
                Log.e(LOG_TAG, ingredientsJson.getString(i));
                ingredientsList.add(i, ingredientsJson.getString(i));
            }

            source = recipeJson.getString(SOURCE);
            title = recipeJson.getString(TITLE);
            image = recipeJson.getString(IMAGE);
        }

        @Override
        protected Void doInBackground(String... rId) {
            if (rId.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String recipeJsonStr = null;

            try {
                //build base URL to fetch recipe
                final String RECIPE_BASE_URL = "http://food2fork.com/api/get";
                final String API_PARAM = "key";
                final String ID_PARAM = "rId";

                Uri builtUri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.FOOD2FORK_API_KEY)
                        .appendQueryParameter(ID_PARAM, rId[0])
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, builtUri.toString());
                //connect to recipe API and open connection
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
                recipeJsonStr = buffer.toString();
                getRecipeFromJson(recipeJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } catch (JSONException e) {
                e.printStackTrace();
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

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            TextView titleText = (TextView) findViewById(R.id.detail_recipe_title);
            ImageView imageView = (ImageView) findViewById(R.id.detail_recipe_image);
            TextView sourceText = (TextView) findViewById(R.id.detail_recipe_directions);

            // Set text and image for the recipe
            titleText.setText(title);
            Picasso.with(getApplicationContext())
                    .load(image)
                    .resize(750,750)
                    .into(imageView);
            ingredientsAdapter.addAll(ingredientsList);


            // Set source text as a hyperlink to the recipe
            sourceText.setClickable(true);
            sourceText.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='" + source + "'> Click here for directions </a>";
            sourceText.setText(Html.fromHtml(text));
        }
    }
}
