package com.abmtech.eduriteadmin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.abmtech.eduriteadmin.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Util {


    public  static  String APIKEY = "key=AAAAujWclzw:APA91bEoWTPHDukwUKww-9DWlJyxpI5B9U48-GEy7S0Lx4DdT4Wd_ubRX7V2BhloMepN5tEUons3IEf98sN1Pwh9ym9N-Zv7L3sCXKfvWQdtoiIN-O0ix9BQMm2YhV58guJDD9dcba8w";

    public static String encodeImageBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeImage(Context context, String image) {
        byte[] decodedString = new byte[0];
        try {
            decodedString = Base64.decode(image, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("TAG", "decodeImage: Image Decode: ", e);
        }
        if (decodedString.length == 0) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo);
        }
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String calculateDateDifference(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(inputDate);
            long currentTimeMillis = System.currentTimeMillis();
            long inputTimeMillis = date.getTime();
            long diffInMillis = currentTimeMillis - inputTimeMillis;

            if (diffInMillis < 0) {
                return "Invalid date";
            }

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            long weeks = days / 7;
            long months = days / 30;

            if (seconds < 60) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes == 1 ? "1 minute ago" : minutes + " mins ago";
            } else if (hours < 24) {
                return hours == 1 ? "1 hour ago" : hours + " hours ago";
            } else if (days < 7) {
                return days == 1 ? "1 day ago" : days + " days ago";
            } else if (weeks < 4) {
                return weeks == 1 ? "1 week ago" : weeks + " weeks ago";
            } else {
                return months == 1 ? "1 month ago" : months + " months ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Invalid Date";
    }
}
