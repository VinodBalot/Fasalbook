package com.wasfat.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ImagePathUtility {

    private Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getImagePath(Activity activity, Uri uri) {
        String strImagePath = null;
        boolean isImageFromGoogleDrive = false;
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(activity, uri)) {

            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    strImagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    Pattern DIR_SEPORATOR = Pattern.compile("/");
                    Set<String> rv = new HashSet<>();
                    String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                    String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                    String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                    if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                        if (TextUtils.isEmpty(rawExternalStorage)) {
                            rv.add("/storage/sdcard0");
                        } else {
                            rv.add(rawExternalStorage);
                        }
                    } else {
                        String rawUserId;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            rawUserId = "";
                        } else {
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            String[] folders = DIR_SEPORATOR.split(path);
                            String lastFolder = folders[folders.length - 1];
                            boolean isDigit = false;
                            try {
                                Integer.valueOf(lastFolder);
                                isDigit = true;
                            } catch (NumberFormatException ignored) {
                            }
                            rawUserId = isDigit ? lastFolder : "";
                        }
                        if (TextUtils.isEmpty(rawUserId)) {
                            rv.add(rawEmulatedStorageTarget);
                        } else {
                            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                        }
                    }
                    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                        String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                        Collections.addAll(rv, rawSecondaryStorages);
                    }
                    String[] temp = rv.toArray(new String[rv.size()]);

                    for (int i = 0; i < temp.length; i++) {
                        File tempf = new File(temp[i] + "/" + split[1]);
                        if (tempf.exists()) {
                            strImagePath = temp[i] + "/" + split[1];
                        }
                    }
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                Cursor cursor = null;
                String column = "_data";
                String[] projection = {
                        column
                };

                try {
                    cursor = activity.getContentResolver().query(contentUri, projection, null, null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        final int column_index = cursor.getColumnIndexOrThrow(column);
                        strImagePath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{
                        split[1]
                };

                Cursor cursor = null;
                String column = "_data";
                String[] projection = {
                        column
                };

                try {
                    cursor = activity.getContentResolver().query(contentUri, projection, selection, selectionArgs,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        strImagePath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                isImageFromGoogleDrive = true;
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            String column = "_data";
            String[] projection = {
                    column
            };

            try {
                cursor = activity.getContentResolver().query(uri, projection, null, null,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(column);
                    strImagePath = cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            strImagePath = uri.getPath();
        }


        if (isImageFromGoogleDrive) {
            try {
                bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File f = new File(strImagePath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
        }
        return strImagePath;
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }

}
