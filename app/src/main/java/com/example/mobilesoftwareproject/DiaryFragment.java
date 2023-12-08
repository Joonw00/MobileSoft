package com.example.mobilesoftwareproject;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DiaryFragment extends Fragment implements View.OnClickListener {

    private String[] mealList = {};
    private TextView selectedMealTextView; // 멤버 변수로 선언
    private int toastCalories = 0;

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
                String selectedMeal = (String) parent.getItemAtPosition(position);

                // Get the selected date from the CalendarView in the desired format
                long selectedDateMillis = calendarView.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = sdf.format(new Date(selectedDateMillis));

                // Retrieve data from the ListView item's text (assuming the format is "(XXXkcal)")
                String itemText = (String) parent.getItemAtPosition(position);

                // Find the position of the opening parenthesis
                int indexOfOpeningParenthesis = itemText.indexOf('(');

                if (indexOfOpeningParenthesis != -1) {
                    // Extract the substring from the opening parenthesis onward
                    String trimmedText = itemText.substring(indexOfOpeningParenthesis);

                    // Extract the calorie value using the regular expression
                    String calorieString = trimmedText.replaceAll("[^0-9]", ""); // Remove all non-numeric characters

                    if (!calorieString.isEmpty()) {
                        int calorie = Integer.parseInt(calorieString);
                        toastCalories += calorie;

                        Toast.makeText(getActivity(), "Total Calories : " + toastCalories + "kcal", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error parsing calorie data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Opening parenthesis not found", Toast.LENGTH_SHORT).show();
                }
            }
        });




        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜에 따라 식사를 표시, 관련 작업 수행
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                // 데이터베이스에서 정보 가져오기
                retrieveDataFromDatabase(selectedDate, null);

                // 아래 부분을 추가하여 선택된 날짜에 해당하는 모든 데이터를 가져와서 ListView에 표시
                try {
                    Cursor cursor = getActivity().getContentResolver().query(
                            MyContentProvider.CONTENT_URI,
                            null,
                            MyContentProvider.TIME + " = ?",
                            new String[]{selectedDate},
                            null
                    );

                    if (cursor != null) {
                        List<String> mealDataList = new ArrayList<>();
                        int totalCalories = 0;

                        while (cursor.moveToNext()) {
                            int calorie = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.CALORIE));
                            String mealType = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TYPE));
                            String foodName = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FOOD_NAME));
                            String cost = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.COST));
                            String location = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.LOCATION));

                            // 데이터를 문자열로 만들어 리스트에 추가
                            String mealData = String.format("%s \t %s : %s (%s kcal)",location , mealType, foodName, calorie);

                            totalCalories += calorie;
                            mealDataList.add(mealData);
                        }
                        selectedMealTextView.setText("Today Calories: " + totalCalories + "kcal");

                        Cursor monthlyCursor = getActivity().getContentResolver().query(
                                MyContentProvider.CONTENT_URI,
                                null,
                                MyContentProvider.TIME + " LIKE ?",
                                new String[]{selectedDate.substring(0, 7) + "%"},
                                null
                        );

                        int monthlyTotalCalories = 0;

                        if (monthlyCursor != null) {
                            while (monthlyCursor.moveToNext()) {
                                int monthlyCalorie = monthlyCursor.getInt(monthlyCursor.getColumnIndexOrThrow(MyContentProvider.CALORIE));
                                monthlyTotalCalories += monthlyCalorie;
                            }
                            monthlyCursor.close();
                        }
                        selectedMealTextView.append("\nMonth Total Calories: " + monthlyTotalCalories + "kcal");

                        // 아침, 점심, 저녁 별 칼로리 합계 출력
                        selectedMealTextView.setVisibility(View.VISIBLE);

                        // 정렬을 위해 Comparator를 사용하여 점심, 아침, 저녁 순으로 정렬
                        Collections.sort(mealDataList, new Comparator<String>() {
                            @Override
                            public int compare(String meal1, String meal2) {
                                int order1 = getMealOrder(meal1);
                                int order2 = getMealOrder(meal2);

                                Log.d("MealSort", meal1 + " Order: " + order1);
                                Log.d("MealSort", meal2 + " Order: " + order2);

                                return order1 - order2;
                            }

                            private int getMealOrder(String meal) {
                                // "위치 \t 식사유형 : 음식이름 (가격원)" 형태에서 식사유형 위치를 찾아서 비교
                                String[] parts = meal.split("\t");
                                if (parts.length >= 2) {
                                    String mealType = parts[1].split(":")[0].trim();
                                    switch (mealType) {
                                        case "아침":
                                            return 0;
                                        case "점심":
                                            return 1;
                                        case "저녁":
                                            return 2;
                                    }
                                }
                                return Integer.MAX_VALUE;
                            }
                        });


                        // ArrayAdapter를 업데이트하여 데이터를 ListView에 표시
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, mealDataList);
                        diaryListView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button resetButton = view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this); // Set the fragment as the click listener

        return view;
    }
    private void resetCalories() {
        toastCalories = 0;

        // Assuming you have a reference to your ListView
        ListView diaryListView = getView().findViewById(R.id.diaryListView);

        // Assuming you have a reference to your ArrayAdapter
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) diaryListView.getAdapter();

        // Clear the data in the adapter and notify the ListView
        adapter.clear();
        adapter.notifyDataSetChanged();

        // Hide the TextView displaying meal information
        selectedMealTextView.setVisibility(View.GONE);

        // Show a toast message indicating that calories have been reset
        Toast.makeText(getActivity(), "Calories Reset", Toast.LENGTH_SHORT).show();
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
        } else {
            // 데이터가 없는 경우
            selectedMealTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.resetButton) {
            // Handle Reset Calories button click
            resetCalories();
        }
    }
}