package hk.hku.yechen.crowdsourcing.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import hk.hku.yechen.crowdsourcing.LoginActivity;
import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.RegisterActivity;
import hk.hku.yechen.crowdsourcing.model.UserModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yechen on 2018/2/28.
 */

public class RegisterHandler extends Handler{
    public RegisterHandler(RegisterActivity registerActivity){
        this.context = new WeakReference<RegisterActivity>(registerActivity);
    }
    private WeakReference<RegisterActivity> context;

    @Override
    public void handleMessage(Message msg) {
        RegisterActivity registerActivity = context.get();
        switch (msg.what){
            case NetworkPresenter.REG_SUCCESS:
                if(registerActivity == null)
                    return;
                registerActivity.showProgress(false);
                MainActivity.userModel = new UserModel((UserModel)msg.obj);
                SharedPreferences sharedPreferences = registerActivity.getSharedPreferences("user",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone",MainActivity.userModel.getPhone());
                editor.putString("password",MainActivity.userModel.getPassword());
                editor.commit();
                registerActivity.showProgress(true);
                Intent intent = new Intent(registerActivity,MainActivity.class);
                registerActivity.startActivity(intent);
                registerActivity.finish();
                break;
            case NetworkPresenter.REG_FAIL:
                registerActivity.showProgress(false);
                Toast.makeText(registerActivity,"User Already Exists",Toast.LENGTH_SHORT).show();
                break;
            case NetworkPresenter.H_FAIL:
                registerActivity.showProgress(false);
                Toast.makeText(registerActivity,"Network error",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

