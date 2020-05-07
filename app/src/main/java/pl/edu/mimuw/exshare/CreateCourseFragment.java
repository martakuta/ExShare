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
                Log.i("Adder","Course added");
                int courseID = Integer.parseInt(course.getText().toString()); // TODO:: jakiś globalny counter,
                                // TODO:: żeby nadawać kolejne numery kursom, ale akceptować nazwy też z innych znaków niz cyfry
                //TODO:: DBAccess.addCourse(userID,courseID);
                System.out.println("Numer kursu:" + courseID + "uzytkownik:" + userID);
                DBAccess.assignUserToCourse(userID, courseID);

                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, new CourseFragment(courseID));
                ft.commit();*/

                ((MainActivity)getActivity()).setPresentCourseID(courseID);

                NavHostFragment.findNavController(CreateCourseFragment.this)
                        .navigate(R.id.action_CreateCourse_to_Course);

        /*Fragment nextFrag= new Fragment();
        Intent i = new Intent(getActivity(), Fragment.class);
        startActivity(i);


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.Layout_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();*/
            }
        });
    }
}

