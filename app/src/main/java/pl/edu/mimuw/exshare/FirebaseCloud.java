package pl.edu.mimuw.exshare;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

class FirebaseCloud {
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    /**
     * Convert image from ImageView to byte array.
     * @param imageView ImageView Object.
     * @return Converted byte[].
     */
    private byte[] getImageFromView(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Get associated StorageReference for a given file.
     * @param path Path to the file.
     * @param fileName Name of the file.
     * @return Associated StorageReference Object.
     */
    StorageReference getStorageReference(String path, String fileName) {
        String fullPath = path + "/" + fileName;
        return storage.getReference().child(fullPath);
    }

    /**
     * uploadFile() wrapper with default path="unordered".
     */
    UploadTask uploadFile(String fileName, byte[] file) {
        return uploadFile("unordered", fileName, file);
    }
    /**
     * Upload a file to the Firebase Storage.
     * @param path path to the file.
     * @param fileName Name of the file.
     * @param file The file as a byte array.
     * @return UploadTask that has upload status listeners.
     */
    UploadTask uploadFile(String path, String fileName, byte[] file) {
        StorageReference fireRef = getStorageReference(path,fileName);

        return fireRef.putBytes(file);
    }
    /**
     * downloadFile() wrapper with default path="unordered".
     */
    Task<byte[]> downloadFile(String fileName) {
        return downloadFile("unordered", fileName);
    }

    /**
     * Download a file from Firebase Storage.
     * @param path path to the file.
     * @param fileName Name of the file.
     * @return Task that will allow access to the file with a listener once
     * the file is downloaded.
     */
    Task<byte[]> downloadFile(String path, String fileName) {
        StorageReference fireRef = getStorageReference(path,fileName);

        return fireRef.getBytes(Long.MAX_VALUE);
    }

    /**
     * uploadImage() wrapper with default path="unordered".
     */
    UploadTask uploadImage(String fileName, ImageView imageView) {
        return uploadImage("unordered", fileName, imageView);
    }
    /**
     * Upload an Image to Firebase Storage.
     * @param path path to the file.
     * @param fileName Name of the image.
     * @param imageView ImageView Object.
     * @return UploadTask that has upload status listeners.
     */
    UploadTask uploadImage(String path, String fileName, ImageView imageView) {
        byte[] file = getImageFromView(imageView);
        return uploadFile(path,fileName,file);
    }

}