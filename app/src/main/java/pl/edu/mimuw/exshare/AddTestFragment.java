package pl.edu.mimuw.exshare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class AddTestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_at);
        EditText testNamePlace = view.findViewById(R.id.test_name);
        int courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        Button addExerciseBtn = view.findViewById(R.id.add_exercise_btn);

        view.findViewById(R.id.create_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testName = testNamePlace.getText().toString();
                if (testName.length() == 0) {
                    Toast.makeText(requireContext(), "Musisz nazwać swój sprawdzian", Toast.LENGTH_SHORT).show();
                } else {
                    DBAccess.addCourseTest(courseID, testName);
                    ((MainActivity) requireActivity()).setPresentTestName(testName);
                    addExerciseBtn.setVisibility(View.VISIBLE);
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

        //TODO:: dodane już zadania dodawać do jakiejś struktury, a potem (niekoniecznie w tym sprincie)
        // jakoś wyświetlać użytkownikowi, co już zostało dodane i pewnie jakis przycisk potwierdzajacy dodanie

    }
}
