package com.leon.patmore.life.efficiency.client

import android.annotation.SuppressLint
import android.util.Log
import com.leon.patmore.life.efficiency.client.domain.LifeEfficiencyException
import com.leon.patmore.life.efficiency.client.domain.ListItem
import com.leon.patmore.life.efficiency.client.domain.TodoItem
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.stream.Collectors
import java.util.stream.IntStream


class LifeEfficiencyClientHttp(private val endpoint: URL, private val client: OkHttpClient) : LifeEfficiencyClient {

    private val TAG = javaClass.name

    @Throws(URISyntaxException::class, MalformedURLException::class)
    private fun getAbsoluteEndpoint(vararg pathComponents: String): URL {
        val currentPath = endpoint.path
        val endPath = java.lang.String.join("/", *pathComponents)
        val finalPath = if (currentPath.endsWith("/")) currentPath + endPath else "$currentPath/$endPath"
        return URI(endpoint.protocol, endpoint.host, finalPath, null)
                .toURL()
    }

    override fun getListItems(): List<ListItem> {
        Log.i(TAG, "Getting list items!")
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_LIST_PATH))
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                val jsonObject = JSONObject(resBody)
                return jsonArrayToListOfItems(jsonObject.getJSONArray("items"))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        } catch (e: JSONException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun getTodayItems(): List<String> {
        Log.i(TAG, "Getting today's items!")
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_TODAY_PATH))
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                val jsonObject = JSONObject(resBody)
                return jsonArrayToList(jsonObject.getJSONArray("items"))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        } catch (e: JSONException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun acceptTodayItems() {
        Log.i(TAG, "Accepting today's items!")
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_TODAY_PATH))
                    .method(POST_METHOD, null)
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun addPurchase(name: String, quantity: Int) {
        Log.i(TAG, String.format("Adding a purchase with name [ %s ] and quantity [ %d ]",
                name,
                quantity))
        @SuppressLint("DefaultLocale") val body = String.format("{\"name\": \"%s\", \"quantity\": \"%d\"}", name, quantity)
        val requestBody: RequestBody = body.toRequestBody()
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_HISTORY_PATH))
                    .method(POST_METHOD, requestBody)
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        client.newCall(request).execute().use { validateResponse(it) }
    }

    override fun addToList(name: String, quantity: Int) {
        Log.i(TAG, String.format("Adding item to list with name [ %s ] and quantity [ %d ]",
                name,
                quantity))
        @SuppressLint("DefaultLocale") val body = String.format("{\"name\": \"%s\", \"quantity\": \"%d\"}", name, quantity)
        val requestBody: RequestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_LIST_PATH))
                    .method(POST_METHOD, requestBody)
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                if (response.code != 200) throw LifeEfficiencyException("Unexpected response code from endpoint!")
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun completeItems(items: List<String>) {
        Log.i(TAG, String.format("Completing items [ %s ]", java.lang.String.join(",", items)))
        val jsonList = "[\"" + java.lang.String.join("\",\"", items) + "\"]"
        Log.i(TAG, "json list is $jsonList")
        @SuppressLint("DefaultLocale") val body = String.format("{\"items\": %s}", jsonList)
        val requestBody: RequestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_ITEMS_PATH))
                    .method(POST_METHOD, requestBody)
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                if (response.code != 200) throw LifeEfficiencyException("Unexpected response code from endpoint!")
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun getRepeatingItems(): List<String> {
        Log.i(TAG, "Getting repeated items!")
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_REPEATING_PATH))
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                val jsonObject = JSONObject(resBody)
                return jsonArrayToList(jsonObject.getJSONArray("items"))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        } catch (e: JSONException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun addRepeatingItem(item: String) {
        Log.i(TAG, String.format("Adding repeating items [ %s ]", item))
        @SuppressLint("DefaultLocale") val body = String.format("{\"item\": \"%s\"}", item)
        val requestBody: RequestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request: Request = try {
            Request.Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_REPEATING_PATH))
                    .method(POST_METHOD, requestBody)
                    .build()
        } catch (e: URISyntaxException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        } catch (e: MalformedURLException) {
            throw LifeEfficiencyException("Problem constructing HTTP request!", e)
        }
        client.newCall(request).execute().use { validateResponse(it) }
    }

    override fun deleteListItem(name: String, quantity: Int) {
        Log.i(TAG, String.format("Deleting list item [ %s ] with quantity [ %s ]", name, quantity))
        val request: Request
        val httpUrl = endpoint.toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_LIST_PATH)
                .addQueryParameter("name", name)
                .addQueryParameter("quantity", quantity.toString())
                .build()
        Log.i(TAG, String.format("Delete list item [ %s ]", httpUrl.toUrl()))
        request = Request.Builder()
                .url(httpUrl)
                .method("DELETE", null)
                .build()
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                if (response.code != 200) throw LifeEfficiencyException(String.format("Unexpected response code %s from endpoint!", response.code))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun completeItem(name: String, quantity: Int) {
        Log.i(TAG, String.format("Completing item [ %s ] with quantity [ %s ]", name, quantity))
        val request: Request
        val httpUrl = endpoint.toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_TODAY_PATH)
                .addQueryParameter("name", name)
                .addQueryParameter("quantity", quantity.toString())
                .build()
        Log.i(TAG, String.format("Complete item [ %s ]", httpUrl.toUrl()))
        request = Request.Builder()
                .url(httpUrl)
                .method("DELETE", null)
                .build()
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(TAG, String.format("Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody))
                if (response.code != 200) throw LifeEfficiencyException(String.format("Unexpected response code %s from endpoint!", response.code))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun getHistory(): List<ListItem> {
        val httpUrl = endpoint.toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_HISTORY_PATH)
                .build()

        client.newCall(Request.Builder().url(httpUrl).get().build())
                .execute()
                .use {
                    val jsonObject = JSONObject(it.body!!.string())
                    return jsonArrayToListOfItems(jsonObject.getJSONArray("purchases"))
                }
    }

    override fun todoList(): List<TodoItem> {
        val httpUrl = endpoint.toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("non_completed")
                .build()
        return client.newCall(Request.Builder().url(httpUrl).get().build())
                .execute()
                .use { jsonArrayToTodoItemList(JSONArray(it.body!!.string())) }
    }

    override fun updateTodoItemStatus(id: Int, status: String) {
        Log.i(TAG, "Updating todo item id [ %d ] to status [ %s ]".format(id, status))

        val httpUrl = endpoint.toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("list")
                .build()
        val body = String.format("{\"id\": %d, \"status\": \"%s\"}", id, status)
        val res = client.newCall(Request.Builder()
                .patch(body.toRequestBody(JSON_MEDIA_TYPE))
                .url(httpUrl).build())
                .execute()
        validateResponse(res)
    }

    override fun addTodo(desc: String) {
        Log.i(TAG, "Adding todo with desc [ {$desc ]")

        val httpUrl = endpoint.toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("list")
                .build()
        val body = "{\"desc\": \"$desc\"}"
        val res = client.newCall(Request.Builder()
                .post(body.toRequestBody(JSON_MEDIA_TYPE))
                .url(httpUrl).build())
                .execute()
        validateResponse(res)
    }

    private fun validateResponse(res: Response) {
        val body = res.body?.string()
        Log.d(TAG, "Response code [ ${res.code} ] with body [ $body ]")
        if (res.code != 200) {
            Log.w(TAG, "Unexpected response code [ ${res.code} ] from endpoint, body [ $body ]")
            throw LifeEfficiencyException("Unexpected response code [ {} ] from endpoint!")
        }
    }

    companion object {
        private const val POST_METHOD = "POST"
        private const val SHOPPING_PATH = "shopping"
        private const val SHOPPING_TODAY_PATH = "today"
        private const val SHOPPING_HISTORY_PATH = "history"
        private const val SHOPPING_LIST_PATH = "list"
        private const val SHOPPING_ITEMS_PATH = "items"
        private const val SHOPPING_REPEATING_PATH = "repeating"
        private const val TODO_PATH = "todo"
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()

        private fun jsonArrayToListOfItems(jsonArray: JSONArray): List<ListItem> {
            return IntStream.range(0, jsonArray.length())
                    .mapToObj { i: Int ->
                        try {
                            return@mapToObj jsonArray.getJSONObject(i)
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                    }
                    .map { item: JSONObject ->
                        try {
                            return@map ListItem(item.getString("name"), item.getInt("quantity"))
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                    }
                    .collect(Collectors.toList())
        }

        @Throws(JSONException::class)
        private fun jsonArrayToList(jsonArray: JSONArray): List<String> {
            val stringArrayList = ArrayList<String>()
            for (i in 0 until jsonArray.length()) {
                stringArrayList.add(jsonArray.getString(i))
            }
            return stringArrayList
        }

        private fun jsonArrayToTodoItemList(jsonArray: JSONArray): List<TodoItem> {
            return IntStream.range(0, jsonArray.length())
                    .mapToObj { i: Int ->
                        try {
                            return@mapToObj jsonArray.getJSONObject(i)
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                    }
                    .map { item: JSONObject ->
                        try {
                            return@map TodoItem(item.getInt("id"),
                                    item.getString("desc"),
                                    item.getString("status"),
                                    item.getString("date_added"))
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                    }
                    .collect(Collectors.toList())
        }

    }

}
