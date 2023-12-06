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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
                String recordDate = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TIME));

                // 날짜를 지난 달의 날짜와 비교
                if (isDateInLastMonth(recordDate)) {
                    int calorie = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.CALORIE));
                    totalCalories += calorie;

                    int cost = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COST));
                    totalCost += cost;
                }
            }
            cursor.close();
        }

        totalCaloriesTextView.setText("최근 한달간 칼료리 총량 : " + totalCalories + "kcal");
        totalCostTextView.setText("최근 한달간 식사 총 비용 : " + totalCost + "원");
    }
    private boolean isDateInLastMonth(String date) {
        // 날짜 문자열을 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        Date recordDate;
        try {
            recordDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        // 달력 인스턴스를 가져오고 오늘로 설정
        Calendar todayCal = Calendar.getInstance();

        // 오늘로부터 30일 이전의 날짜를 가져오기
        todayCal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgoDate = todayCal.getTime();

        // 오늘로부터 30일 이전에 해당하는 모든 날짜를 저장하는 리스트
        String[] dateArray = new String[30];
        for (int i = 0; i < 30; i++) {
            dateArray[i] = dateFormat.format(todayCal.getTime());
            todayCal.add(Calendar.DAY_OF_MONTH, 1);
        }


        // 레코드 날짜가 오늘로부터 30일 이전에 속하는지 확인
        boolean flag = false;  // flag를 true로 초기화
        for (String dateStr : dateArray) {
            Date date1;

            // 레코드 날짜가 오늘로부터 30일 이전에 속하는지 확인
            if (date.equals(dateStr)) {
                flag = true;
                break;
            }
        }

        return flag;
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
                String recordDate = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TIME));
                String mealType = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TYPE));
                int cost = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COST));

                // 날짜를 지난 달의 날짜와 비교
                if (isDateInLastMonth(recordDate)) {
                    // 이미 해당 식사 타입에 대한 금액이 맵에 추가되어 있다면 더하기, 없다면 새로 추가
                    if (mealCostMap.containsKey(mealType)) {
                        mealCostMap.put(mealType, mealCostMap.get(mealType) + cost);
                    } else {
                        mealCostMap.put(mealType, cost);
                    }
                }

            }

            // LinkedHashMap을 사용하여 정렬된 맵 생성
            Map<String, Integer> sortedMealCostMap = new LinkedHashMap<>();
            mealCostMap.entrySet().stream()
                    .sorted((entry1, entry2) -> {
                        // 아침, 음료, 저녁, 점심 순으로 정렬
                        List<String> order = Arrays.asList("아침", "점심", "저녁", "음료");
                        return Integer.compare(order.indexOf(entry1.getKey()), order.indexOf(entry2.getKey()));
                    })
                    .forEachOrdered(entry -> sortedMealCostMap.put(entry.getKey(), entry.getValue()));

            // 아래 부분을 추가하여 선택된 날짜에 해당하는 모든 데이터를 가져와서 ListView에 표시
            List<String> analysisDataList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : sortedMealCostMap.entrySet()) {
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
