package com.obs.image_cropping;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ImageMinimumSizeCalculator {


    public static int[] getMinSquarDimension(Uri imageUri, Context context) {

        int[] imageheightAndWidth = new int[2];
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            //set minimum heigt and width
            int minimumSize = bitmap.getHeight() / 2;
            imageheightAndWidth[0] = minimumSize;
            imageheightAndWidth[1] = minimumSize;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageheightAndWidth;
    }

    public static boolean isLandscape(Uri imageUri) {
        int rotate = 0;
        boolean isLandscape = true;
        try {
            File imageFile = new File(imageUri.getPath());

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    isLandscape = false;
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    isLandscape = true;
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    isLandscape = false;
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isLandscape;
    }

    public static int[] getMinRectangleDimensions(Uri imageUri, Context context) {
        int[] imageheightAndWidth = new int[2];
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            //set maximum width as for wall
            imageheightAndWidth[0] = bitmap.getWidth();
            if (bitmap.getHeight() < 300) {
                imageheightAndWidth[1] = 300;
            } else {
                imageheightAndWidth[1] = (bitmap.getHeight() / 300) * 300;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageheightAndWidth;
    }
}
