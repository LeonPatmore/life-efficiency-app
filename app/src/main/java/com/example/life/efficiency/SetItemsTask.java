package com.example.life.efficiency;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetItemsTask extends AsyncTask<Void, Void, String[]> {

    private OkHttpClient client = new OkHttpClient();

    private static String[] jsonArrayToStringArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            stringArrayList.add(jsonArray.getString(i));
        }
        return stringArrayList.toArray(new String[]{});
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String[] doInBackground(Void... voids) {
        String url = "https://tfv84iy2sf.execute-api.eu-west-1.amazonaws.com/Prod/shopping/today";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = Objects.requireNonNull(response.body()).string();
            System.out.println("BODY " + body);
            JSONObject jsonObject = new JSONObject(body);
            return jsonArrayToStringArray(jsonObject.getJSONArray("items"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
