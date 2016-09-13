package HTTP;

import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Viktor on 4.9.2016 г..
 */
public class HTTPUtility {
    public static String BaseURL = "http://192.168.8.101:8888/";//"http://192.168.1.118:8888/";

    public static String getQuery(Pair<String, String> params[]) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (int i = 0; i < params.length; i++) {
            if (i != 0)
                result.append("&");

            result.append(URLEncoder.encode(params[i].first.toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params[i].second.toString(), "UTF-8"));
        }

        return result.toString();
    }
}
