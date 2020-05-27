package pl.edu.mimuw.exshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.Arrays;


public class AddFolderFragment extends Fragment {
    private FirebaseCloud firebaseCloud = new FirebaseCloud();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_folder, container, false);
    }

    private void setImageView(ImageView imageView, byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayout = view.findViewById(R.id.linear_layout_af);
        LinearLayout coursesCheckbox = view.findViewById(R.id.check_courses);
        EditText folderNamePlace = view.findViewById(R.id.folder_name);
        int courseID = ((MainActivity) requireActivity()).getPresentCourseID();
        Button addCheckedTests = view.findViewById(R.id.add_checked_tests);

        JSONArray courseTests = DBAccess.getCourseTests(courseID);
        for (int i = 0; i < courseTests.length(); i++) {
            try {
                String testName = courseTests.get(i).toString();

                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setChecked(false);
                checkBox.setText(testName);
                coursesCheckbox.addView(checkBox);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        addCheckedTests.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String folderName = folderNamePlace.getText().toString();
                if (folderName.length() == 0) {
                    Toast.makeText(requireContext(), "Musisz nazwać swój folder", Toast.LENGTH_SHORT).show();
                } else {
                    int howManyChecked = 0;
                    System.out.println("Dodano folder o nazwie " + folderName);
                    int checkboxesCount = coursesCheckbox.getChildCount();
                    for (int i = 0; i < checkboxesCount; i++) {
                        CheckBox checkBox = (CheckBox)coursesCheckbox.getChildAt(i);
                        if (checkBox.isChecked()) {
                            howManyChecked++;
                        }
                    }
                    String[] folderArray = new String[howManyChecked+1];
                    folderArray[0] = folderName;
                    for (int i = 0; i < checkboxesCount; i++) {
                        CheckBox checkBox = (CheckBox)coursesCheckbox.getChildAt(i);
                        if (checkBox.isChecked()) {
                            folderArray[howManyChecked] = checkBox.getText().toString();
                            howManyChecked--;
                        }
                    }
                    try {
                        JSONArray jsonFolderArray = new JSONArray(folderArray);
                        System.out.println(jsonFolderArray);
                        DBAccess.addFolder(courseID, jsonFolderArray);
                        NavHostFragment.findNavController(AddFolderFragment.this)
                                .popBackStack();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
