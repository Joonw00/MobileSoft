package com.example.mobilesoftwareproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private ListView listView;
    private CursorAdapter cursorAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ListView and CursorAdapter
        listView = view.findViewById(R.id.listView);
        cursorAdapter = createCursorAdapter();
        listView.setAdapter(cursorAdapter);

        // Load data from the database
        loadDataFromProvider();

        return view;
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

    private CursorAdapter createCursorAdapter() {
        // Define the columns to retrieve from the database
        String[] columns = {
                MyContentProvider.LOCATION,
                MyContentProvider.FOOD_NAME,
                MyContentProvider.BEVERAGE_NAME,
                MyContentProvider.IMPRESSIONS,
                MyContentProvider.TIME,
                MyContentProvider.COST
        };

        // Define the XML views to bind the data to
        int[] to = {
                R.id.textViewLocation,
                R.id.textViewFoodName,
                R.id.textViewBeverageName,
                R.id.textViewImpressions,
                R.id.textViewTime,
                R.id.textViewCost
        };

        // Create a new CursorAdapter
        return new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_layout,
                null,
                columns,
                to,
                0
        );
    }
}
