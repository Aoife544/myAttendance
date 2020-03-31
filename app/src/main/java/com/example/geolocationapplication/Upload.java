package com.example.geolocationapplication;

public class Upload {

    private String mName;
    private String mImageURL;

    public Upload() {

    }

    public Upload(String name, String imageURL) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageURL = imageURL;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageURL;
    }

    public void setImageUrl(String imageUrl) {
        mImageURL = imageUrl;
    }
}
