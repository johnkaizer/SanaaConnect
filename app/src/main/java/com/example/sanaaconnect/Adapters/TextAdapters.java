package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.MessageModel;

import java.util.List;

public class TextAdapters extends RecyclerView.Adapter<TextAdapters.ViewHolder> {
    Context context;
    List<MessageModel>list;

    public TextAdapters(Context context, List<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TextAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull TextAdapters.ViewHolder holder, int position) {
        MessageModel messageModel = list.get(position);
        holder.nameTxt.setText(messageModel.getUserName());
        holder.timeTxt.setText(messageModel.getTimeStamp());
        holder.contentTxt.setText(messageModel.getContent());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView timeTxt;
        TextView contentTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            timeTxt = itemView.findViewById(R.id.sms_time);
            contentTxt = itemView.findViewById(R.id.content_txt);

        }
    }
}
