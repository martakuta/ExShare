package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class CourseFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    private String courseName;
    // TODO:: private HashSet<Post> coursePosts;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        Bundle b = requireActivity().getIntent().getExtras();
        if (b != null) {
            userID = b.getString("userID");
            userName = b.getString("userName");
            System.out.println("user i course:..." + userID + " " + courseID + " " + courseName);
        } else {
            System.out.println("user i course: bundle is null");
        }

        MutableLiveData<String> mText = new MutableLiveData<>();
        mText.setValue("Cześć " + userName + "!\nWitaj na kursie \"" + courseName + "\"!");

        View root = inflater.inflate(R.layout.fragment_course, container, false);
        final TextView textView = root.findViewById(R.id.text_course);
        mText.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView courseIdInfo = view.findViewById(R.id.course_id_info);
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseIdInfo.setText("Kod kursu: " + courseID + "\nPodaj go swoim znajomym, aby mogli do Ciebie dołączyć");
    }
}
