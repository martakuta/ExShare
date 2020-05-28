package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONException;

public class TestFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    private String courseName;
    private String testName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        testName = ((MainActivity) requireActivity()).getPresentTestName();
        ((MainActivity) requireActivity()).getSupportActionBar().setTitle(testName);
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_tf);

        Bundle b = requireActivity().getIntent().getExtras();
        try {
            userID = b.getString("userID");
        } catch (NullPointerException e) {
            Toast.makeText(requireContext(), "Wystąpił problem z zalogowaniem.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        testName = ((MainActivity) requireActivity()).getPresentTestName();

        JSONArray testExercises = DBAccess.getTestExercises(courseID, testName);
        for (int i = 0; i < testExercises.length(); i++) {
            try {
                int exerciseNumber = (Integer) testExercises.get(i);
                System.out.println("zadanie " + exerciseNumber);

                Button btn = new Button(getActivity());
                btn.setText(String.valueOf(exerciseNumber));
                btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.myLightGreen));
                btn.setTextColor(btn.getContext().getResources().getColor(R.color.myDarkBrown));
                btn.setTextSize(17);
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_note_transp, 0, 0, 0);
                btn.setPadding(50, 0, 50, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 50, 50);
                btn.setLayoutParams(params);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int exerciseNumber = Integer.parseInt(btn.getText().toString());
                        System.out.println("Go to exercise " + exerciseNumber);

                        ((MainActivity) requireActivity()).setPresentExerciseNumber(exerciseNumber);

                        NavHostFragment.findNavController(TestFragment.this)
                                .navigate(R.id.action_Test_to_Exercise);
                    }
                });
                linearLayout.addView(btn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
