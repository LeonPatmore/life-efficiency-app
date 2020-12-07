package com.example.life.efficiency.client;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LifeEfficiencyClientHttp implements LifeEfficiencyClient {

    private static final String POST_METHOD = "POST";

    private static final String TAG = "LifeEfficiencyClient";

    private static final String SHOPPING_PATH = "shopping";
    private static final String SHOPPING_TODAY_PATH = "today";
    private static final String SHOPPING_HISTORY_PATH = "history";
    private static final String SHOPPING_LIST_PATH = "list";
    private static final String SHOPPING_ITEMS_PATH = "items";
    private static final String SHOPPING_REPEATING_PATH = "repeating";

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
    public List<String> getTodayItems() throws LifeEfficiencyException {
        Log.i(TAG, "Getting today's items!");

        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_TODAY_PATH))
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));
            JSONObject jsonObject = new JSONObject(resBody);
            return jsonArrayToList(jsonObject.getJSONArray("items"));
        } catch (IOException | JSONException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            stringArrayList.add(jsonArray.getString(i));
        }
        return stringArrayList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void acceptTodayItems() throws LifeEfficiencyException {
        Log.i(TAG, "Accepting today's items!");

        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_TODAY_PATH))
                    .method(POST_METHOD, RequestBody.create(
                            "",
                            MediaType.get("text/plain")))
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addPurchase(String name, int quantity) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Adding a purchase with name [ %s ] and quantity [ %d ]",
                name,
                quantity));

        @SuppressLint("DefaultLocale") String body =
                String.format("{\"item\": \"%s\", \"quantity\": \"%d\"}", name, quantity);
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_HISTORY_PATH))
                    .method(POST_METHOD, requestBody)
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));

            if (response.code() != 200)
                throw new LifeEfficiencyException("Unexpected response code from endpoint!");
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addToList(String name, int quantity) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Adding item to list with name [ %s ] and quantity [ %d ]",
                name,
                quantity));

        @SuppressLint("DefaultLocale") String body =
                String.format("{\"item\": \"%s\", \"quantity\": \"%d\"}", name, quantity);
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_LIST_PATH))
                    .method(POST_METHOD, requestBody)
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));

            if (response.code() != 200)
                throw new LifeEfficiencyException("Unexpected response code from endpoint!");
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void completeItems(List<String> items) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Completing items [ %s ]", String.join(",", items)));

        String jsonList = "[\"" + String.join("\",\"", items) + "\"]";
        Log.i(TAG, "json list is " + jsonList);

        @SuppressLint("DefaultLocale") String body = String.format("{\"items\": %s}", jsonList);
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_ITEMS_PATH))
                    .method(POST_METHOD, requestBody)
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));

            if (response.code() != 200)
                throw new LifeEfficiencyException("Unexpected response code from endpoint!");
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<String> getRepeatingItems() throws LifeEfficiencyException {
        Log.i(TAG, "Getting repeated items!");

        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_REPEATING_PATH))
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));
            JSONObject jsonObject = new JSONObject(resBody);
            return jsonArrayToList(jsonObject.getJSONArray("items"));
        } catch (IOException | JSONException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addRepeatingItem(String item) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Adding repeating items [ %s ]", item));

        @SuppressLint("DefaultLocale") String body = String.format("{\"item\": \"%s\"}", item);
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_REPEATING_PATH))
                    .method(POST_METHOD, requestBody)
                    .build();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new LifeEfficiencyException("Problem constructing HTTP request!", e);
        }

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));

            if (response.code() != 200)
                throw new LifeEfficiencyException("Unexpected response code from endpoint!");
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

}
