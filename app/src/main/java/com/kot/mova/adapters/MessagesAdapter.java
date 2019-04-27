package com.kot.mova.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kot.mova.R;
import com.kot.mova.model.ViewMessage;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private ArrayList<ViewMessage> mMessages;
    final private OnListItemClickListener mOnListItemClickListener;

    public MessagesAdapter(ArrayList<ViewMessage> messages, OnListItemClickListener listener){
        mMessages = messages;
        mOnListItemClickListener = listener;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.message.setText(mMessages.get(position).getMessage());
        viewHolder.time.setText(mMessages.get(position).getTime());
        viewHolder.distance.setText(mMessages.get(position).getDistance());
        viewHolder.name.setText(mMessages.get(position).getName());
    }

    public int getItemCount() {
        return mMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView time;
        TextView distance;
        TextView message;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userNameTextView);
            time = itemView.findViewById(R.id.timeTextView);
            distance = itemView.findViewById(R.id.distanceTextView);
            message = itemView.findViewById(R.id.messageTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}