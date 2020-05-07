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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import java.util.HashSet;

public class CreateCourseFragment extends Fragment {
    private String userID;
    private EditText course;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_course, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = requireActivity().getIntent().getExtras();
        if(b != null)
            userID = b.getString("userID");
        course=view.findViewById(R.id.create_course_place);
        Button create_new_course_button=view.findViewById(R.id.create_new_course_button);
        create_new_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = course.getText().toString();
                int courseID = DBAccess.getNewCourse(userID, courseName);
                if (DBAccess.assignUserToCourse(userID, courseID) != 1) {
                    // wyjątek że kurs nie istnieje lub nie udało się dodać
                };
                System.out.println("Nazwa kursu:" + courseName + " Numer kursu:" + courseID + "Użytkownik:" + userID);

                ((MainActivity)getActivity()).setPresentCourseID(courseID);
                Toast.makeText(getActivity(), "Właśnie stworzyłeś kurs!", Toast.LENGTH_LONG).show();

                NavHostFragment.findNavController(CreateCourseFragment.this)
                        .navigate(R.id.action_CreateCourse_to_Course);
            }
        });
    }
}

