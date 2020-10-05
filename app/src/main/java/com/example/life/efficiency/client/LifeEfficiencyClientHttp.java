package com.example.life.efficiency.client;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LifeEfficiencyClientHttp implements LifeEfficiencyClient {

    private static final String SHOPPING_PATH = "shopping";
    private static final String SHOPPING_TODAY_PATH = "today";

    private final URL endpoint;
    private final OkHttpClient client;

    public LifeEfficiencyClientHttp(URL endpoint, OkHttpClient client){
        this.endpoint = endpoint;
        this.client = client;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private URL getAbsoluteEndpoint(String... pathComponents) throws
            URISyntaxException,
            MalformedURLException {
        String currentPath = endpoint.getPath();
        String endPath = String.join("/", pathComponents);
        String finalPath = currentPath.endsWith("/") ?
                currentPath + endPath :
                currentPath + "/" + endPath;
        return new URI(endpoint.getProtocol(), endpoint.getHost(), finalPath, null)
                .toURL();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<String> getTodayItems() {
        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_TODAY_PATH))
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (Response response = client.newCall(request).execute()) {
            String body = Objects.requireNonNull(response.body()).string();
            System.out.println("BODY " + body);
            JSONObject jsonObject = new JSONObject(body);
            return jsonArrayToList(jsonObject.getJSONArray("items"));
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            stringArrayList.add(jsonArray.getString(i));
        }
        return stringArrayList;
    }

    @Override
    public void acceptTodayItems() {

    }

}
