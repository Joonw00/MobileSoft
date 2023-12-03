package com.example.mobilesoftwareproject;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegisterFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private EditText locationEditText;
    private EditText foodNameEditText;
    private EditText beverageNameEditText;
    private EditText impressionsEditText;
    private EditText timeEditText;
    private EditText costEditText;
    private Button registerButton;
    private Button cameraButton;
    private Button galleryButton;
    private ImageView foodImage;
    private Uri uri;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize your EditText fields
        locationEditText = view.findViewById(R.id.locationEditText);
        foodNameEditText = view.findViewById(R.id.foodNameEditText);
        beverageNameEditText = view.findViewById(R.id.beverageNameEditText);
        impressionsEditText = view.findViewById(R.id.impressionsEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        costEditText = view.findViewById(R.id.costEditText);
        foodImage = view.findViewById(R.id.foodImage);


        // Initialize the register button
        registerButton = view.findViewById(R.id.registerButton);
        cameraButton = view.findViewById(R.id.cameraButton);
        galleryButton = view.findViewById(R.id.galleryButton);
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
        return view;

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
        String location = locationEditText.getText().toString();
        String foodName = foodNameEditText.getText().toString();
        String beverageName = beverageNameEditText.getText().toString();
        String impressions = impressionsEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String cost = costEditText.getText().toString();
        byte[] photoBytes = imageToByteArray(foodImage);


        // 데이터를 저장할 ContentValues 인스턴스 생성
        ContentValues addValues = new ContentValues();
        addValues.put(MyContentProvider.LOCATION, location);
        addValues.put(MyContentProvider.FOOD_NAME, foodName);
        addValues.put(MyContentProvider.BEVERAGE_NAME, beverageName);
        addValues.put(MyContentProvider.IMPRESSIONS, impressions);
        addValues.put(MyContentProvider.TIME, time);
        addValues.put(MyContentProvider.COST, cost);
        addValues.put(MyContentProvider.PHOTO, photoBytes);

        try {
            // ContentProvider에 데이터 삽입
            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);
            Toast.makeText(getActivity().getBaseContext(), "식사 기록 추가됨", Toast.LENGTH_LONG).show();

            // 성공적인 삽입 후 EditText 필드 지우기 , 사진 기본이미지로 바꾸기
            locationEditText.setText("");
            foodNameEditText.setText("");
            beverageNameEditText.setText("");
            impressionsEditText.setText("");
            timeEditText.setText("");
            costEditText.setText("");
            clearImageView();

        } catch (Exception e) {
            // 예외 처리 (예: 데이터베이스 오류)
            e.printStackTrace();
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

    private String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }

    private void setImageView(String imagePath) {
        // 이미지 경로를 사용하여 Bitmap으로 변환
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        // 이미지뷰에 Bitmap 설정
        foodImage.setImageBitmap(bitmap);
    }


}
