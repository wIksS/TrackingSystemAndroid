package Contracts;

import org.json.JSONException;

/**
 * Created by Viktor on 4.9.2016 г..
 */
public interface ICallback {
    public void execute(String result) throws JSONException;

    public void error(String error);
}
