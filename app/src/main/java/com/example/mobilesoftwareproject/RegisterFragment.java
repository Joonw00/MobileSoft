package com.example.mobilesoftwareproject;


import android.content.ContentValues;
import android.net.Uri;
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
                saveDataToProvider();
            }
        });
        return view;
    }
    private void saveDataToProvider() {
        //edittext에 입력된 값을 가져옴
        String location = locationEditText.getText().toString();
        String foodName = foodNameEditText.getText().toString();
        String beverageName = beverageNameEditText.getText().toString();
        String impressions = impressionsEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String cost = costEditText.getText().toString();

        //데이터를 저장할 contentValues 인스턴스 생성
        ContentValues addValues = new ContentValues();
        addValues.put(MyContentProvider.LOCATION, location);
        addValues.put(MyContentProvider.FOOD_NAME, foodName);
        addValues.put(MyContentProvider.BEVERAGE_NAME, beverageName);
        addValues.put(MyContentProvider.IMPRESSIONS, impressions);
        addValues.put(MyContentProvider.TIME, time);
        addValues.put(MyContentProvider.COST, cost);

        //contentvalues 인스턴스를 contentprovider에 삽입

        getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);


        locationEditText.setText("");
        foodNameEditText.setText("");
        beverageNameEditText.setText("");
        impressionsEditText.setText("");
        timeEditText.setText("");
        costEditText.setText("");
    }
}
