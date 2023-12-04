package com.example.mobilesoftwareproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private ListView listView;
    private CursorAdapter cursorAdapter;
    private Button titleDateButton;
    private TextView textViewDate;
    // Define the columns to retrieve from the database
    String[] columns = {
            MyContentProvider.LOCATION,
            MyContentProvider.FOOD_NAME,
            MyContentProvider.BEVERAGE_NAME,
            MyContentProvider.IMPRESSIONS,
            MyContentProvider.TIME,
            MyContentProvider.COST,
            MyContentProvider.PHOTO
    };

    // Define the XML views to bind the data to
    int[] to = {
            R.id.textViewLocation,
            R.id.textViewFoodName,
            R.id.textViewBeverageName,
            R.id.textViewImpressions,
            R.id.textViewTime,
            R.id.textViewCost,
            R.id.imageViewFood
    };


    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 레이아웃을 팽창시킴
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ListView 및 CursorAdapter 초기화
        listView = view.findViewById(R.id.listView);
        cursorAdapter = new CustomCursorAdapter(
                getActivity(),
                R.layout.list_item_layout,
                null,
                columns,
                to,
                0
        );
        listView.setAdapter(cursorAdapter);
        // onCreateView 내부에서
        titleDateButton = view.findViewById(R.id.titleDateButton);
        titleDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogForData();
            }
        });


        // 데이터베이스에서 데이터를 로드
        // TextView에 오늘의 날짜 표시
        textViewDate = view.findViewById(R.id.hometitle);

        return view;
    }
    private String getCurrentDate() {
        // 현재 날짜와 시간을 가져오기
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // 날짜 포맷 지정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 현재 날짜를 문자열로 반환
        return dateFormat.format(currentDate);
    }
    private void showDatePickerDialogForData() {
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

                        String title = selectedDate+ " 의 식단";
                        textViewDate.setText(title);
                        // Load data from the provider for the selected date
                        loadDataFromProvider(selectedDate);
                    }
                },
                year, // 초기 년도 - 현재 년도 또는 기본값으로 설정
                month, // 초기 월 - 현재 월 또는 기본값으로 설정
                day // 초기 일 - 현재 일 또는 기본값으로 설정
        );

        // DatePickerDialog를 표시
        datePickerDialog.show();
    }


    private class CustomCursorAdapter extends SimpleCursorAdapter {

        public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get references to views
            TextView textViewLocation = view.findViewById(R.id.textViewLocation);
            TextView textViewFoodName = view.findViewById(R.id.textViewFoodName);
            TextView textViewBeverageName = view.findViewById(R.id.textViewBeverageName);
            TextView textViewImpressions = view.findViewById(R.id.textViewImpressions);
            TextView textViewTime = view.findViewById(R.id.textViewTime);
            TextView textViewCost = view.findViewById(R.id.textViewCost);
            ImageView imageViewFood = view.findViewById(R.id.imageViewFood);

            // Extract data from the cursor
            String location = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.LOCATION));
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FOOD_NAME));
            String beverageName = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.BEVERAGE_NAME));
            String impressions = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.IMPRESSIONS));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TIME));
            String cost = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.COST));
            byte[] photoByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(MyContentProvider.PHOTO));

            // Set data to views
            textViewLocation.setText(location);
            textViewFoodName.setText(foodName);
            textViewBeverageName.setText(beverageName);
            textViewImpressions.setText(impressions);
            textViewTime.setText(time);
            textViewCost.setText(cost);

            // Set the photo from byte array
            if (photoByteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);
                imageViewFood.setImageBitmap(bitmap);
            }
        }
    }



    private void loadDataFromProvider(String selectedDate) {
        // Query the database using ContentProvider with the selected date condition
        Cursor cursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " LIKE ?",
                new String[]{selectedDate + "%"},  // Use the selected date as a prefix
                null
        );

        // Update the Cursor in the CursorAdapter
        cursorAdapter.swapCursor(cursor);
    }

}

