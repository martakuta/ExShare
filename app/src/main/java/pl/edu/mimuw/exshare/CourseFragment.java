package pl.edu.mimuw.exshare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;

public class CourseFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    // TODO:: private HashSet<Post> coursePosts;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        courseID = ((MainActivity)getActivity()).getPresentCourseID();
        Bundle b = requireActivity().getIntent().getExtras();
        if (b != null) {
            userID = b.getString("userID");
            userName = b.getString("userName");
            System.out.println("user i course:..." + userID + " " + courseID);
        } else {
            System.out.println("user i course: bundle is null");
        }

        MutableLiveData<String> mText = new MutableLiveData<>();
        mText.setValue("Cześć " + userName + "!\nWitaj na kursie \"" + courseID + "\"!");

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
}
