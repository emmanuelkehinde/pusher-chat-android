package com.kehinde.pusher_chat_test.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kehinde.pusher_chat_test.R;
import com.kehinde.pusher_chat_test.model.Message;
import com.kehinde.pusher_chat_test.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kehinde on 9/24/17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private Context context;
    private ArrayList<Message> messageList;
    private String m_username;

    public ChatRecyclerAdapter(Context context, ArrayList<Message> messageList, String m_username) {
        this.context = context;
        this.messageList = messageList;
        this.m_username = m_username;
    }

    class  ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_username) TextView txt_username;
        @BindView(R.id.txt_chat_message) TextView txt_chat_message;
        @BindView(R.id.img_profile_pic) ImageView img_profile_pic;

        public ChatViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_chat_message,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Message message=messageList.get(position);

        holder.txt_chat_message.setText(message.getMessage());
        if (message.getUsername().equals(m_username))
            holder.txt_username.setText("You");
        else
            holder.txt_username.setText(message.getUsername());

        ImageUtils.loadImageFromDrawable(context,holder.img_profile_pic,R.drawable.pusher);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message){
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void setMessages(ArrayList<Message> list){
        if (list!=null) messageList=list;
        notifyDataSetChanged();
    }

    public ArrayList<Message> getMessageList() {
        return messageList;
    }
}
