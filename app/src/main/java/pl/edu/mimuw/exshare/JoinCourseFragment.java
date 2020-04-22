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

public class JoinCourseFragment extends Fragment {

    private String userID;
    private EditText course;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_join_course, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if(b != null)
            userID = b.getString("user_id");
        course=view.findViewById(R.id.join_course_place);
        Button sign_to_course_button=view.findViewById(R.id.sign_to_course_button);
        sign_to_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adder","Course added");
                int courseID = Integer.parseInt(course.getText().toString());
                DBAccess.assignUserToCourse(userID,courseID);
                joinedToCourse();

            }
        });
    }
    private void joinedToCourse() {
        Toast.makeText(getActivity(), "Zarejestrowany do kursu", Toast.LENGTH_LONG).show();
    }
}
