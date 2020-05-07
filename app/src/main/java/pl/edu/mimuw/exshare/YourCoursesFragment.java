package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

public class YourCoursesFragment extends Fragment {
    private String userID;
    private int courseID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_courses, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        courseID = ((MainActivity)getActivity()).getPresentCourseID();
        Bundle b = requireActivity().getIntent().getExtras();
        if (b != null) {
            userID = b.getString("userID");
            //userName = b.getString("userName");
            System.out.println("user i course:..." + userID + " " + courseID);
        } else {
            System.out.println("user i course: bundle is null");
        }

        JSONArray userCourses = DBAccess.getUserCourses(userID);
        for (int i = 0; i < userCourses.length(); i++) {
            try {
                courseID = (Integer)(userCourses.get(i));
                System.out.println("kurs: " + courseID);

                Button btn = new Button(getActivity());
                btn.setText(String.valueOf(courseID));
                btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.myMiddleBlue));
                btn.setTextColor(btn.getContext().getResources().getColor(R.color.myVeryDarkBlue));
                btn.setTextSize(17);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 50);
                btn.setLayoutParams(params);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String courseIDString = btn.getText().toString();
                        int courseIDInteger = Integer.parseInt(courseIDString);

                        ((MainActivity)getActivity()).setPresentCourseID(courseIDInteger);

                        NavHostFragment.findNavController(YourCoursesFragment.this)
                              .navigate(R.id.action_YourCourses_to_Course);
                        System.out.println("Go to course " + courseIDString);
                    }
                });
                linearLayout.addView(btn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
