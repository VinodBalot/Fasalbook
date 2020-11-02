package com.wasfat.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.wasfat.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ImagePickerUtility {

    private Fragment mFragment;
    public static int REQUEST_CAMERA = 11;
    public static int REQUEST_GALLARY = 12;
    private Uri mHighQualityImageUri = null;
    private File fileUpload;
    private String mCurrentPhotoPath;
    private ImagePickerCallback imagePickerCallback;
    private Activity mActivity;

    public ImagePickerUtility(Fragment fragment, ImagePickerCallback imagePickerCallback) {
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
        this.imagePickerCallback = imagePickerCallback;
    }

    public ImagePickerUtility(Activity activity, ImagePickerCallback imagePickerCallback) {
        this.mActivity = activity;
        this.imagePickerCallback = imagePickerCallback;
    }

    //Capture Image From Camera
    public void captureImageFromCamera() {
        if (hasStoragePermission(REQUEST_CAMERA)) {
            Log.d("1234","Open camera 2");
            mHighQualityImageUri = generateTimeStampPhotoFileUri();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mHighQualityImageUri);
            if (mFragment != null)
                mFragment.startActivityForResult(intent, REQUEST_CAMERA);
            else mActivity.startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    //Pick Image From Gallery
    public void chooseImageFromGallary() {
        if (hasStoragePermission(REQUEST_GALLARY)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            if (mFragment != null)
                mFragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLARY);
            else
                mActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLARY);
        }
    }

    public Uri getImageUri() {
        return this.mHighQualityImageUri;
    }

    public String getImagePath() {
        return this.mCurrentPhotoPath;
    }

    public File getImageFile() {
        return this.fileUpload;
    }


    //Uses For Camera
    private Uri generateTimeStampPhotoFileUri() {
        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if (outputDir != null) {
            File photoFile = new File(outputDir, System.currentTimeMillis()
                    + ".jpg");
            photoFile.delete();
            mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoFileUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", photoFile);
            } else
                photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    private File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageStagte = Environment.getExternalStorageState();
        if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED)) {
            File photoDir = Environment
                    .getExternalStorageDirectory();
            outputDir = new File(photoDir.getAbsolutePath(), mActivity.getString(R.string.app_name));
            if (!outputDir.exists())
                if (!outputDir.mkdirs()) {
                    outputDir = null;
                }
        }
        return outputDir;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLARY && resultCode == Activity.RESULT_OK) {
            mHighQualityImageUri = data.getData();
            ImagePathUtility imagePathUtil = new ImagePathUtility();
            mCurrentPhotoPath = imagePathUtil.getImagePath(mActivity, mHighQualityImageUri);
            if (mCurrentPhotoPath != null) {
                fileUpload = new File(mCurrentPhotoPath);
                // For Fetch bitmap
                String compressImage = compressImage(mCurrentPhotoPath);
                if (compressImage != null) fileUpload = new File(compressImage);
                imagePickerCallback.onImagePickSuccess();
            } else imagePickerCallback.onImagePickError("Error to fetch image path");
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri imageUri = Uri.parse(mCurrentPhotoPath);
                fileUpload = new File(imageUri.getPath());
                mHighQualityImageUri = imageUri;
                String compressImage = compressImage(imageUri.getPath());
                if (compressImage != null) fileUpload = new File(compressImage);
            } else {
                mCurrentPhotoPath = mHighQualityImageUri.getPath();
                fileUpload = new File(mCurrentPhotoPath);
                String compressImage = compressImage(mCurrentPhotoPath);
                if (compressImage != null) fileUpload = new File(compressImage);
            }
            imagePickerCallback.onImagePickSuccess();
        }
    }

    public String compressImage(String imageUri) {

        // String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imageUri, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(imageUri, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(imageUri);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), mActivity.getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mActivity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera();
            } else
                imagePickerCallback.onImagePickError("Permission denied to read your External storage");
        } else if (requestCode == REQUEST_GALLARY) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageFromGallary();
            } else
                imagePickerCallback.onImagePickError("Permission denied to read your External storage");
        }

    }


    //Check storage Permission
    private boolean hasStoragePermission(int permission) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permission);
            return false;
        } else return true;
    }

    public interface ImagePickerCallback {
        public void onImagePickSuccess();

        public void onImagePickError(String message);
    }

    private class ImagePathUtility {
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


    private void copyFileOrDirectory(String srcDir, String dstDir, String file_name_for_copy) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, file_name_for_copy);

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1, file_name_for_copy);
                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
