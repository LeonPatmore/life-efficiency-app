package com.leon.patmore.life.efficiency.client

import android.annotation.SuppressLint
import android.util.Log
import com.leon.patmore.life.efficiency.client.Utils.toIntIgnoreNull
import com.leon.patmore.life.efficiency.client.domain.Goal
import com.leon.patmore.life.efficiency.client.domain.HistoryItem
import com.leon.patmore.life.efficiency.client.domain.LifeEfficiencyException
import com.leon.patmore.life.efficiency.client.domain.ListItem
import com.leon.patmore.life.efficiency.client.domain.RepeatingItem
import com.leon.patmore.life.efficiency.client.domain.TodoItem
import com.leon.patmore.life.efficiency.client.domain.WeeklyItem
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

class LifeEfficiencyClientHttp(
    private val endpoint: URL,
    private val client: OkHttpClient,
) : LifeEfficiencyClient {
    private val tag = javaClass.name

    @Throws(URISyntaxException::class, MalformedURLException::class)
    private fun getAbsoluteEndpoint(vararg pathComponents: String): URL {
        val currentPath = endpoint.path
        val endPath = java.lang.String.join("/", *pathComponents)
        val finalPath = if (currentPath.endsWith("/")) currentPath + endPath else "$currentPath/$endPath"
        return URI(endpoint.protocol, endpoint.host, finalPath, null)
            .toURL()
    }

    override fun getListItems(): List<ListItem> {
        Log.i(tag, "Getting list items!")
        val request: Request =
            try {
                Request
                    .Builder()
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
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
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
        Log.i(tag, "Getting today's items!")
        val request: Request =
            try {
                Request
                    .Builder()
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
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                val jsonObject = JSONObject(resBody)
                return jsonArrayToList(jsonObject.getJSONArray("items"))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        } catch (e: JSONException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun addPurchase(
        name: String,
        quantity: Int,
    ) {
        Log.i(
            tag,
            String.format(
                "Adding a purchase with name [ %s ] and quantity [ %d ]",
                name,
                quantity,
            ),
        )
        @SuppressLint("DefaultLocale")
        val body = String.format("{\"name\": \"%s\", \"quantity\": \"%d\"}", name, quantity)
        val requestBody: RequestBody = body.toRequestBody()
        val request: Request =
            try {
                Request
                    .Builder()
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

    override fun addToList(
        name: String,
        quantity: Int,
    ) {
        Log.i(
            tag,
            String.format(
                "Adding item to list with name [ %s ] and quantity [ %d ]",
                name,
                quantity,
            ),
        )
        @SuppressLint("DefaultLocale")
        val body = String.format("{\"name\": \"%s\", \"quantity\": \"%d\"}", name, quantity)
        val requestBody: RequestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request: Request =
            try {
                Request
                    .Builder()
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
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                if (response.code != 200) throw LifeEfficiencyException("Unexpected response code from endpoint!")
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun completeItems(items: List<String>) {
        Log.i(tag, String.format("Completing items [ %s ]", java.lang.String.join(",", items)))
        val jsonList = "[\"" + java.lang.String.join("\",\"", items) + "\"]"
        Log.i(tag, "json list is $jsonList")
        @SuppressLint("DefaultLocale")
        val body = String.format("{\"items\": %s}", jsonList)
        val requestBody: RequestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request: Request =
            try {
                Request
                    .Builder()
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
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                if (response.code != 200) throw LifeEfficiencyException("Unexpected response code from endpoint!")
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun getRepeatingItems(): List<String> {
        Log.i(tag, "Getting repeated items!")
        val request: Request =
            try {
                Request
                    .Builder()
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
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                val jsonObject = JSONObject(resBody)
                return jsonArrayToList(jsonObject.getJSONArray("items"))
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        } catch (e: JSONException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun getRepeatingItemsDetails(): Map<String, RepeatingItem> {
        Log.i(tag, "Getting repeated items details!")
        val request: Request =
            try {
                Request
                    .Builder()
                    .url(getAbsoluteEndpoint(SHOPPING_PATH, SHOPPING_REPEATING_DETAILS_PATH))
                    .build()
            } catch (e: URISyntaxException) {
                throw LifeEfficiencyException("Problem constructing HTTP request!", e)
            } catch (e: MalformedURLException) {
                throw LifeEfficiencyException("Problem constructing HTTP request!", e)
            }
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                val jsonObject = JSONObject(resBody)
                return jsonObjectToMapOfRepeatingItems(jsonObject)
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        } catch (e: JSONException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun addRepeatingItem(item: String) {
        Log.i(tag, String.format("Adding repeating items [ %s ]", item))
        @SuppressLint("DefaultLocale")
        val body = String.format("{\"item\": \"%s\"}", item)
        val requestBody: RequestBody = body.toRequestBody(JSON_MEDIA_TYPE)
        val request: Request =
            try {
                Request
                    .Builder()
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

    override fun deleteListItem(
        name: String,
        quantity: Int,
    ) {
        Log.i(tag, String.format("Deleting list item [ %s ] with quantity [ %s ]", name, quantity))
        val request: Request
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_LIST_PATH)
                .addQueryParameter("name", name)
                .addQueryParameter("quantity", quantity.toString())
                .build()
        Log.i(tag, String.format("Delete list item [ %s ]", httpUrl.toUrl()))
        request =
            Request
                .Builder()
                .url(httpUrl)
                .method("DELETE", null)
                .build()
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                if (response.code !=
                    200
                ) {
                    throw LifeEfficiencyException(String.format("Unexpected response code %s from endpoint!", response.code))
                }
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun completeItem(
        name: String,
        quantity: Int,
    ) {
        Log.i(tag, String.format("Completing item [ %s ] with quantity [ %s ]", name, quantity))
        val request: Request
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_TODAY_PATH)
                .addQueryParameter("name", name)
                .addQueryParameter("quantity", quantity.toString())
                .build()
        Log.i(tag, String.format("Complete item [ %s ]", httpUrl.toUrl()))
        request =
            Request
                .Builder()
                .url(httpUrl)
                .method("DELETE", null)
                .build()
        try {
            client.newCall(request).execute().use { response ->
                val resBody = response.body!!.string()
                Log.d(
                    tag,
                    String.format(
                        "Response code [ %d ] with body [ %s ]",
                        response.code,
                        resBody,
                    ),
                )
                if (response.code !=
                    200
                ) {
                    throw LifeEfficiencyException(String.format("Unexpected response code %s from endpoint!", response.code))
                }
            }
        } catch (e: IOException) {
            throw LifeEfficiencyException("Problem during HTTP call!", e)
        }
    }

    override fun getHistory(): List<HistoryItem> {
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(SHOPPING_PATH)
                .addPathSegment(SHOPPING_HISTORY_PATH)
                .build()

        client
            .newCall(
                Request
                    .Builder()
                    .url(httpUrl)
                    .get()
                    .build(),
            ).execute()
            .use {
                val jsonObject = JSONObject(it.body!!.string())
                return jsonArrayToListOfHistoryItems(jsonObject.getJSONArray("purchases"))
            }
    }

    override fun todoNonCompleted(): List<TodoItem> {
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("non_completed")
                .build()
        return client
            .newCall(
                Request
                    .Builder()
                    .url(httpUrl)
                    .get()
                    .build(),
            ).execute()
            .use { jsonArrayToTodoItemList(JSONArray(it.body!!.string())) }
    }

    override fun todoList(
        status: String?,
        sorted: Boolean,
    ): List<TodoItem> {
        val httpUrlBuilder =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("list")
        status?.also { httpUrlBuilder.addQueryParameter("status", status) }
        status?.also { httpUrlBuilder.addQueryParameter("sort", sorted.toString()) }
        return client
            .newCall(
                Request
                    .Builder()
                    .url(httpUrlBuilder.build())
                    .get()
                    .build(),
            ).execute()
            .use { jsonArrayToTodoItemList(JSONArray(it.body!!.string())) }
    }

    override fun updateTodoItemStatus(
        id: String,
        status: String,
    ) {
        Log.i(tag, "Updating todo item id [ $id ] to status [ $status ]")

        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("list")
                .build()
        val body = "{\"id\": \"$id\", \"status\": \"$status\"}"
        val res =
            client
                .newCall(
                    Request
                        .Builder()
                        .patch(body.toRequestBody(JSON_MEDIA_TYPE))
                        .url(httpUrl)
                        .build(),
                ).execute()
        validateResponse(res)
    }

    override fun addTodo(desc: String) {
        Log.i(tag, "Adding todo with desc [ {$desc ]")

        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment("list")
                .build()
        val body = "{\"desc\": \"$desc\"}"
        val res =
            client
                .newCall(
                    Request
                        .Builder()
                        .post(body.toRequestBody(JSON_MEDIA_TYPE))
                        .url(httpUrl)
                        .build(),
                ).execute()
        validateResponse(res)
    }

    override fun getWeekly(setId: String?): List<WeeklyItem> {
        Log.i(tag, "Getting weekly with set ID filter $setId")
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment(WEEKLY_PATH)
        if (!setId.isNullOrEmpty()) httpUrl.addQueryParameter("set_id", setId)
        return client
            .newCall(
                Request
                    .Builder()
                    .get()
                    .url(httpUrl.build())
                    .build(),
            ).execute()
            .use { jsonArrayToListOfWeeklyItems(JSONArray(it.body!!.string())) }
    }

    override fun completeWeeklyItem(id: Int) {
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(TODO_PATH)
                .addPathSegment(WEEKLY_PATH)
                .addQueryParameter("id", id.toString())
                .build()
        val res =
            client
                .newCall(
                    Request
                        .Builder()
                        .method(POST_METHOD, "".toRequestBody())
                        .url(httpUrl)
                        .build(),
                ).execute()
        validateResponse(res)
    }

    override fun getGoals(): Map<String, Map<String, List<Goal>>> {
        val httpUrl =
            endpoint
                .toHttpUrlOrNull()!!
                .newBuilder()
                .addPathSegment(GOALS_PATH)
                .addPathSegment("list")
                .build()
        return client
            .newCall(
                Request
                    .Builder()
                    .get()
                    .url(httpUrl)
                    .build(),
            ).execute()
            .use { JSONObject(it.body!!.string()) }
            .let {
                val goalMap = mutableMapOf<String, Map<String, List<Goal>>>()
                for (year in it.keys()) {
                    val yearObject = it.getJSONObject(year)
                    val yearMap = mutableMapOf<String, List<Goal>>()
                    for (quarter in yearObject.keys()) {
                        val goalArrayList = yearObject.getJSONArray(quarter)
                        val goalList = mutableListOf<Goal>()
                        repeat(goalArrayList.length()) { index ->
                            val goalObject = goalArrayList.getJSONObject(index)
                            goalList.add(
                                Goal(
                                    goalObject.getString("name"),
                                    goalObject.getString("progress"),
                                ),
                            )
                        }
                        yearMap[quarter] = goalList
                    }
                    goalMap[year] = yearMap
                }
                goalMap
            }
    }

    private fun validateResponse(res: Response) {
        val body = res.body?.string()
        Log.d(tag, "Response code [ ${res.code} ] with body [ $body ]")
        if (res.code != 200) {
            Log.w(tag, "Unexpected response code [ ${res.code} ] from endpoint, body [ $body ]")
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
        private const val SHOPPING_REPEATING_DETAILS_PATH = "repeating-details"
        private const val TODO_PATH = "todo"
        private const val WEEKLY_PATH = "weekly"
        private const val GOALS_PATH = "goals"
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()

        private fun jsonArrayToListOfWeeklyItems(jsonArray: JSONArray): List<WeeklyItem> {
            return IntStream
                .range(0, jsonArray.length())
                .mapToObj { i: Int ->
                    try {
                        return@mapToObj jsonArray.getJSONObject(i)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.map { item: JSONObject ->
                    try {
                        return@map WeeklyItem(
                            item.getInt("id"),
                            item.getInt("day"),
                            item.getString("desc"),
                            item.getBoolean("complete"),
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.collect(Collectors.toList())
        }

        private fun jsonArrayToListOfItems(jsonArray: JSONArray): List<ListItem> {
            return IntStream
                .range(0, jsonArray.length())
                .mapToObj { i: Int ->
                    try {
                        return@mapToObj jsonArray.getJSONObject(i)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.map { item: JSONObject ->
                    try {
                        return@map ListItem(item.getString("name"), item.getInt("quantity"))
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.collect(Collectors.toList())
        }

        private fun jsonArrayToListOfHistoryItems(jsonArray: JSONArray): List<HistoryItem> {
            return IntStream
                .range(0, jsonArray.length())
                .mapToObj { i: Int ->
                    try {
                        return@mapToObj jsonArray.getJSONObject(i)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.map { item: JSONObject ->
                    try {
                        return@map HistoryItem(
                            item.getString("name"),
                            item.getInt("quantity"),
                            item.getString("date"),
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.collect(Collectors.toList())
        }

        @Throws(JSONException::class)
        private fun jsonArrayToList(jsonArray: JSONArray): List<String> {
            val stringArrayList = ArrayList<String>()
            for (i in 0 until jsonArray.length()) {
                stringArrayList.add(jsonArray.getString(i))
            }
            return stringArrayList
        }

        private fun jsonObjectToMapOfRepeatingItems(jsonObject: JSONObject): Map<String, RepeatingItem> {
            val finalMap = mutableMapOf<String, RepeatingItem>()
            jsonObject.keys().forEachRemaining {
                val thisItem = jsonObject.getJSONObject(it)
                finalMap[it] =
                    RepeatingItem(
                        thisItem.getString("avg_gap_days").toIntIgnoreNull(),
                        thisItem.getString("time_since_last_bought").toIntIgnoreNull(),
                    )
            }
            return finalMap
        }

        private fun jsonArrayToTodoItemList(jsonArray: JSONArray): List<TodoItem> {
            return IntStream
                .range(0, jsonArray.length())
                .mapToObj { i: Int ->
                    try {
                        return@mapToObj jsonArray.getJSONObject(i)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.map { item: JSONObject ->
                    try {
                        return@map TodoItem(
                            item.getString("id"),
                            item.getString("desc"),
                            item.getString("status"),
                            item.getString("date_added"),
                            item.getString("date_done"),
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }.collect(Collectors.toList())
        }
    }
}
