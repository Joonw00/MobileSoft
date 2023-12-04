package com.example.mobilesoftwareproject;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DiaryFragment extends Fragment {

    private String[] mealList = {"아침", "점심", "저녁"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        ListView diaryListView = view.findViewById(R.id.diaryListView);
        final TextView selectedMealTextView = view.findViewById(R.id.selectedMealTextView);
        CalendarView calendarView = view.findViewById(R.id.calendarView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, mealList);
        diaryListView.setAdapter(adapter);

        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMeal = mealList[position];

                // 선택된 날짜의 선택된 끼니의 식사를 표시

                selectedMealTextView.setText(selectedMeal);
                selectedMealTextView.setVisibility(View.VISIBLE);
            }
        });

        // CalendarView의 날짜 변경 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜에 따라 식사를 표시, 관련 작업 수행
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                Toast.makeText(requireContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}