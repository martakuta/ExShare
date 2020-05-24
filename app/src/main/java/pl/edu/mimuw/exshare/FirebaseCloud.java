package pl.edu.mimuw.exshare;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


class FirebaseCloud implements FirebaseAuth.AuthStateListener{
    private FirebaseStorage storage;
    private int count;

    FirebaseCloud() {
        storage = FirebaseStorage.getInstance();
        Log.i("[AUTHENTICATOR]", "signed_in = " + isSignedIn());
    }

    /**
     * Check if a user is signed in.
     *
     * @return @p true if the user is signed in, and @p false otherwise.
     */
    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * Convert image from ImageView to byte array.
     *
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
     *
     * @param path     Path to the file.
     * @param fileName Name of the file.
     * @return Associated StorageReference Object.
     */
    private StorageReference getStorageReference(String path, String fileName) {
        String fullPath = path + "/" + fileName;
        return storage.getReference().child(fullPath);
    }

    /**
     * Upload a file to the Firebase Storage.
     *
     * @param path     path to the file.
     * @param fileName Name of the file.
     * @param file     The file as a byte array.
     * @return UploadTask that has upload status listeners.
     */
    private UploadTask uploadFile(String path, String fileName, byte[] file) {
        StorageReference fireRef = getStorageReference(path, fileName);
        Log.i("[UPLOADER]", "Starting upload: " + path + "/" + fileName);
        return fireRef.putBytes(file);
    }

    /**
     * Download exercise content as an image from Firebase Storage.
     *
     * @param courseID       ID of the course.
     * @param courseName     Name of the course.
     * @param exerciseNumber Number of the exercise.
     * @return UploadTask that has upload status listeners.
     */
    Task<byte[]> downloadContentImage(int courseID, String courseName, String testName, int exerciseNumber) {
        String path = "courses/" + courseID + "/" + courseName + "/" + testName + "/content";
        String fileName = exerciseNumber + ".png";

        StorageReference fireRef = getStorageReference(path, fileName);
        Log.i("[DOWNLOADER]", "Starting download: " + path + "/" + fileName);
        return fireRef.getBytes(Long.MAX_VALUE);
    }

    /**
     * Download solution content as an image from Firebase Storage.
     *
     * @param courseID       ID of the course.
     * @param courseName     Name of the course.
     * @param exerciseNumber Number of the exercise.
     * @return UploadTask that has upload status listeners.
     */
    Task<byte[]> downloadSolutionImage(int courseID, String courseName, String testName, int exerciseNumber, int solutionNumber) {
        String path = "courses/" + courseID + "/" + courseName + "/" + testName + "/solution/" + exerciseNumber;
        String fileName = solutionNumber + ".png";

        StorageReference fireRef = getStorageReference(path, fileName);
        Log.i("[DOWNLOADER]", "Starting download: " + path + "/" + fileName);
        return fireRef.getBytes(Long.MAX_VALUE);
    }

    /**
     * Upload an exercise content as an image to Firebase Storage.
     *
     * @param courseID       ID of the course.
     * @param courseName     Name of the course.
     * @param exerciseNumber Number of the exercise.
     * @param imageView      ImageView Object.
     * @return UploadTask that has upload status listeners.
     */
    UploadTask uploadContentImage(int courseID, String courseName, String testName, int exerciseNumber, ImageView imageView) {
        String path = "courses/" + courseID + "/" + courseName + "/" + testName + "/content";
        String fileName = exerciseNumber + ".png";

        byte[] file = getImageFromView(imageView);
        return uploadFile(path, fileName, file);
    }

    /**
     * Properly convert a String to int.
     *
     * @param str String containing a number.
     * @return converted int.
     */
    int atoi(String str) {
        int num;
        try {
            assert (str != null);
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            num = 0;
        }
        return num;
    }

    /**
     * Upload the count of images as a metadata of an empty file.
     *
     * @param metadataRef Path to the file containing the metadata.
     * @return Upload Task.
     */
    private Task<StorageMetadata> updateMetadata(StorageReference metadataRef) {
        count++;
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("imageCount", String.valueOf(count))
                .build();
        return metadataRef.updateMetadata(metadata);
    }

    /**
     * Get the number of solutions provided for an exercise.
     *
     * @param courseID       ID of the course.
     * @param courseName     Name of the course.
     * @param exerciseNumber Number of the exercise.
     * @return Download Task.
     */
    Task<StorageMetadata> getSolutionsCount(int courseID, String courseName, String testName, int exerciseNumber) {
        String path = "courses/" + courseID + "/" + courseName + "/" + testName + "/solution/" + exerciseNumber;
        StorageReference metadataRef = getStorageReference(path, "metadata");


        Log.i("[UPLOADER]", "Trying to get metadata...");

        return metadataRef.getMetadata();
/*
                .addOnSuccessListener(storageMetadata -> {
            String countStr = storageMetadata.getCustomMetadata("imageCount");
            count = atoi(countStr);

        }).addOnFailureListener(exception -> {
            count = 0;


        });
*/
    }

    /**
     * Upload an exercise solution as an image to Firebase Storage.
     *
     * @param courseID       ID of the course.
     * @param courseName     Name of the course.
     * @param exerciseNumber Number of the exercise.
     * @param imageView      ImageView Object.
     */
    void uploadSolutionImage(int courseID, String courseName, String testName, int exerciseNumber, ImageView imageView) {
        String path = "courses/" + courseID + "/" + courseName + "/" + testName + "/solution/" + exerciseNumber;
        StorageReference metadataRef = getStorageReference(path, "metadata");

        Log.i("[UPLOADER]", "Trying to get metadata...");

        metadataRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            String countStr = storageMetadata.getCustomMetadata("imageCount");
            count = atoi(countStr);

            updateMetadata(metadataRef).addOnSuccessListener(storageMetadata1 -> {
                String fileName = count + ".png";
                byte[] file = getImageFromView(imageView);
                uploadFile(path, fileName, file);
            });


        }).addOnFailureListener(exception -> {
            int errorCode = ((StorageException) exception).getErrorCode();
            if(errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                count = 0;
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("imageCount", String.valueOf(count))
                        .build();

                byte[] empty_file = {0};
                metadataRef.putBytes(empty_file, metadata).addOnSuccessListener(taskSnapshot ->
                        updateMetadata(metadataRef).addOnSuccessListener(storageMetadata -> {
                    String fileName = count + ".png";
                    byte[] file = getImageFromView(imageView);
                    uploadFile(path, fileName, file);
                }));
            }
        });
    }

    /**
     * Obligatory function that handles what happens when the user's authentication state is changed.
     * @param firebaseAuth Firebase Authentication object which contains the authentication data.
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}