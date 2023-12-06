package com.example.mobilesoftwareproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalysisFragment extends Fragment {

    private TextView todayDateTextView;
    private TextView totalCaloriesTextView;
    private TextView totalCostTextView;
    private ListView analysisListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        todayDateTextView = view.findViewById(R.id.todayDateTextView);
        totalCaloriesTextView = view.findViewById(R.id.totalCaloriesTextView);
        totalCostTextView = view.findViewById(R.id.totalCostTextView);
        analysisListView = view.findViewById(R.id.analysisListView);

        // 오늘의 날짜 표시
        showTodayDate();

        // 최근 1달 간의 식사에 대한 칼로리 총량 및 비용 총량 보여주기
        showTotalCaloriesAndCostForLastMonth();

        // 최근 1달 간의 식사 비용을 종류 별로 분석하여 보여주기
        analyzeMealCostForLastMonth();

        return view;
    }

    private void showTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        todayDateTextView.setText(todayDate);
    }

    private void showTotalCaloriesAndCostForLastMonth() {
        // 현재 날짜를 기준으로 최근 1달 간의 데이터를 가져오기
        String lastMonth = MyDateUtils.getLastMonthDateString();
        Cursor cursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " >= ?",
                new String[]{lastMonth},
                null
        );

        int totalCalories = 0;
        int totalCost = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int calorie = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.CALORIE));
                totalCalories += calorie;

                int cost = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COST));
                totalCost += cost;
            }
            cursor.close();
        }

        totalCaloriesTextView.setText("최근 한달간 칼료리 총량 : " + totalCalories + "kcal");
        totalCostTextView.setText("최근 한달간 식사 총 비용 : " + totalCost + "원");
    }

    private void analyzeMealCostForLastMonth() {
        String lastMonth = MyDateUtils.getLastMonthDateString();
        Cursor cursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " >= ?",
                new String[]{lastMonth},
                null
        );

        if (cursor != null) {
            Map<String, Integer> mealCostMap = new HashMap<>();

            while (cursor.moveToNext()) {
                String mealType = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TYPE));
                int cost = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COST));

                // 이미 해당 식사 타입에 대한 금액이 맵에 추가되어 있다면 더하기, 없다면 새로 추가
                if (mealCostMap.containsKey(mealType)) {
                    mealCostMap.put(mealType, mealCostMap.get(mealType) + cost);
                } else {
                    mealCostMap.put(mealType, cost);
                }
            }

            // 아래 부분을 추가하여 선택된 날짜에 해당하는 모든 데이터를 가져와서 ListView에 표시
            List<String> analysisDataList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : mealCostMap.entrySet()) {
                String analysisData = entry.getKey() + " : " + entry.getValue() + "원";
                analysisDataList.add(analysisData);
            }

            // ArrayAdapter를 업데이트하여 데이터를 ListView에 표시
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, analysisDataList);
            analysisListView.setAdapter(adapter);

            cursor.close();
        } else {
            Toast.makeText(getActivity(), "No data available for analysis.", Toast.LENGTH_SHORT).show();
        }
    }
}
