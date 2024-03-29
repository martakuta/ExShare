package pl.edu.mimuw.exshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class CreateCourseFragment extends Fragment {
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_course, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if (b != null)
            userID = b.getString("userID");

        EditText courseNamePlace = view.findViewById(R.id.create_course_place);
        Button create_new_course_button = view.findViewById(R.id.create_new_course_button);
        create_new_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = courseNamePlace.getText().toString();
                int courseID = DBAccess.getNewCourse(userID, courseName);
                if (DBAccess.assignUserToCourse(userID, courseID) != 1) {
                    Toast.makeText(requireContext(), "Nie udało się stowrzyć kursu.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Nazwa kursu:" + courseName + " Numer kursu:" + courseID + "Użytkownik:" + userID);

                    ((MainActivity) requireActivity()).setPresentCourseID(courseID);

                    NavHostFragment.findNavController(CreateCourseFragment.this)
                            .navigate(R.id.action_CreateCourse_to_Course);
                }
            }
        });
    }
}

