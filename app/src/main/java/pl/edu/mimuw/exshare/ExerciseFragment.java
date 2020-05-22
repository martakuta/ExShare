package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class ExerciseFragment extends Fragment {
    private static final int SELECT_PICTURE = 1;
    private String userID;
    private String userName;
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
        ((MainActivity) requireActivity()).getSupportActionBar().setTitle("Zadanie " + exerciseNumber);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_ef);

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
        exerciseNumber = ((MainActivity) requireActivity()).getPresentExerciseNumber();
        imageView = view.findViewById(R.id.exercise_content);

        Task<byte[]> downloadTask = firebaseCloud.downloadContentImage(courseID, courseName, testName, exerciseNumber);
        downloadTask.addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                setImageView(imageView, bytes);
            }
        });

        view.findViewById(R.id.add_solution_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ExerciseFragment.this)
                        .navigate(R.id.action_Exercise_to_AddSolution);
            }
        });


        //JSONArray exercise = DBAccess.getExercise(courseID, testName, exerciseNumber); // TODO:: getExercise które zwraca listę: 1. to treść, kolejne to dodane już rozwiązania
        /* TODO::
         * i teraz tutaj musi byc cos co złapie z layoutu pudełko na treść zadania (ImageView content = view.findViewById(R.id.exercise_content);)
         * a potem przypisze mu obraz który w jakiejś formie dostanie z bazy danych, przez DBAccess.getExercise w pierwszym elemencie listy
         * następnie z kolejnych elementów listy pobierze kolejne rozwiązania i je podobnie wyświetli - dodając dla każdego z nich nowy ImageView czy coś takiego
         * (ponieważ nie wiadomo ile ich będzie, może 0, to trzeba je tutaj dodawać dynamicznie (to chyba robi się podobnie jak dynamicznie dodawane buttony w klasach
         * YourCourseFragment, CourseFragment i TestFragment)
         */
    }
}
