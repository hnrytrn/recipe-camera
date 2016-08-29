package com.example.henrytran.recipecamera;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by henrytran on 16-08-28.
 */
public class Recipe implements Parcelable{
    private int id;
    private String title;
    private String image;
    private int usedIngredients;
    private int missedIngredients;
    private int likes;

    public Recipe (int id, String title, String image, int usedIngredients, int missedIngredients, int likes) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.usedIngredients = usedIngredients;
        this.missedIngredients = missedIngredients;
        this.likes = likes;
    }

    protected Recipe(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.id = Integer.parseInt(data[0]);
        this.title = data[1];
        this.image = data[2];
        this.usedIngredients = Integer.parseInt(data[3]);
        this.missedIngredients = Integer.parseInt(data[4]);
        this.likes = Integer.parseInt(data[5]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
            String.valueOf(this.id),
            this.title,
            this.image,
            String.valueOf(this.usedIngredients),
            String.valueOf(this.missedIngredients),
            String.valueOf(this.likes)
        });
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {

        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
