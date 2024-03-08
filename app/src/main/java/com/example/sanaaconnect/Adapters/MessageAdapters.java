package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.MessageModel;

import java.util.List;

public class MessageAdapters extends RecyclerView.Adapter<MessageAdapters.ViewHolder> {
    Context context;
    List<MessageModel>list;

    public MessageAdapters(Context context, List<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapters.ViewHolder holder, int position) {
        MessageModel messageModel = list.get(position);
        holder.nameTxt.setText(messageModel.getUserName());
        holder.timeTxt.setText(messageModel.getTimeStamp());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView timeTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            timeTxt = itemView.findViewById(R.id.sms_time);

        }
    }
}
