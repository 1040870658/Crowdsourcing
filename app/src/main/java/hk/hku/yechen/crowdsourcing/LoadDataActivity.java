package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import hk.hku.yechen.crowdsourcing.fragments.FragmentMain;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/1/24.
 */

public class LoadDataActivity extends Activity {
    private Handler handler;
    private NetworkPresenter networkPresenter;
    private ReadFile readFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case FragmentMain.ORIGIN_REVERSE_CODE:break;
                    case 0:
                         networkPresenter = new NetworkPresenter
                            (FragmentMain.ORIGIN_REVERSE_CODE,NetworkPresenter.UrlBuilder.buildRGEO(
                                    (String)msg.obj)
                                    , null, handler, ResponseExtractor.BuildReverseGeoExtractor(handler));

                }
            }
        };
        readFile = new ReadFile(handler);
        new Thread(readFile).start();
    }
    private class ReadFile implements Runnable{
        private Handler handler;

        public ReadFile(Handler handler){
            this.handler = handler;
        }
        @Override
        public void run() {
            String inputLine;
            InputStream is = getResources().openRawResource(R.raw.extract);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                while( (inputLine = reader.readLine()) != null){
                    LevelLog.log(LevelLog.ERROR,"file",inputLine);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
