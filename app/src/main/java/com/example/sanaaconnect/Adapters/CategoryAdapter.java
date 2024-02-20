package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.CategoryModel;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CategoryModel> listCat;
    private OnItemClickListener itemClickListener;
    private int selectedItem = -1;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> listCat, OnItemClickListener listener) {
        this.context = context;
        this.listCat = listCat;
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(CategoryModel category);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView text;
        private CardView cardView1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.categoryTxt);
            cardView1 = itemView.findViewById(R.id.cardView_cat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CategoryModel selectedCategory = listCat.get(position);
                itemClickListener.onItemClick(selectedCategory);
                setSelectedItem(position);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel category = listCat.get(position);
        holder.text.setText(category.getHeader());

        if (position == selectedItem) {
            holder.cardView1.setBackgroundResource(R.drawable.selected_category_background);
        } else {
            holder.cardView1.setBackgroundResource(R.drawable.default_category_background);
        }
    }

    @Override
    public int getItemCount() {
        return listCat.size();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    public int getSelectedItem() {
        return selectedItem;
    }
}
