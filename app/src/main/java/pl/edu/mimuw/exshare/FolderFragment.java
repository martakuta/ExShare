package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class FolderFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    private String courseName;
    private String folderName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        folderName = ((MainActivity) requireActivity()).getPresentFolderName();
        ((MainActivity) requireActivity()).getSupportActionBar().setTitle("Folder " + folderName);
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_f);

        Bundle b = requireActivity().getIntent().getExtras();
        try {
            userID = b.getString("userID");
        } catch (NullPointerException e) {
            Toast.makeText(requireContext(), "Wystąpił problem z zalogowaniem.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);

        JSONArray folderTests = DBAccess.getFolderTests(courseID, folderName);
        for (int i = 0; i < folderTests.length(); i++) {
            try {
                String testName = folderTests.get(i).toString();
                System.out.println("sprawdzian: " + testName);

                Button btn = new Button(getActivity());
                btn.setText(testName);
                btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.myLightBlue));
                btn.setTextColor(btn.getContext().getResources().getColor(R.color.myVeryDarkBlue));
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

                        NavHostFragment.findNavController(FolderFragment.this)
                                .navigate(R.id.action_Folder_to_Test);
                    }
                });
                linearLayout.addView(btn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
