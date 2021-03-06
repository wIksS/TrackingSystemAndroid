//package HTTP;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.util.Pair;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import Contracts.ICallback;
//import Helpers.ErrorHandler;
//
///**
// * Created by Viktor on 5.9.2016 г..
// */
//
//
//public class HTTPPostTask extends AsyncTask<Pair<String, String>, Void, String> {
//    private ICallback callBack;
//
//    public HTTPPostTask(ICallback callback) {
//        this.callBack = callback;
//    }
//
//    @Override
//    protected String doInBackground(Pair<String, String>... params) {
//// if (ConnectivityUtility.isNetworkConnected(this)) {
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String response = null;
//        boolean isServerError = false;
//
//        try {
//            String query = HTTPUtility.getQuery(params, 1);
//            URL url = new URL(HTTPUtility.BaseURL + params[0].second);
//            byte[] postDataBytes = query.getBytes("UTF-8");
//
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//            urlConnection.setDoOutput(true);
//            urlConnection.getOutputStream().write(postDataBytes);
//
//            InputStream stream = null;
//            try {
//                stream = urlConnection.getInputStream();
//            } catch (IOException exception) {
//                Log.e("PlaceholderFragment", "Error ", exception);
//                isServerError = true;
//                stream = urlConnection.getErrorStream();
//            }
//
//            StringBuffer buffer = new StringBuffer();
//            if (stream == null) {
//                return null;
//            }
//
//            reader = new BufferedReader(new InputStreamReader(stream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line + '\n');
//            }
//
//            response = buffer.toString();
//        } catch (Exception e) {
//            Log.e("PlaceholderFragment", "Error ", e);
//            return null;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e("PlaceholderFragment", "Error closing stream", e);
//                }
//            }
//        }
//
//        try {
//            if (!isServerError) {
//                callBack.execute(response);
//            } else {
//                callBack.error(response);
//
//            }
//
//            return response;
//        } catch (Exception ex) {
//            Log.e("t", "Error", ex);
//            //TODO: show error;
//        }
//        //   } else {
//        //TODO: show error;
//        // }
//
//        return null;
//    }
//}
