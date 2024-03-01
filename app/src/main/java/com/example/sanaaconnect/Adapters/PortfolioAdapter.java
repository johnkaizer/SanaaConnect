package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.Portfolio;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private List<Portfolio> portfolioList;
    private Context context;

    public PortfolioAdapter(Context context, List<Portfolio> portfolioList) {
        this.context = context;
        this.portfolioList = portfolioList;
    }

    @NonNull
    @Override
    public PortfolioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioAdapter.ViewHolder holder, int position) {
        Portfolio portfolio = portfolioList.get(position);
        if (portfolio.getTimeStamp() != null) {
            // Convert the timestamp to a Date object
            Date date = new Date(portfolio.getTimeStamp());

            // Create a SimpleDateFormat instance with your desired format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

            // Format the date object into a human-readable string
            String formattedDate = simpleDateFormat.format(date);

            // Set the formatted date string to your TextView
            holder.timesStamp.setText("last updated"+ formattedDate);
        } else {
            // Handle case where timestamp is null, if possible
            holder.timesStamp.setText("Date not available");
        }

        String imageUrl = portfolio.getImageUrl();
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
        return portfolioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView timesStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.roundedImage);
            timesStamp = itemView.findViewById(R.id.sms_time);
            // Initialize other views in the item layout if needed
        }
    }
}
