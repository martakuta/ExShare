package pl.edu.mimuw.exshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.Charset;

public class CreateCourseFragment extends Fragment {
    private String userID;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_course, container, false);
    }
    private void handleUploadTask(UploadTask uploadTask) {
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Log.i("[UPLOADER]","Upload Failed: " + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("[UPLOADER]","Upload Successful");
            }
        });
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if(b != null)
            userID = b.getString("userID");

        if(userID != null) {
            byte[] example_file = userID.getBytes(Charset.forName("UTF-8"));
            FirebaseCloud firebaseCloud = new FirebaseCloud();
            UploadTask uploadTask;

            uploadTask = firebaseCloud.uploadFile("example_directory", "example_name.txt", example_file);
            handleUploadTask(uploadTask);

        }

        EditText courseNamePlace=view.findViewById(R.id.create_course_place);
        Button create_new_course_button=view.findViewById(R.id.create_new_course_button);
        create_new_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = courseNamePlace.getText().toString();
                int courseID = DBAccess.getNewCourse(userID, courseName);
                if (DBAccess.assignUserToCourse(userID, courseID) != 1) {
                    throw new AssertionError("An error occurred while adding user to the course.");
                }
                System.out.println("Nazwa kursu:" + courseName + " Numer kursu:" + courseID + "Użytkownik:" + userID);

                ((MainActivity) requireActivity()).setPresentCourseID(courseID);

                NavHostFragment.findNavController(CreateCourseFragment.this)
                        .navigate(R.id.action_CreateCourse_to_Course);
            }
        });
    }
}

