package com.example.sanaaconnect.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    List<Users> usersList;

    public UsersAdapter(List<Users> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.userName.setText(users.getFullName());
        holder.pass.setText(users.getPassword());
        holder.phone.setText(users.getPhoneNumber());
        holder.email.setText(users.getEmail());
        holder.role.setText(users.getRole());

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView pass;
        TextView phone;
        TextView role;
        TextView email;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            pass = itemView.findViewById(R.id.password);
            phone = itemView.findViewById(R.id.phone);
            role = itemView.findViewById(R.id.business);
            email = itemView.findViewById(R.id.email);
        }
    }
}
