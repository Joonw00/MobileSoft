package com.example.mobilesoftwareproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


    public class HomeFragment extends Fragment {

        private RecyclerAdapter morningAdapter;
    private RecyclerAdapter lunchAdapter;
    private RecyclerAdapter dinnerAdapter;
    private RecyclerAdapter beverageAdapter;
    private RecyclerView morningRecyclerView;
    private RecyclerView lunchRecyclerView;
    private RecyclerView dinnerRecyclerView;
    private RecyclerView beverageRecyclerView;
    private ImageButton morningButton;
    private ImageButton lunchButton;
    private ImageButton dinnerButton;
    private ImageButton beverageButton;
    private ImageButton titleDateButton;
    private ImageButton deleteButton;
    private TextView textViewDate;
    private StringBuilder title;
        private static final String PREFS_NAME = "MyPrefsFile";
        private static final String LAST_SELECTED_DATE = "lastSelectedDate";


    public HomeFragment() {
        // Required empty public constructor
    }
        @Override
        public void onHiddenChanged(boolean hidden) {
            super.onHiddenChanged(hidden);

            if (!hidden) { // 프래그먼트가 다시 나타날 때
                // 여기에 리사이클러뷰 업데이트 코드 추가
                String lastSelectedDate = getLastSelectedDate();
                String today = getCurrentDate();
                String title;
                if(lastSelectedDate.equals(today)) {
                    title = lastSelectedDate + "(오늘)의 식단";
                }
                else
                {
                    title = lastSelectedDate + "의 식단";
                }
                textViewDate.setText(title);
                loadDataFromProvider(lastSelectedDate);
            }
        }
        private void saveLastSelectedDate(String date) {
            SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(LAST_SELECTED_DATE, date);
            editor.apply();
        }

        private String getLastSelectedDate() {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.getString(LAST_SELECTED_DATE, getCurrentDate());
        }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 레이아웃을 팽창시킴
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        morningRecyclerView = view.findViewById(R.id.morningRecyclerView);
        lunchRecyclerView = view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView = view.findViewById(R.id.dinnerRecyclerView);
        beverageRecyclerView = view.findViewById(R.id.beverageRecyclerView);

        // onCreateView 내부에서
        titleDateButton = view.findViewById(R.id.titleDateButton);
        titleDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogForData();
            }
        });
        morningButton = view.findViewById(R.id.morningButton);
        morningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecyclerViewVisibility(morningRecyclerView,morningButton);
            }
        });
        lunchButton = view.findViewById(R.id.lunchButton);
        lunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecyclerViewVisibility(lunchRecyclerView,lunchButton);
            }
        });
        dinnerButton = view.findViewById(R.id.dinnerButton);
        dinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecyclerViewVisibility(dinnerRecyclerView,dinnerButton);
            }
        });
        beverageButton = view.findViewById(R.id.beverageButton);
        beverageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecyclerViewVisibility(beverageRecyclerView,beverageButton);
            }
        });



        // 데이터베이스에서 데이터를 로드
        // TextView에 오늘의 날짜 표시
        textViewDate = view.findViewById(R.id.hometitle);
        String currentDate = getCurrentDate();
        String title = currentDate + "(오늘)의 식단";
        textViewDate.setText(title);
        loadDataFromProvider(currentDate);
        toggleRecyclerViewVisibility(morningRecyclerView,morningButton);
        toggleRecyclerViewVisibility(lunchRecyclerView,lunchButton);
        toggleRecyclerViewVisibility(dinnerRecyclerView,dinnerButton);
        toggleRecyclerViewVisibility(beverageRecyclerView,beverageButton);
        return view;
    }
    private void toggleRecyclerViewVisibility(RecyclerView recyclerView,ImageButton button) {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            // If visible, hide it and set the visibility to GONE
            recyclerView.setVisibility(View.GONE);
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } else {
            // If hidden, make it visible and set the visibility to VISIBLE
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private ArrayList<FoodData> getData(ArrayList<FoodData> foodData,Cursor cursor)
    {
        ArrayList<FoodData> foodDataArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.ID));
            String impression = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.IMPRESSIONS));
            int calorie = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.CALORIE));
            String mealType = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TYPE));
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FOOD_NAME));
            int cost = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COST));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.LOCATION));
            String photo = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.PHOTO));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.TIME));
            FoodData addData = new FoodData(id,foodName,impression,cost,location,calorie,photo,mealType,time);
            foodDataArrayList.add(addData);
        }
        cursor.close();
        return foodDataArrayList;
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
                        saveLastSelectedDate(selectedDate);
                    }
                },
                year, // 초기 년도 - 현재 년도 또는 기본값으로 설정
                month, // 초기 월 - 현재 월 또는 기본값으로 설정
                day // 초기 일 - 현재 일 또는 기본값으로 설정
        );

        // DatePickerDialog를 표시
        datePickerDialog.show();
    }




    // HomeFragment 클래스에
    private void deleteItem(long id, int position) {
        // ContentResolver를 사용하여 해당 ID에 해당하는 데이터를 삭제
        int rowsDeleted = getActivity().getContentResolver().delete(
                MyContentProvider.CONTENT_URI,
                MyContentProvider.ID + "=?",
                new String[]{String.valueOf(id)}
        );

        // 삭제가 성공적으로 이루어졌을 경우에만 RecyclerView 갱신
        if (rowsDeleted > 0) {
            // RecyclerView에서도 아이템 삭제
            morningAdapter.deleteItem(position);
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
        Cursor morningcursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " LIKE ? AND " + MyContentProvider.TYPE + " = ?",
                new String[]{selectedDate + "%", "아침"},  // Use the selected date as a prefix and filter by "아침"
                null
        );
        Cursor lunchcursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " LIKE ? AND " + MyContentProvider.TYPE + " = ?",
                new String[]{selectedDate + "%", "점심"},  // Use the selected date as a prefix and filter by "아침"
                null
        );
        Cursor dinnercursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " LIKE ? AND " + MyContentProvider.TYPE + " = ?",
                new String[]{selectedDate + "%", "저녁"},  // Use the selected date as a prefix and filter by "아침"
                null
        );Cursor beveragecursor = getActivity().getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                null,
                MyContentProvider.TIME + " LIKE ? AND " + MyContentProvider.TYPE + " = ?",
                new String[]{selectedDate + "%", "음료"},  // Use the selected date as a prefix and filter by "아침"
                null
        );

        ArrayList<FoodData> morningArrayList = new ArrayList<>();
        ArrayList<FoodData> lunchArrayList = new ArrayList<>();
        ArrayList<FoodData> dinnerArrayList = new ArrayList<>();
        ArrayList<FoodData> beverageArrayList = new ArrayList<>();

        // Load data into respective lists
        morningArrayList = getData(morningArrayList, morningcursor);
        lunchArrayList = getData(lunchArrayList, lunchcursor);
        dinnerArrayList = getData(dinnerArrayList, dinnercursor);
        beverageArrayList = getData(beverageArrayList, beveragecursor);
        // Adapter 생성
        morningAdapter = new RecyclerAdapter(requireContext(), morningArrayList);
        lunchAdapter = new RecyclerAdapter(requireContext(), lunchArrayList);
        dinnerAdapter = new RecyclerAdapter(requireContext(), dinnerArrayList);
        beverageAdapter = new RecyclerAdapter(requireContext(), beverageArrayList);

        morningRecyclerView.setAdapter(morningAdapter);
        lunchRecyclerView.setAdapter(lunchAdapter);
        dinnerRecyclerView.setAdapter(dinnerAdapter);
        beverageRecyclerView.setAdapter(beverageAdapter);

        morningAdapter.notifyDataSetChanged();
        lunchAdapter.notifyDataSetChanged();
        dinnerAdapter.notifyDataSetChanged();
        beverageAdapter.notifyDataSetChanged();

        morningRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        lunchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        beverageRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

    }


}

