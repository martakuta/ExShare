package pl.edu.mimuw.exshare;


import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseCloud {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    UploadTask uploadFile(String fileName, byte[] file) {
        return uploadFile("unordered", fileName, file);
    }

    UploadTask uploadFile(String path, String fileName, byte[] file) {
        String fullPath = path+"/"+ fileName;
        StorageReference fireRef = storage.getReference().child(fullPath);

        return fireRef.putBytes(file);
    }

    Task<byte[]> downloadFile(String fileName) {
        return downloadFile("unordered", fileName);
    }

    Task<byte[]> downloadFile(String path, String fileName) {
        String fullPath = path+"/"+ fileName;
        StorageReference fireRef = storage.getReference().child(fullPath);

        return fireRef.getBytes(Long.MAX_VALUE);
    }
}