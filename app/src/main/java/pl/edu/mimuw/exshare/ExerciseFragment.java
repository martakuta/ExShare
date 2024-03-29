package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageMetadata;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class ExerciseFragment extends Fragment {
    private static final int SELECT_PICTURE = 1;
    private int courseID;
    private String courseName;
    private String testName;
    private int exerciseNumber;
    private ImageView imageView;
    private FirebaseCloud firebaseCloud = new FirebaseCloud();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        exerciseNumber = ((MainActivity) requireActivity()).getPresentExerciseNumber();
        ((MainActivity) requireActivity()).getSupportActionBar().setTitle(String.valueOf(exerciseNumber));
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Picasso.get().load(data.getData()).noPlaceholder().centerCrop().fit()
                        .into(imageView);
            }
        }
    }

    private void setImageView(ImageView imageView, byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        testName = ((MainActivity) requireActivity()).getPresentTestName();
        exerciseNumber = ((MainActivity) requireActivity()).getPresentExerciseNumber();
        imageView = view.findViewById(R.id.exercise_content);

        Task<byte[]> downloadTask = firebaseCloud.downloadContentImage(courseID, courseName, testName, exerciseNumber);
        downloadTask.addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                setImageView(imageView, bytes);
            }
        });

        Task<StorageMetadata> countDownload = firebaseCloud.getSolutionsCount(courseID, courseName, testName, exerciseNumber);

        countDownload.addOnSuccessListener(storageMetadata -> {
            int count = firebaseCloud.pullCount(storageMetadata);
            LinearLayout linearLayout = view.findViewById(R.id.linear_layout_ef);
            showSolutions(count, linearLayout);
        });

        view.findViewById(R.id.add_solution_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ExerciseFragment.this)
                        .navigate(R.id.action_Exercise_to_AddSolution);
            }
        });

        view.findViewById(R.id.comments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).setPresentSolutionNumber(0);

                NavHostFragment.findNavController(ExerciseFragment.this)
                        .navigate(R.id.action_Exercise_to_Comments);
            }
        });
    }

    private void showSolutions(int count, LinearLayout linearLayout) {
        for (int i = 1; i <= count; i++) {
            try {
                Task<byte[]> downloadTask = firebaseCloud.downloadSolutionImage(courseID, courseName, testName, exerciseNumber, i);
                int solutionNumber = i;
                downloadTask.addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(byte[] bytes) {

                        ImageView solutionImg = new ImageView(getActivity());
                        setImageView(solutionImg, bytes);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 50, 0, 0);
                        solutionImg.setLayoutParams(params);
                        linearLayout.addView(solutionImg);

                        Button btn = new Button(getActivity());
                        btn.setText("Komentarze");
                        btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.myLightGreen));
                        btn.setTextColor(btn.getContext().getResources().getColor(R.color.myDarkBrown));
                        btn.setTextSize(13);
                        btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_course_transp, 0, 0, 0);
                        btn.setContentDescription(String.valueOf(solutionNumber));
                        btn.setPadding(50, 0, 50, 0);
                        params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(20, 5, 20, 0);
                        btn.setLayoutParams(params);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int solutionNumber = Integer.parseInt((String) btn.getContentDescription());

                                ((MainActivity) requireActivity()).setPresentSolutionNumber(solutionNumber);

                                NavHostFragment.findNavController(ExerciseFragment.this)
                                        .navigate(R.id.action_Exercise_to_Comments);
                            }
                        });
                        linearLayout.addView(btn);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
