package com.leon.patmore.life.efficiency.client;

import android.annotation.SuppressLint;
import android.util.Log;

import com.leon.patmore.life.efficiency.client.domain.ListItem;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LifeEfficiencyClientHttp implements LifeEfficiencyClient {

    private static final String POST_METHOD = "POST";

    private final String TAG = getClass().getName();

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

    @Override
    public List<ListItem> getListItems() throws LifeEfficiencyException {
        Log.i(TAG, "Getting list items!");

        Request request;
        try {
            request = new Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_LIST_PATH))
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
            return jsonArrayToListOfItems(jsonObject.getJSONArray("items"));
        } catch (IOException | JSONException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

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

    private static List<ListItem> jsonArrayToListOfItems(JSONArray jsonArray) {
        return IntStream.range(0, jsonArray.length())
                .mapToObj(i -> {
                    try {
                        return jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(item -> {
                    try {
                        return new ListItem(item.getString("name"), item.getInt("quantity"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            stringArrayList.add(jsonArray.getString(i));
        }
        return stringArrayList;
    }

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

    @Override
    public void addToList(String name, int quantity) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Adding item to list with name [ %s ] and quantity [ %d ]",
                name,
                quantity));

        @SuppressLint("DefaultLocale") String body =
                String.format("{\"name\": \"%s\", \"quantity\": \"%d\"}", name, quantity);
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

    @Override
    public void deleteListItem(String name, int quantity) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Deleting list item [ %s ] with quantity [ %s ]", name, quantity));

        Request request;
        HttpUrl httpUrl = HttpUrl
                .get(endpoint)
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_LIST_PATH)
                .addQueryParameter("name", name)
                .addQueryParameter("quantity", String.valueOf(quantity))
                .build();
        Log.i(TAG, String.format("Delete list item [ %s ]", httpUrl.url()));
        request = new Request.Builder()
                .url(httpUrl)
                .method("DELETE", null)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));

            if (response.code() != 200)
                throw new LifeEfficiencyException(String.format("Unexpected response code %s from endpoint!", response.code()));
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

    @Override
    public void completeItem(String name, int quantity) throws LifeEfficiencyException {
        Log.i(TAG, String.format("Completing item [ %s ] with quantity [ %s ]", name, quantity));

        Request request;
        HttpUrl httpUrl = HttpUrl
                .get(endpoint)
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_TODAY_PATH)
                .addQueryParameter("name", name)
                .addQueryParameter("quantity", String.valueOf(quantity))
                .build();
        Log.i(TAG, String.format("Complete item [ %s ]", httpUrl.url()));
        request = new Request.Builder()
                .url(httpUrl)
                .method("DELETE", null)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String resBody = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                    response.code(),
                    resBody));

            if (response.code() != 200)
                throw new LifeEfficiencyException(String.format("Unexpected response code %s from endpoint!", response.code()));
        } catch (IOException e) {
            throw new LifeEfficiencyException("Problem during HTTP call!", e);
        }
    }

}
