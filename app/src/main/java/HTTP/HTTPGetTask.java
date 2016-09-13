package HTTP;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Contracts.ICallback;

/**
 * Created by Viktor on 5.9.2016 г..
 */


public class HTTPGetTask extends AsyncTask<HTTPData, Void, String> {
    private ICallback callBack;

    @Override
    protected String doInBackground(HTTPData... params) {
// if (ConnectivityUtility.isNetworkConnected(this)) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        boolean isServerError = false;
        HTTPData data = params[0];
        String query = null;
        byte[] postDataBytes = null;

        try {
            if (data.Body != null) {
                query = HTTPUtility.getQuery(data.Body);
                postDataBytes = query.getBytes("UTF-8");
            }
            URL url = new URL(HTTPUtility.BaseURL + data.Url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(data.Type);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (data.Body != null) {
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            }
            if(data.Headers != null) {
                for (Pair<String, String> header : data.Headers) {
                    urlConnection.setRequestProperty(header.first, header.second);
                }
            }

            if(data.Body != null) {
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postDataBytes);
            }

            InputStream stream = null;
            try {
                stream = urlConnection.getInputStream();
            } catch (IOException exception) {
                Log.e("PlaceholderFragment", "Error ", exception);
                isServerError = true;
                stream = urlConnection.getErrorStream();
            }
            StringBuffer buffer = new StringBuffer();
            if (stream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + '\n');
            }

            response = buffer.toString();
        } catch (Exception e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        try {
            if (!isServerError) {
                callBack.execute(response);
            } else {
                callBack.error(response);

            }

            return response;
        } catch (Exception ex) {
            Log.e("t", "Error", ex);
        }
        //   } else {
        //TODO: show error;
        // }

        return null;
    }

    public HTTPGetTask(ICallback callback) {
        this.callBack = callback;
    }
}
