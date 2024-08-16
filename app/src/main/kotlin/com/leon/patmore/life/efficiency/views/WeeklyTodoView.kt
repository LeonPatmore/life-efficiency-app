package com.leon.patmore.life.efficiency.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.WeeklyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class WeeklyTodoView(
    view: View,
    button: Button,
    private val listView: ListView,
    private val context: Context,
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ActiveView(view, button) {
    override fun onActive() {
        val weeklyItems =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getWeekly()
                }
            }

        val listAdapter =
            object : ArrayAdapter<WeeklyItem>(context, R.layout.weekly_todo_item, weeklyItems) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view = convertView ?: inflater.inflate(R.layout.weekly_todo_item, parent, false)
                    val weeklyItem = this.getItem(position)!!
                    val textField = view.findViewById(R.id.textField) as TextView
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
        listView.adapter = listAdapter
    }

    private fun getBackgroundColour(weeklyItem: WeeklyItem): Int =
        if (weeklyItem.complete) {
            Color.valueOf(0f, 0.7f, 0f, 0.3f).toArgb()
        } else {
            Color.valueOf(0.7f, 0f, 0f, 0.3f).toArgb()
        }
}
