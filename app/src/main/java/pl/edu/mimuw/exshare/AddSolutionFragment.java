package pl.edu.mimuw.exshare;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class AddSolutionFragment extends Fragment {
    private int courseID;
    private String courseName;
    private String testName;
    private int exerciseNumber;
    private ImageView imageView;
    ProgressBar progressBar;
    Button uploadButton;
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

    private void addSolution(ImageView imageView) throws NullPointerException {
        String path = "courses/" + courseID + "/" + courseName + "/" + testName + "/solution/" + exerciseNumber;
        StorageReference metadataRef = firebaseCloud.getStorageReference(path, "metadata");

        metadataRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            String countStr = storageMetadata.getCustomMetadata("imageCount");
            int count = firebaseCloud.atoi(countStr) + 1;
            firebaseCloud.updateMetadata(metadataRef, count).addOnSuccessListener(storageMetadata1 -> {
                UploadTask uploadTask = firebaseCloud.uploadImage(path, imageView, count);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    NavHostFragment.findNavController(AddSolutionFragment.this)
                            .popBackStack();
                }).addOnFailureListener(e -> {
                    NavHostFragment.findNavController(AddSolutionFragment.this)
                            .popBackStack();
                });
            });

        }).addOnFailureListener(e -> {
            int errorCode = ((StorageException) e).getErrorCode();
            if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                StorageMetadata metadata = firebaseCloud.createMetadata(0);
                byte[] empty_file = {0};

                metadataRef.putBytes(empty_file, metadata).addOnSuccessListener(taskSnapshot -> {
                    firebaseCloud.updateMetadata(metadataRef, 1).addOnSuccessListener(m -> {
                        UploadTask uploadTask = firebaseCloud.uploadImage(path, imageView, 1);
                        uploadTask.addOnSuccessListener(taskSnapshot1 -> {
                            NavHostFragment.findNavController(AddSolutionFragment.this)
                                    .popBackStack();
                        }).addOnFailureListener(e1 -> {
                            NavHostFragment.findNavController(AddSolutionFragment.this)
                                    .popBackStack();
                        });
                    });
                });
            }

        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uploadButton = view.findViewById(R.id.upload_btn);
        progressBar = view.findViewById(R.id.upload_progress_bar);
        progressBar.setVisibility(View.GONE);
        courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        courseName = DBAccess.getCourseName(courseID);
        testName = ((MainActivity) requireActivity()).getPresentTestName();
        exerciseNumber = ((MainActivity) requireActivity()).getPresentExerciseNumber();
        imageView = view.findViewById(R.id.add_solution_image);

        Task<StorageMetadata> countDownload = firebaseCloud.getSolutionsCount(courseID, courseName, testName, exerciseNumber);

        countDownload.addOnSuccessListener(storageMetadata -> {
            int count = firebaseCloud.pullCount(storageMetadata);
            handleCount(count);
        }).addOnFailureListener(e -> {
            int count = 0;
            handleCount(count);
        });

        view.findViewById(R.id.add_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        uploadButton.setOnClickListener(view1 -> {
            try {
                uploadButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                imageView.getDrawable();
                addSolution(imageView);

            } catch (NullPointerException e) {
                Toast.makeText(requireContext(), "Zdjęcie jest puste", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void handleCount(int count) {
        if (count != 0)
            Toast.makeText(requireContext(), "zadanie już ma " + count + " rozwiązań", Toast.LENGTH_LONG).show();
    }
}
