package hk.hku.yechen.crowdsourcing.network;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by yechen on 2017/11/20.
 */

public interface Network {
    public Response syncRequest(String url) throws IOException;
    public void asynRequest(String url,NetworkEngine.Listener listener);
}
