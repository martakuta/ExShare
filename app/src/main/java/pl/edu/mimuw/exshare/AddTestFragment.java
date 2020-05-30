package pl.edu.mimuw.exshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;


public class AddTestFragment extends Fragment {
    private FirebaseCloud firebaseCloud = new FirebaseCloud();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_test, container, false);
    }

    private void setImageView(ImageView imageView, byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_at);
        LinearLayout alreadyAddedExercises = view.findViewById(R.id.already_added_exercises);
        EditText testNamePlace = view.findViewById(R.id.test_name);
        int courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        String courseName = DBAccess.getCourseName(courseID);
        Button addExerciseBtn = view.findViewById(R.id.add_exercise_btn);
        Button createTestBtn = view.findViewById(R.id.create_test);
        Button confirmBtn = view.findViewById(R.id.confirm_add_test);

        boolean alreadyCreated = ((MainActivity) requireActivity()).getTestAlreadyCreated();
        System.out.println("alreadyCreated=" + alreadyCreated);
        if (alreadyCreated) {
            String testName = ((MainActivity) requireActivity()).getPresentTestName();
            testNamePlace.setText(testName);
            addExerciseBtn.setVisibility(View.VISIBLE);
            confirmBtn.setVisibility(View.VISIBLE);
            createTestBtn.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
            params.setMargins(0, 0, 0, 0);
            createTestBtn.setLayoutParams(params);

            JSONArray testExercises = DBAccess.getTestExercises(courseID, testName);
            for (int i = 0; i < testExercises.length(); i++) {
                try {
                    int exerciseNumber = (Integer) testExercises.get(i);
                    System.out.println("i: exercise number = " + i + ": " + exerciseNumber);
                    Task<byte[]> downloadTask = firebaseCloud.downloadContentImage(courseID, courseName, testName, exerciseNumber);
                    downloadTask.addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            ImageView solutionImg = new ImageView(getActivity());
                            setImageView(solutionImg, bytes);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    600);
                            params.setMargins(0, 0, 0, 40);
                            solutionImg.setLayoutParams(params);
                            alreadyAddedExercises.addView(solutionImg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        createTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testName = testNamePlace.getText().toString();
                if (testName.length() == 0) {
                    Toast.makeText(requireContext(), "Musisz nazwać swój sprawdzian", Toast.LENGTH_SHORT).show();
                } else {
                    DBAccess.addCourseTest(courseID, testName);
                    ((MainActivity) requireActivity()).setPresentTestName(testName);
                    addExerciseBtn.setVisibility(View.VISIBLE);
                    ((MainActivity) requireActivity()).setTestAlreadyCreated(true);
                }
            }
        });

        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddTestFragment.this)
                        .navigate(R.id.action_AddTest_to_AddExercise);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddTestFragment.this)
                        .navigate(R.id.action_AddTest_to_Test);
            }
        });
    }
}
