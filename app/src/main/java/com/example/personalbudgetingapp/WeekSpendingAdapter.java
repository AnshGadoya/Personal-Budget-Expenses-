package com.example.personalbudgetingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekSpendingAdapter extends RecyclerView.Adapter<WeekSpendingAdapter.viewHolder>{

    private Context mContext;
    private List<Data> mydatalist;

    public WeekSpendingAdapter(Context mContext, List<Data> mydatalist) {
        this.mContext = mContext;
        this.mydatalist = mydatalist;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,parent,false);
        return new WeekSpendingAdapter.viewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Data data = mydatalist.get(position);

        holder.item.setText("Item: "+data.getItem());
        holder.amount.setText("Amount: "+data.getAmount());
        holder.date.setText("Date: "+data.getDate());
        holder.notes.setText("Notes: "+data.getNotes());

        switch (data.getItem()) {
            case "Transport":
                holder.imageView.setImageResource(R.drawable.ic_transport);
                break;

            case "Food":
                holder.imageView.setImageResource(R.drawable.ic_food);
                break;

            case "House":
                holder.imageView.setImageResource(R.drawable.ic_house);
                break;

            case "Entertainment":
                holder.imageView.setImageResource(R.drawable.ic_entertainment);
                break;

            case "Education":
                holder.imageView.setImageResource(R.drawable.ic_education);
                break;

            case "Charity":
                holder.imageView.setImageResource(R.drawable.ic_consultancy);
                break;

            case "Apparel":
                holder.imageView.setImageResource(R.drawable.ic_shirt);
                break;

            case "Health":
                holder.imageView.setImageResource(R.drawable.ic_health);
                break;

            case "Personal":
                holder.imageView.setImageResource(R.drawable.ic_personalcare);
                break;

            case "Other":
                holder.imageView.setImageResource(R.drawable.ic_other);
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mydatalist.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public TextView item,amount,date,notes;
        public ImageView imageView;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            item=itemView.findViewById(R.id.item);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
            notes=itemView.findViewById(R.id.note);
            imageView=itemView.findViewById(R.id.imageview);

        }
    }
}
