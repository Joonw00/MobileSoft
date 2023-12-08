package com.example.mobilesoftwareproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<FoodData> foodDataArrayList;
    private Context context;


    public RecyclerAdapter(Context context,ArrayList<FoodData> foodDataArrayList) {
        this.foodDataArrayList = foodDataArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position)
    {
        // 현재 위치의 FoodData 객체 가져오기
        FoodData currentFoodData = foodDataArrayList.get(position);
        // 뷰홀더의 각 뷰에 데이터 설정
        holder.textViewFoodName.setText("음식 이름 :" + currentFoodData.getFoodName());
        holder.textViewImpressions.setText("소감 :" + currentFoodData.getImpression());
        holder.textViewCost.setText("가격 :"+currentFoodData.getCost());
        holder.textViewLocation.setText("위치 :"+currentFoodData.getLocation());
        holder.textViewCalorie.setText("칼로리 : "+currentFoodData.getCalorie());

        byte[] imageByteArray = currentFoodData.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

        holder.imageViewFood.setImageBitmap(bitmap);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteItem(position);
            }
        });

    }
    public void deleteItem(int position) {
        // 선택된 위치의 아이템을 삭제
        int id = getid(position);
        int rowsDeleted = context.getContentResolver().delete(
                MyContentProvider.CONTENT_URI,
                MyContentProvider.ID + "=?",
                new String[]{String.valueOf(id)}
        );

        // 삭제가 성공적으로 이루어졌을 경우에만 RecyclerView 갱신
        if (rowsDeleted > 0) {
            // RecyclerView에서도 아이템 삭제
            foodDataArrayList.remove(position);
            Toast.makeText(context, "식사 기록 삭제 성공", Toast.LENGTH_LONG).show();
        }
        // RecyclerView에 데이터가 변경되었음을 알림
        notifyItemRemoved(position);
    }
    public int getid(int position) {
        // 특정 위치(position)의 아이템의 ID를 반환
        return foodDataArrayList.get(position).getID();
    }

    @Override
    public int getItemCount()
    {
        return foodDataArrayList.size();
    }
    public void setData(ArrayList<FoodData> foodDataArrayList)
    {
        this.foodDataArrayList = foodDataArrayList;
        notifyDataSetChanged();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewFoodName;
        TextView textViewImpressions;
        TextView textViewCost;
        TextView textViewLocation;
        TextView textViewCalorie;
        ImageButton deleteButton;
        ImageView imageViewFood;

        public ViewHolder(View itemView){
            super(itemView);
            textViewFoodName =itemView.findViewById(R.id.textViewFoodName);
            textViewImpressions = itemView.findViewById(R.id.textViewImpressions);
            textViewCost = itemView.findViewById(R.id.textViewCost);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewCalorie = itemView.findViewById(R.id.textViewCalorie);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);

        }
    }
}
