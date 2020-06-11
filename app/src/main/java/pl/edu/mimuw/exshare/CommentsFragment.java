package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;

public class CommentsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_comm);


        int courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        String testName = ((MainActivity) requireActivity()).getPresentTestName();
        int exerciseNumber = ((MainActivity) requireActivity()).getPresentExerciseNumber();
        int solutionNumber = ((MainActivity) requireActivity()).getPresentSolutionNumber();
        EditText commentPlace = view.findViewById(R.id.add_comment_place);

        JSONArray comments = DBAccess.getComments(courseID, testName, exerciseNumber, solutionNumber);
        assert comments != null;
        for (int i = 0; i < comments.length(); i++) {
            try {
                String comment = comments.get(i).toString();

                TextView textView = new TextView(getActivity());
                textView.setText(comment);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 40, 0, 0);
                textView.setPadding(30, 30, 30, 30);
                textView.setBackgroundColor(textView.getContext().getResources().getColor(R.color.myBackgroundGreen));
                textView.setTextColor(textView.getContext().getResources().getColor(R.color.myDarkBrown));
                textView.setLayoutParams(params);
                linearLayout.addView(textView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        view.findViewById(R.id.add_comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentPlace.getText().toString();
                if (!DBAccess.addComment(courseID, testName, exerciseNumber, solutionNumber, comment)) {
                    Toast.makeText(requireContext(), "Nie udało się dodać komentarza.", Toast.LENGTH_SHORT).show();
                    System.out.println("Add comment: " + courseID + " " + testName + " " + exerciseNumber + " " + solutionNumber + " " + comment);
                } else {
                    commentPlace.setText("");

                    TextView textView = new TextView(getActivity());
                    textView.setText(comment);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 40, 0, 0);
                    textView.setPadding(30, 30, 30, 30);
                    textView.setBackgroundColor(textView.getContext().getResources().getColor(R.color.myBackgroundGreen));
                    textView.setTextColor(textView.getContext().getResources().getColor(R.color.myDarkBrown));
                    textView.setLayoutParams(params);
                    linearLayout.addView(textView);
                }
            }
        });
    }
}
