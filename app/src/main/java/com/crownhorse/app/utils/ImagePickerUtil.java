package com.crownhorse.app.utils;

import android.content.Context;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ImagePickerUtil {
    public static final String IMAGE_MIME_TYPE = "image/*";

    /**
     * Creates a launcher that opens the system image picker.
     * Register this in onCreate before any fragment transactions.
     */
    public static ActivityResultContracts.GetContent getContentContract() {
        return new ActivityResultContracts.GetContent();
    }

    /**
     * Returns a file name suggestion for uploading to Firebase Storage.
     */
    public static String generateFileName(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * Checks whether a URI points to a valid image resource.
     */
    public static boolean isValidUri(Context context, Uri uri) {
        if (uri == null) return false;
        try {
            context.getContentResolver().getType(uri);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
