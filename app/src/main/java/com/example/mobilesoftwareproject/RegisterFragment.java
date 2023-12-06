package com.example.mobilesoftwareproject;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class RegisterFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private EditText foodNameEditText;
    private EditText impressionsEditText;
    private TextView dateEditText;
    private EditText costEditText;
    private Button registerButton;
    private Button cameraButton;
    private Button galleryButton;
    private ImageView foodImage;
    private Uri uri;
    private Spinner locationtypeSpinner;
    private Spinner mealTypespinner;
    private ImageButton datePickerButton;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        locationtypeSpinner = view.findViewById(R.id.locationSpinner);
        mealTypespinner = view.findViewById(R.id.mealTypespinner);
        String[] locationTypes = {"상록원 3층", "상록원 2층", "상록원 1층", "남산학사","그루터기","가든쿡","편의점"};
        String[] mealTypes = {"아침","점심","저녁","음료"};
        // Initialize your EditText fields
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, locationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationtypeSpinner.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, mealTypes);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypespinner.setAdapter(adapter2);
        foodNameEditText = view.findViewById(R.id.foodNameEditText);
        impressionsEditText = view.findViewById(R.id.impressionsEditText);
        costEditText = view.findViewById(R.id.costEditText);
        dateEditText = view.findViewById(R.id.dateEditText);
        foodImage = view.findViewById(R.id.foodImage);


        // Initialize the  button
        registerButton = view.findViewById(R.id.registerButton);
        cameraButton = view.findViewById(R.id.cameraButton);
        galleryButton = view.findViewById(R.id.galleryButton);
        datePickerButton = view.findViewById(R.id.datePickerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToProvider();
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityResult.launch(intent);
            }
        });
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        return view;

    }
    private void showDatePickerDialog() {
        // 현재 날짜를 기본값으로 설정
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog를 생성하고 설정
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 사용자가 선택한 날짜를 처리
                        String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        // monthOfYear는 0부터 시작하므로 1을 더해줍니다.
                        // 예: 0(Jan), 1(Feb), ..., 11(Dec)
                        dateEditText.setText(selectedDate);
                    }
                },
                year, // 초기 년도 - 현재 년도 또는 기본값으로 설정
                month, // 초기 월 - 현재 월 또는 기본값으로 설정
                day // 초기 일 - 현재 일 또는 기본값으로 설정
        );

        // DatePickerDialog를 표시
        datePickerDialog.show();
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                    {
                        uri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            foodImage.setImageBitmap(bitmap);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
            }

    );

    private void saveDataToProvider() {
        // EditText 필드에서 데이터를 가져옴
        String selectedlocation = locationtypeSpinner.getSelectedItem().toString();
        String selectedType = mealTypespinner.getSelectedItem().toString();
        String foodName = foodNameEditText.getText().toString();
        String impressions = impressionsEditText.getText().toString();
        String time = dateEditText.getText().toString();
        String costString = costEditText.getText().toString();
        Integer cost =0;
        Integer calorie=0;
        byte[] photoBytes = imageToByteArray(foodImage);
        if (foodName.isEmpty() || impressions.isEmpty() || time.isEmpty() || costString.isEmpty() ) {
            Toast.makeText(getContext(), "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!costString.isEmpty()) {
            try {
                cost = Integer.parseInt(costString);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "비용 란에 숫자만 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if("아침".equals(selectedType))
        {
            calorie = 400;
        }
        else if("점심".equals(selectedType))
        {
            calorie = 600;
        }
        else if("저녁".equals(selectedType))
        {
            calorie = 800;
        }
        else if("음료".equals(selectedType))
        {
            calorie = 800;
        }
        else {
            calorie = 0;
        }


        // 데이터를 저장할 ContentValues 인스턴스 생성
        ContentValues addValues = new ContentValues();
        addValues.put(MyContentProvider.LOCATION, selectedlocation);
        addValues.put(MyContentProvider.TYPE, selectedType);
        addValues.put(MyContentProvider.CALORIE, calorie);
        addValues.put(MyContentProvider.FOOD_NAME, foodName);
        addValues.put(MyContentProvider.IMPRESSIONS, impressions);
        addValues.put(MyContentProvider.TIME, time);
        addValues.put(MyContentProvider.COST, cost);
        addValues.put(MyContentProvider.PHOTO, photoBytes);

        try {
            // ContentProvider에 데이터 삽입
            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);
            Toast.makeText(getActivity().getBaseContext(), "식사 기록 추가됨", Toast.LENGTH_LONG).show();

            // 성공적인 삽입 후 EditText 필드 지우기 , 사진 기본이미지로 바꾸기
            foodNameEditText.setText("");
            impressionsEditText.setText("");
            dateEditText.setText("");
            costEditText.setText("");
            clearImageView();

        } catch (Exception e) {
            // 예외 처리 (예: 데이터베이스 오류)
            e.printStackTrace();
            Log.e("RegisterFragment", "Error in try-catch block: " + e.getMessage());
            Toast.makeText(getActivity().getBaseContext(), "식사 기록 추가 실패", Toast.LENGTH_LONG).show();
        }
    }

    // 이미지 파일을 바이트 배열로 변환
    private byte[] imageToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }
    private void clearImageView() {
        foodImage.setImageResource(R.drawable.camera);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) { //사진찍기 버튼 클릭
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // 이미지뷰에 이미지 표시
            foodImage.setImageBitmap(photo);
        }
    }
}
