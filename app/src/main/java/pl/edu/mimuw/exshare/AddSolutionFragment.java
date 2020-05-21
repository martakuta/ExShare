package pl.edu.mimuw.exshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class AddSolutionFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    private String courseName;
    private String testName;
    private int exerciseNumber;
    private ImageView imageView;
    private FirebaseCloud firebaseCloud = new FirebaseCloud();
    private static final int SELECT_PICTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_solution, container, false);
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

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void setImageView(ImageView imageView, byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        testName = ((MainActivity) requireActivity()).getPresentTestName();
        exerciseNumber = ((MainActivity) requireActivity()).getPresentExerciseNumber();
        imageView = view.findViewById(R.id.add_solution_image);

        view.findViewById(R.id.add_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        view.findViewById(R.id.upload_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    imageView.getDrawable();
                    firebaseCloud.uploadSolutionImage(courseID, courseName, testName, exerciseNumber, imageView);
                } catch (NullPointerException e) {
                    Toast.makeText(requireContext(), "Zdjęcie jest puste", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
