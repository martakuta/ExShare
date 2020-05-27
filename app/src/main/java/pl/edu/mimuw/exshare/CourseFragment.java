package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONException;

public class CourseFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    private String courseName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        ((MainActivity) requireActivity()).getSupportActionBar().setTitle("Kurs " + courseName);
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_c);

        Bundle b = requireActivity().getIntent().getExtras();
        try {
            userID = b.getString("userID");
        } catch (NullPointerException e) {
            Toast.makeText(requireContext(), "Wystąpił problem z zalogowaniem.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);

        JSONArray courseFolders = DBAccess.getCourseFolders(courseID);
        for (int i = 0; i < courseFolders.length(); i++) {
            try {
                String folderName = courseFolders.get(i).toString();
                System.out.println("folder: " + folderName);

                Button btn = new Button(getActivity());
                btn.setText(folderName);
                btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.myDarkGreen));
                btn.setTextColor(btn.getContext().getResources().getColor(R.color.myDarkBrown));
                btn.setTextSize(17);
                btn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_folder_transp, 0, 0, 0);
                btn.setPadding(50, 0, 50, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 50);
                btn.setLayoutParams(params);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folderName = btn.getText().toString();
                        System.out.println("Go to folder " + folderName);

                        ((MainActivity) requireActivity()).setPresentFolderName(folderName);

                        NavHostFragment.findNavController(CourseFragment.this)
                                .navigate(R.id.action_Course_to_Folder);
                    }
                });
                linearLayout.addView(btn);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray courseTests = DBAccess.getCourseTests(courseID);
        for (int i = 0; i < courseTests.length(); i++) {
            try {
                String testName = courseTests.get(i).toString();
                System.out.println("sprawdzian: " + testName);

                Button btn = new Button(getActivity());
                btn.setText(testName);
                btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.myMiddleGreen));
                btn.setTextColor(btn.getContext().getResources().getColor(R.color.myDarkBrown));
                btn.setTextSize(17);
                btn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_test_transp, 0, 0, 0);
                btn.setPadding(50, 0, 50, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 50);
                btn.setLayoutParams(params);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String testName = btn.getText().toString();
                        System.out.println("Go to test " + testName);

                        ((MainActivity) requireActivity()).setPresentTestName(testName);

                        NavHostFragment.findNavController(CourseFragment.this)
                                .navigate(R.id.action_Course_to_Test);
                    }
                });
                linearLayout.addView(btn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_add_test) {
            System.out.println("Action add test clicked");
            ((MainActivity) requireActivity()).setTestAlreadyCreated(false);
            NavHostFragment.findNavController(CourseFragment.this)
                    .navigate(R.id.action_Course_to_AddTest);
            return true;
        } else if (id == R.id.action_add_folder) {
            System.out.println("Action add folder clicked");
            ((MainActivity) requireActivity()).setTestAlreadyCreated(false);
            NavHostFragment.findNavController(CourseFragment.this)
                    .navigate(R.id.action_Course_to_AddFolder);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
