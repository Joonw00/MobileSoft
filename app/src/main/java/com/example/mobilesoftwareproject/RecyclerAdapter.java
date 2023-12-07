package com.example.mobilesoftwareproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<FoodData> foodDataArrayList;

    public RecyclerAdapter(ArrayList<FoodData> foodDataArrayList) {
        this.foodDataArrayList = foodDataArrayList;
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
                // 삭제 버튼을 클릭했을 때의 동작 구현
                // 예를 들어, 해당 아이템을 리스트에서 제거하고 RecyclerView 갱신 등을 수행할 수 있습니다.
                //deleteItem(position);
            }
        });

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
