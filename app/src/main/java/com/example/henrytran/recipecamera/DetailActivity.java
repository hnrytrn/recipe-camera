package com.example.henrytran.recipecamera;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    String rId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the recipe id from the intent sent
        Intent intent = getIntent();
        if (intent.hasExtra("recipeTag") && intent != null) {
            rId = intent.getStringExtra("recipeTag");
        }
    }

    private class FetchDetailRecipeTask extends AsyncTask<String, Void, Void> {
        final String LOG_TAG = FetchDetailRecipeTask.class.getSimpleName();

        private void getRecipeFromJson(String RecipeJsonStr)
            throws JSONException {

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
                final String ID_PARAM = "rid";

                Uri builtUri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.FOOD2FORK_API_KEY)
                        .appendQueryParameter(ID_PARAM, rId[0])
                        .build();
                URL url = new URL(builtUri.toString());

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
    }
}
