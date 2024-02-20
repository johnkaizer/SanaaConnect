package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.ProfessionModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfessionAdapter extends RecyclerView.Adapter<ProfessionAdapter.ViewHolder> {
    List<ProfessionModel> professionList;

    public ProfessionAdapter(List<ProfessionModel> professionList) {
        this.professionList = professionList;
    }
    public void filterList(List<ProfessionModel> filteredList) {
        professionList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profession_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ProfessionAdapter.ViewHolder holder, int position) {
        ProfessionModel professionModel = professionList.get(position);
        // Set the values to TextViews
        holder.nameTxt.setText("Name:" + professionModel.getFullName());
        holder.proffesionTxt.setText("Description: " + professionModel.getTitle());
        holder.chargesTxt.setText("Ksh:" + professionModel.getCharges());
        holder.phoneTxt.setText("Phone: " + professionModel.getPhone());
        String imageUrl = professionModel.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.imageView);
        } else {
            // If imageUrl is empty or null, set a default placeholder
            holder.imageView.setImageResource(R.drawable.ic_logo);
        }

    }

    @Override
    public int getItemCount() {
        return professionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView proffesionTxt;
        TextView chargesTxt;
        TextView phoneTxt;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            proffesionTxt = itemView.findViewById(R.id.title_txt);
            chargesTxt = itemView.findViewById(R.id.charges_txt);
            phoneTxt = itemView.findViewById(R.id.phone_txt);
            imageView = itemView.findViewById(R.id.roundedImage);
        }
    }
}