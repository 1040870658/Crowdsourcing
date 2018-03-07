package hk.hku.yechen.crowdsourcing.util;

import okhttp3.Response;

/**
 * Created by yechen on 2017/12/26.
 */

public interface Extractor {
    public void  extract(Response response,int messageCode);
}
