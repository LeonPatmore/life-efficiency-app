package com.leon.patmore.life.efficiency.views.todo

import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.WeeklyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TodoWeeklyViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ViewManager() {
    var sharedPreferences: SharedPreferences? = null

    override fun onActive() {
        val currentFilter = getCurrentSetFilter()

        val weeklyItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getWeekly(currentFilter)
                }
            }

        val filterText = view!!.findViewById<AutoCompleteTextView>(R.id.TodoWeeklyFilterText)
        filterText.setText(currentFilter, false)

        val listAdapter =
            object : ArrayAdapter<WeeklyItem>(context!!, R.layout.list_item_todo_weekly_item, weeklyItems) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view = convertView ?: inflater.inflate(R.layout.list_item_todo_weekly_item, parent, false)
                    val weeklyItem = this.getItem(position)!!
                    val textField: TextView = view.findViewById(R.id.textField)
                    textField.text = weeklyItem.toString()
                    val completeButton = view.findViewById<Button>(R.id.completeButton)
                    completeButton.setOnClickListener {
                        try {
                            runBlocking {
                                launch {
                                    withContext(Dispatchers.Default) {
                                        lifeEfficiencyClient.completeWeeklyItem(weeklyItem.id)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        onActive()
                    }
                    textField.setBackgroundColor(getBackgroundColour(weeklyItem))
                    return view
                }
            }
        view!!.findViewById<ListView>(R.id.TodoWeeklyList).adapter = listAdapter

        view!!.findViewById<Button>(R.id.TodoWeeklyFilterButton).setOnClickListener {
            sharedPreferences!!.edit().putString("SET_FILTER", filterText.text.toString()).commit()
            onActive()
        }
    }

    private fun getCurrentSetFilter(): String? = sharedPreferences!!.getString("SET_FILTER", "")

    private fun getBackgroundColour(weeklyItem: WeeklyItem): Int =
        if (weeklyItem.complete) {
            Color.valueOf(0f, 0.7f, 0f, 0.3f).toArgb()
        } else {
            Color.valueOf(0.7f, 0f, 0f, 0.3f).toArgb()
        }
}
