package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;


public class ExerciseFragment extends Fragment {
    private String userID;
    private String userName;
    private int courseID;
    private String courseName;
    private String testName;
    private int exerciseNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise, container, false);
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
