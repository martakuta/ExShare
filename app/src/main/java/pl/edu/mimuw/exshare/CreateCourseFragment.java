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

public class CreateCourseFragment extends Fragment {
    private String userID;
    private EditText course;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_create_course, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if(b != null)
            userID = b.getString("user_id");
        course=view.findViewById(R.id.create_course_place);
        Button create_new_course_button=view.findViewById(R.id.create_new_course_button);
        create_new_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adder","Course added");
                int courseID = Integer.parseInt(course.getText().toString());
                //TODO:: DBAccess.addCourse(userID,courseID);
                courseAdded();

            }
        });
    }
    private void courseAdded() {

    }

}

