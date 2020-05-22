package pl.edu.mimuw.exshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class JoinCourseFragment extends Fragment {

    private String userID;
    private int courseID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_join_course, container, false);
    }

    private void handleDownloadedFile(String content) {
        Log.i("[DOWNLOADER]","Downloaded a String: " + content);
    }
    private void handleDownloadTask(Task<byte[]> downloadTask) {
        downloadTask.addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                handleDownloadedFile(new String(bytes));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("[DOWNLOADER]", "Download Failed: " + e.getMessage());
            }
        });
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if (b != null)
            userID = b.getString("userID");

        FirebaseCloud firebaseCloud = new FirebaseCloud();

        Task<byte[]> downloadTask = firebaseCloud.downloadFile("example_directory", "example_name.txt");
        handleDownloadTask(downloadTask);

        EditText courseIDPlace = view.findViewById(R.id.join_course_place);
        Button sign_to_course_button = view.findViewById(R.id.sign_to_course_button);
        sign_to_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseIDString = courseIDPlace.getText().toString();
                try {
                    courseID = Integer.parseInt(courseIDString);

                    if (DBAccess.assignUserToCourse(userID, courseID) != 1) {
                        throw new AssertionError("An error occurred while adding user to the course.");
                    }
                    ((MainActivity) requireActivity()).setPresentCourseID(courseID);

                    NavHostFragment.findNavController(JoinCourseFragment.this)
                            .navigate(R.id.action_JoinCourse_to_Course);

                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Kod kursu powinien być liczbą naturalną.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (AssertionError e) {
                    Toast.makeText(requireContext(), "Kurs o takim kodzie nie istnieje.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
