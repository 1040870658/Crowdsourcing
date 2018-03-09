package hk.hku.yechen.crowdsourcing.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.model.UserModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yechen on 2018/3/8.
 */

public class UserHandler extends Handler {
    private MainActivity context;
    public UserHandler(MainActivity context){
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case NetworkPresenter.LOGIN_SUCCESS:
                MainActivity.userModel = new UserModel((UserModel) msg.obj);
                SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone", MainActivity.userModel.getPhone());
                editor.putString("password", MainActivity.userModel.getPassword());
                editor.commit();
                context.setUserInfo();
                break;
        }
    }
}
