package hk.hku.yechen.crowdsourcing.network;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yechen on 2017/11/20.
 */

public class NetworkEngine implements Network{
    private OkHttpClient okHttpClient;


    public Response syncRequest(String url)throws IOException{
        Request request = new Request.Builder().url(url).build();
        return okHttpClient.newCall(request).execute();
    }
    @Override
    public void asynRequest(String url,Listener listener) {
        Request request = new Request.Builder()
                .url(url).build();
        okHttpClient.newCall(request).enqueue(listener);
    }
    public void addParam(){
    }
    public void asynRequest(String url, FormBody formBody, Listener listener){
        if (null == formBody){
            asynRequest(url,listener);
            return;
        }
        Request request = new Request.Builder()
                .url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(listener);
    }
    private static class NetworkHolder{
        private static final NetworkEngine networkEngine = new NetworkEngine();
    }
    public static NetworkEngine getInstance(){
        return NetworkHolder.networkEngine;
    }
    private NetworkEngine(){
        this.okHttpClient = new OkHttpClient();
    };
    public interface Listener extends Callback{

    }
}
