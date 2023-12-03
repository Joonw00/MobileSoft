package com.example.mobilesoftwareproject;

import android.content.ContentValues;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


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

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String param1 = getArguments().getString("param1");
            String param2 = getArguments().getString("param2");
        }
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
            public void onClick(View view) {
                onRegisterButtonClicked();
            }
        });
        return view;
    }
    public void onRegisterButtonClicked() {
        try {
            String location = locationEditText.getText().toString();
            String foodName = foodNameEditText.getText().toString();
            String beverageName = beverageNameEditText.getText().toString();
            String impressions = impressionsEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String cost = costEditText.getText().toString();
            //FoodBDManager에서 insert 메소드를 호출하여 데이터를 추가한다.

            ContentValues addValues = new ContentValues();
            View fragmentView = getView();
            if (fragmentView != null) {
                addValues.put(MyContentProvider.LOCATION, ((EditText) fragmentView.findViewById(R.id.locationEditText)).getText().toString());
                addValues.put(MyContentProvider.FOOD_NAME, ((EditText) fragmentView.findViewById(R.id.foodNameEditText)).getText().toString());
                addValues.put(MyContentProvider.BEVERAGE_NAME, ((EditText) getView().findViewById(R.id.beverageNameEditText)).getText().toString());
                addValues.put(MyContentProvider.IMPRESSIONS, ((EditText) getView().findViewById(R.id.impressionsEditText)).getText().toString());
                addValues.put(MyContentProvider.TIME, ((EditText) getView().findViewById(R.id.timeEditText)).getText().toString());
                addValues.put(MyContentProvider.COST, ((EditText) getView().findViewById(R.id.costEditText)).getText().toString());
            } else {
                Log.d("RegisterFragment", "fragmentView is null");
                return;
            }


            //database에 contentvalue 인스턴스를 삽입
            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);

            Toast.makeText(getActivity().getBaseContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("RegisterFragment", "Error inserting data to ContentProvider", e);
            Toast.makeText(getActivity().getBaseContext(), "등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
