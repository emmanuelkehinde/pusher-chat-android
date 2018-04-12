package com.kehinde.pusher_chat_test.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by kehinde on 9/24/17.
 */

public class ImageUtils {

    public static void loadImageFromDrawable(Context context, ImageView imageView,int path){
        Picasso.with(context).load(path).into(imageView);
    }

}
