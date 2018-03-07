package hk.hku.yechen.crowdsourcing.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import hk.hku.yechen.crowdsourcing.LoginActivity;
import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.model.UserModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yechen on 2018/2/28.
 */

public class LoginHandler extends Handler {
    public LoginHandler(LoginActivity loginActivity){
        this.context = new WeakReference<LoginActivity>(loginActivity);
    }
    private WeakReference<LoginActivity> context;

    @Override
    public void handleMessage(Message msg) {
        LoginActivity loginActivity = context.get();
        switch (msg.what){
            case NetworkPresenter.LOGIN_SUCCESS:
                if(loginActivity == null)
                    return;
                loginActivity.showProgress(false);
                MainActivity.userModel = new UserModel((UserModel)msg.obj);
                SharedPreferences sharedPreferences = loginActivity.getSharedPreferences("user",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone",MainActivity.userModel.getPhone());
                editor.putString("password",MainActivity.userModel.getPassword());
                editor.commit();
                loginActivity.showProgress(true);
                Intent intent = new Intent(loginActivity,MainActivity.class);
                loginActivity.startActivity(intent);
                loginActivity.finish();
                break;
            case NetworkPresenter.LOGIN_FAIL:
                loginActivity.showProgress(false);
                Toast.makeText(loginActivity,"Incorrect Password",Toast.LENGTH_SHORT).show();
                break;
            case NetworkPresenter.H_FAIL:
                loginActivity.showProgress(false);
                Toast.makeText(loginActivity,"Network error",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
