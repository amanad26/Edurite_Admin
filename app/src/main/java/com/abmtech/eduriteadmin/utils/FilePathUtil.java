package com.abmtech.eduriteadmin.utils;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class FilePathUtil {

    // Get the real path from a content URI
    public static String getPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        final String scheme = uri.getScheme();
        if (scheme == null) {
            return uri.getPath();
        }

        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e("FilePathUtil", "Error getting file path from content URI: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return null;
    }
}
