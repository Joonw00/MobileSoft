package com.example.mobilesoftwareproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeFragment extends Fragment {

    private ListView listView;
    private CursorAdapter cursorAdapter;
    private ImageButton titleDateButton;
    private ImageButton deleteButton;
    private TextView textViewDate;
    private StringBuilder title;
    // Define the columns to retrieve from the database
    String[] columns = {
            MyContentProvider.LOCATION,
            MyContentProvider.FOOD_NAME,
            MyContentProvider.IMPRESSIONS,
            MyContentProvider.COST,
            MyContentProvider.CALORIE,
            MyContentProvider.PHOTO
    };

    // Define the XML views to bind the data to
    int[] to = {
            R.id.textViewLocation,
            R.id.textViewFoodName,
            R.id.textViewImpressions,
            R.id.textViewCost,
            R.id.textViewCalorie,
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
        String currentDate = getCurrentDate();
        String title = currentDate + "(오늘)의 식단";
        textViewDate.setText(title);
        loadDataFromProvider(currentDate);

        return view;
    }
    private String getCurrentDate() {
        // 현재 날짜와 시간을 가져오기
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // 날짜 포맷 지정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());

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
                        title = new StringBuilder();
                        // monthOfYear는 0부터 시작하므로 1을 더해줍니다.
                        // 예: 0(Jan), 1(Feb), ..., 11(Dec)
                        if(selectedDate.equals(getCurrentDate()))
                            title.append(selectedDate).append("(오늘)의 식단");
                        else
                            title.append(selectedDate).append(" 의 식단");
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
            try {
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CustomCursorAdapter", e.getMessage());
            }
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            // Get references to views
            TextView textViewLocation = view.findViewById(R.id.textViewLocation);
            TextView textViewFoodName = view.findViewById(R.id.textViewFoodName);
            TextView textViewImpressions = view.findViewById(R.id.textViewImpressions);
            TextView textViewCost = view.findViewById(R.id.textViewCost);
            ImageView imageViewFood = view.findViewById(R.id.imageViewFood);
            TextView textViewCalorie = view.findViewById(R.id.textViewCalorie);
            String type = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TYPE));


            // Extract data from the cursor
            String location = "위치 :" + cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.LOCATION));
            String foodName = "음식 이름 : "+ cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FOOD_NAME));
            String impressions = "감상평 : "+cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.IMPRESSIONS));
            int cost = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COST));
            int calorie = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.CALORIE));
            byte[] photoByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(MyContentProvider.PHOTO));

            // Set data to views
            textViewLocation.setText(location);
            textViewFoodName.setText(foodName);
            textViewCost.setText("가격 : " + String.valueOf(cost)+"원");
            textViewCalorie.setText("칼로리 : " + String.valueOf(calorie) +"Kcal");
            textViewImpressions.setText(impressions);

            // Set the photo from byte array
            if (photoByteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);
                imageViewFood.setImageBitmap(bitmap);
            }
            deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setTag(cursor.getLong(cursor.getColumnIndexOrThrow(MyContentProvider.ID))); // Set the ID as the tag for later retrieval
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the ID from the tag
                    long id = (long) v.getTag();
                    // Call a method to delete the item with the given ID
                    deleteItem(id);
                }
            });
        }
    }

    // HomeFragment 클래스에
    private void deleteItem(long id) {
        // ContentResolver를 사용하여 해당 ID에 해당하는 데이터를 삭제
        int rowsDeleted = getActivity().getContentResolver().delete(
                MyContentProvider.CONTENT_URI,
                MyContentProvider.ID + "=?",
                new String[]{String.valueOf(id)}
        );
        String extractedDate = getTextViewDateText();

        // 삭제가 성공적으로 이루어졌을 경우에만 메시지를 표시
            // 삭제가 성공적으로 이루어졌을 경우에만 메시지를 표시
            if (rowsDeleted > 0) {
                loadDataFromProvider(extractedDate);

        }
    }
    private String getTextViewDateText() {
        TextView hometitleTextView = requireView().findViewById(R.id.hometitle);
        String title = hometitleTextView.getText().toString(); //date 외의 글자도 포함.
        String res = "";
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{1,2}");
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            res = matcher.group();
        }
        return res;
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

