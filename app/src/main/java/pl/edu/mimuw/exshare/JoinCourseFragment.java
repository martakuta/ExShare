package pl.edu.mimuw.exshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class JoinCourseFragment extends Fragment {

    private String userID;
    private EditText course;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_join_course, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if (b != null)
            userID = b.getString("userID");
        course = view.findViewById(R.id.join_course_place);
        Button sign_to_course_button = view.findViewById(R.id.sign_to_course_button);
        sign_to_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int courseID = Integer.parseInt(course.getText().toString());
                if (DBAccess.assignUserToCourse(userID, courseID) != 1) {
                    throw new AssertionError("An error occurred while adding user to the course.");
                }
                ((MainActivity) requireActivity()).setPresentCourseID(courseID);

                NavHostFragment.findNavController(JoinCourseFragment.this)
                        .navigate(R.id.action_JoinCourse_to_Course);

            }
        });
    }
}
