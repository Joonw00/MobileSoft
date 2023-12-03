package com.example.mobilesoftwareproject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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

        // 데이터베이스에서 데이터를 로드
        loadDataFromProvider();
        // TextView에 오늘의 날짜 표시
        TextView textViewDate = view.findViewById(R.id.hometitle);
        String currentDate = getCurrentDate() + " 의 식단";
        textViewDate.setText(currentDate);

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



        private void loadDataFromProvider() {
            // Query the database using ContentProvider
            Cursor cursor = getActivity().getContentResolver().query(
                    MyContentProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            // Update the Cursor in the CursorAdapter
            cursorAdapter.swapCursor(cursor);
        }
    }

