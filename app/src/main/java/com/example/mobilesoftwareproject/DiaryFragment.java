package com.example.mobilesoftwareproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DiaryFragment extends Fragment {

    private String[] mealList = {"아침", "점심", "저녁"};
    private TextView selectedMealTextView; // 멤버 변수로 선언

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        ListView diaryListView = view.findViewById(R.id.diaryListView);
        selectedMealTextView = view.findViewById(R.id.selectedMealTextView);
        CalendarView calendarView = view.findViewById(R.id.calendarView); // CalendarView 객체 참조

        // ListView에 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, mealList);
        diaryListView.setAdapter(adapter); // ListView에 어댑터 설정


        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMeal = mealList[position];

                // 선택된 날짜의 선택된 끼니에 해당하는 데이터를 표시
                CalendarView calendarView = requireView().findViewById(R.id.calendarView);

                long selectedDateInMillis = calendarView.getDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = dateFormat.format(new Date(selectedDateInMillis));

                // 데이터베이스에서 정보 가져오기
                try {
                    Cursor cursor;
                    if (selectedMeal != null) {
                        // 선택된 날짜와 끼니(type)에 해당하는 데이터 조회
                        cursor = getActivity().getContentResolver().query(
                                MyContentProvider.CONTENT_URI,
                                null,
                                MyContentProvider.TIME + " = ? AND " + MyContentProvider.TYPE + " = ?",
                                new String[]{selectedDate, selectedMeal},
                                null
                        );
                    } else {
                        // 선택된 날짜에 해당하는 모든 끼니의 데이터 조회
                        cursor = getActivity().getContentResolver().query(
                                MyContentProvider.CONTENT_URI,
                                null,
                                MyContentProvider.TIME + " = ?",
                                new String[]{selectedDate},
                                null
                        );
                    }

                    // 조회된 데이터 처리
                    if (cursor != null && cursor.moveToFirst()) {
                        String foodName = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FOOD_NAME));
                        String cost = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.COST));

                        // 가져온 데이터를 UI에 표시
                        displaySelectedMealAndDate(selectedMeal, selectedDate);
                        displayFoodInformation(foodName, cost);
                    } else {
                        // 데이터가 없는 경우
                        selectedMealTextView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜에 따라 식사를 표시, 관련 작업 수행
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                // 데이터베이스에서 정보 가져오기
                retrieveDataFromDatabase(selectedDate, null); // type은 null로 전달하여 전체 끼니에 대한 정보를 가져옴
            }
        });

        return view;
    }

    // 데이터베이스에서 정보를 가져와 처리하는 메서드
    private void retrieveDataFromDatabase(String selectedDate, String selectedMeal) {
        // ContentProvider를 통해 데이터 조회
        Cursor cursor;
        if (selectedMeal != null) {
            // 선택된 날짜와 끼니(type)에 해당하는 데이터 조회
            cursor = getActivity().getContentResolver().query(
                    MyContentProvider.CONTENT_URI,
                    null,
                    MyContentProvider.TIME + " = ? AND " + MyContentProvider.TYPE + " = ?",
                    new String[]{selectedDate, selectedMeal},
                    null
            );
        } else {
            // 선택된 날짜에 해당하는 모든 끼니의 데이터 조회
            cursor = getActivity().getContentResolver().query(
                    MyContentProvider.CONTENT_URI,
                    null,
                    MyContentProvider.TIME + " = ?",
                    new String[]{selectedDate},
                    null
            );
        }

        // 조회된 데이터 처리
        if (cursor != null && cursor.moveToFirst()) {
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FOOD_NAME));
            String cost = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.COST));

            // TODO: 가져온 데이터를 활용하여 UI에 표시
            // 예시로 토스트 메시지로 출력
            String displayText = "Food: " + foodName +  "\nCost: " + cost;
            // 가져온 데이터를 UI에 표시
            displaySelectedMealAndDate(selectedMeal, selectedDate);
            displayFoodInformation(foodName, cost);
        } else {
            // 데이터가 없는 경우
            selectedMealTextView.setVisibility(View.GONE);
        }
    }
    // 선택된 끼니와 날짜를 표시하는 메서드
    private void displaySelectedMealAndDate(String selectedMeal, String selectedDate) {
        selectedMealTextView.setText(selectedMeal);
        selectedMealTextView.setVisibility(View.VISIBLE);
    }
    // 음식 정보를 표시하는 메서드
    private void displayFoodInformation(String foodName, String cost) {
        TextView foodInformationTextView = requireView().findViewById(R.id.foodInformationTextView);

        // 가져온 데이터를 UI에 표시
        String displayText = "Food: " + foodName + "\nCost: " + cost;
        foodInformationTextView.setText(displayText);
        foodInformationTextView.setVisibility(View.VISIBLE);
    }
}