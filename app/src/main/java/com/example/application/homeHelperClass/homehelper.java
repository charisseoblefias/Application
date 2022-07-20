package com.example.application.homeHelperClass;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class homehelper {

    String title;
    GradientDrawable color;

    public homehelper(GradientDrawable color, String title) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }


    public Drawable getgradient() {
        return color;
    }


}
