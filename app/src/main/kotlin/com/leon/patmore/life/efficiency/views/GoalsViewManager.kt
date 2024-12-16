package com.leon.patmore.life.efficiency.views

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.Goal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GoalsViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ViewManager() {
    override fun onActive() {
        val goals = getGoalsAsList()
        val listAdapter =
            object : ArrayAdapter<Triple<String, String, Goal>>(context!!, R.layout.list_item_simple, goals) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view = convertView ?: inflater.inflate(R.layout.list_item_simple, parent, false)
                    val currentGoal = this.getItem(position)!!
                    val textField = view.findViewById(R.id.textField) as TextView
                    val text = "[ ${currentGoal.first} - ${currentGoal.second} ] : ${currentGoal.third.name}"
                    textField.text = text
                    textField.textSize = 20f
                    textField.setTextColor(Color.rgb(30, 30, 30))
                    when (currentGoal.third.progress) {
                        "done" -> {
                            view.setBackgroundColor(Color.argb(150, 100, 250, 100))
                        }
                        "not_started" -> {
                            view.setBackgroundColor(Color.argb(150, 250, 100, 100))
                        }
                        else -> {
                            view.setBackgroundColor(Color.argb(150, 235, 166, 46))
                        }
                    }
                    return view
                }
            }
        view!!.findViewById<ListView>(R.id.GoalsList).adapter = listAdapter
    }

    private fun getGoalsAsList(): List<Triple<String, String, Goal>> {
        val goals =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.getGoals()
                }
            }
        val finalGoals = mutableListOf<Triple<String, String, Goal>>()
        goals.forEach { (year, yearGoals) ->
            yearGoals.forEach { (quarter, quarterGoals) ->
                quarterGoals.forEach {
                    finalGoals.add(Triple(year, quarter, it))
                }
            }
        }
        return finalGoals.reversed()
    }
}
