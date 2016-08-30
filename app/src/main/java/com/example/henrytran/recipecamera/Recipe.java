package com.example.henrytran.recipecamera;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by henrytran on 16-08-28.
 */
public class Recipe implements Parcelable{
    private String id;
    private String title;
    private String image;

    public Recipe (String id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    protected Recipe(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.id = data[0];
        this.title = data[1];
        this.image = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
            this.id,
            this.title,
            this.image,
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
