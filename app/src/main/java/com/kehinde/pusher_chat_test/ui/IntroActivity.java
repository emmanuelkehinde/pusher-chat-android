package com.kehinde.pusher_chat_test.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kehinde.pusher_chat_test.R;
import com.kehinde.pusher_chat_test.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.edt_room_name) EditText edt_room_name;
    @BindView(R.id.edt_user_name) EditText edt_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_enter)
    void btnEnterClick(){
        String chat_room_name=edt_room_name.getText().toString();
        String user_name=edt_user_name.getText().toString();

        if (TextUtils.isEmpty(chat_room_name)){
            showToastMessage("Enter a chat room name");
            return;
        }

        if (TextUtils.isEmpty(user_name)){
            showToastMessage("Enter a username");
            return;
        }

        enterRoom(chat_room_name,user_name);
    }

    private void enterRoom(String chat_room_name, String user_name) {
        Intent intent=new Intent(this,ChatRoomActivity.class);
        intent.putExtra(Constants.CHAT_ROOM_NAME_EXTRA,chat_room_name);
        intent.putExtra(Constants.USER_NAME_EXTRA,user_name);
        startActivity(intent);
    }

    private void showToastMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
