package com.kehinde.pusher_chat_test.ui;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kehinde.pusher_chat_test.R;
import com.kehinde.pusher_chat_test.adapter.ChatRecyclerAdapter;
import com.kehinde.pusher_chat_test.model.Message;
import com.kehinde.pusher_chat_test.model.response.ServerResponse;
import com.kehinde.pusher_chat_test.network.APIService;
import com.kehinde.pusher_chat_test.utils.Constants;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatRoomActivity extends AppCompatActivity {

    @BindView(R.id.edt_chat_message) EditText edt_chat_message;
    @BindView(R.id.fab_send_message) FloatingActionButton fab_send_message;
    @BindView(R.id.chat_recycler_view) RecyclerView chat_recycler_view;
    @BindView(R.id.progress) ProgressBar progress;

    private String chat_room_name;
    private String username;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<Message> messageList=new ArrayList<>();
    private String LIST = "list";
    private Channel channel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

        if (getIntent()!=null) {
            chat_room_name = getIntent().getStringExtra(Constants.CHAT_ROOM_NAME_EXTRA);
            username = getIntent().getStringExtra(Constants.USER_NAME_EXTRA);
        }
        if (getSupportActionBar()!=null && chat_room_name!=null) {
            getSupportActionBar().setTitle(chat_room_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if (savedInstanceState!=null) messageList=savedInstanceState.getParcelableArrayList(LIST);

        chatRecyclerAdapter=new ChatRecyclerAdapter(this,messageList,username);
        chat_recycler_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        chat_recycler_view.setItemAnimator(new DefaultItemAnimator());
        chat_recycler_view.setAdapter(chatRecyclerAdapter);

        edt_chat_message.addTextChangedListener(textWatcher);

        //Pusher Connection
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("[PUSHER_API_KEY]", options);

        channel = pusher.subscribe(chat_room_name);
        channel.bind("new_message", subscriptionEventListener);

        pusher.connect();
    }

    SubscriptionEventListener subscriptionEventListener=new SubscriptionEventListener() {
        @Override
        public void onEvent(String channelName, String eventName, final String data) {
            Gson gson=new Gson();
            final Message message = gson.fromJson(data,Message.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMessageAdded(message);
                }
            });
        }
    };

    @OnClick(R.id.fab_send_message)
    void fabSendMessageClicked(){
        String message=edt_chat_message.getText().toString();
        if (!TextUtils.isEmpty(message)) {

            sendMessage(message);
        }
    }

    private void sendMessage(final String message) {
        showProgress();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        APIService apiService= builder.build().create(APIService.class);
        Call<ServerResponse> call=apiService.sendMessage(chat_room_name,
                new Message(message,username));
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body()!=null){
                    if (response.body().getSuccess()!=null) {
                        hideProgress();
                        edt_chat_message.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                hideProgress();
            }
        });

    }

    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
        fab_send_message.setVisibility(View.INVISIBLE);
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
        fab_send_message.setVisibility(View.VISIBLE);
    }

    public void showMessageAdded(Message message){
        chatRecyclerAdapter.addMessage(message);
        chat_recycler_view.scrollToPosition((chatRecyclerAdapter.getItemCount()-1));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST,chatRecyclerAdapter.getMessageList());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                fab_send_message.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
            }
            else {
                fab_send_message.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (channel.isSubscribed())channel.unbind("new_message",subscriptionEventListener);
    }
}
