package com.example.henrytran.recipecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final ClarifaiClient clarifaiClient = new ClarifaiClient(BuildConfig.CLARIFAI_CLIENT_ID,
            BuildConfig.CLARIFAI_CLIENT_SECRET);
    private ArrayList<String> ingredientList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // intent for using the camera to take a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    // get the image captured as a bitmap
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //scale down image to improve performance
                    bitmap = Bitmap.createScaledBitmap(bitmap, 320,
                            320 * bitmap.getHeight() / bitmap.getWidth(), true);

                    // run image recognition on a background thread
                    new AsyncTask<Bitmap, Void, RecognitionResult>() {
                        @Override
                        protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                            // Compress image as a JPEG
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                            byte[] jpeg = outputStream.toByteArray();

                            //send jpeg to clarifai client and return result
                            return clarifaiClient.recognize(new RecognitionRequest(jpeg).setModel("food-items-v1.0")).get(0);
                        }

                        @Override
                        protected void onPostExecute (RecognitionResult result) {
                            //add the recognized ingredients to the ingredients list
                            for (Tag tag : result.getTags()) {
                                //add ingredient if the probability of the tag is greater than 90%
                                if (tag.getProbability() > 0.9) {
                                    ingredientList.add(tag.getName());
                                }
                            }

                            // Create intent for the recipe activity
                            String[] ingredients = new String[ingredientList.size()];
                            ingredientList.toArray(ingredients);
                            Intent recipeIntent = new Intent(getApplicationContext(), RecipeActivity.class)
                                    .putExtra("ingredients", ingredients);

                            startActivity(recipeIntent);
                        }
                    }.execute(bitmap);

                } catch (FileNotFoundException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            } else if (requestCode == RESULT_CANCELED) {
                // user cancelled the image capture
            } else {
                // capture failed
            }
        }
    }
}
