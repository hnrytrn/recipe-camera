package com.example.henrytran.recipecamera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecipeActivityFragment())
                    .commit();
        }
    }

}
