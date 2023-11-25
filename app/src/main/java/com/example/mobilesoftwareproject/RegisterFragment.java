package com.example.mobilesoftwareproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    private EditText locationEditText;
    private EditText foodNameEditText;
    private EditText beverageNameEditText;
    private EditText impressionsEditText;
    private EditText timeEditText;
    private EditText costEditText;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize your EditText fields
        locationEditText = view.findViewById(R.id.locationEditText);
        foodNameEditText = view.findViewById(R.id.foodNameEditText);
        beverageNameEditText = view.findViewById(R.id.beverageNameEditText);
        impressionsEditText = view.findViewById(R.id.impressionsEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        costEditText = view.findViewById(R.id.costEditText);

        // Initialize the register button
        View registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                // 여기에 버튼을 클릭했을 때 수행할 동작을 추가하세요.
            }
        });
        return view;
    }
}
