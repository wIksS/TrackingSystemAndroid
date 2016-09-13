package HTTP;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;

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


public class HTTPGetTaskAuthorized extends AsyncTask<Pair<String, String>, Void, String> {
    private ICallback callBack;

    public HTTPGetTaskAuthorized(ICallback callback) {
        this.callBack = callback;
    }

    @Override
    protected String doInBackground(Pair<String, String>... params) {
// if (ConnectivityUtility.isNetworkConnected(this)) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        boolean isServerError = false;

        try {
            URL url = new URL(HTTPUtility.BaseURL + params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream stream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (stream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + '\n');
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            response = buffer.toString();
        } catch (IOException e) {
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

        if (!isServerError) {
            try {
                callBack.execute(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            callBack.error(response);

        }
        //   } else {
        //TODO: show error;
        // }

        return null;
    }
}
