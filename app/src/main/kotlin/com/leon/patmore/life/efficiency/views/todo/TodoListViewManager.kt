package com.leon.patmore.life.efficiency.views.todo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.ViewManager
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TodoListViewManager(
    private val lifeEfficiencyClient: LifeEfficiencyClient,
) : ViewManager() {
    override fun onActive() {
        setupTodoList()
        setupAddTodoButton()
    }

    private fun setupTodoList() {
        val todoList =
            runBlocking {
                withContext(Dispatchers.Default) {
                    lifeEfficiencyClient.todoNonCompleted()
                }
            }
        val listAdapter =
            object : ArrayAdapter<TodoItem>(context!!, R.layout.list_item_todo_item, todoList) {
                val inflater = LayoutInflater.from(context)

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val view = convertView ?: inflater.inflate(R.layout.list_item_todo_item, parent, false)
                    val todoItem = this.getItem(position)!!
                    val textField: TextView = view.findViewById(R.id.textField)
                    val text = "${todoItem.status}: ${todoItem.desc}"
                    textField.text = text
                    val completeButton = view.findViewById<Button>(R.id.TodoItemCompleteButton)
                    completeButton.setOnClickListener {
                        runBlocking { withContext(Dispatchers.Default) { lifeEfficiencyClient.updateTodoItemStatus(todoItem.id, "done") } }
                        setupTodoList()
                    }
                    val inProgressButton = view.findViewById<Button>(R.id.TodoItemInProgressButton)
                    inProgressButton.setOnClickListener {
                        runBlocking {
                            withContext(
                                Dispatchers.Default,
                            ) { lifeEfficiencyClient.updateTodoItemStatus(todoItem.id, "in_progress") }
                        }
                        setupTodoList()
                    }
                    if (todoItem.status == "in_progress") {
                        view.setBackgroundColor(Color.argb(98, 250, 192, 94))
                    } else {
                        view.setBackgroundColor(Color.argb(0, 255, 255, 255))
                    }
                    return view
                }
            }
        view!!.findViewById<ListView>(R.id.TodoListList).adapter = listAdapter
    }

    private fun setupAddTodoButton() {
        view!!.findViewById<Button>(R.id.TodoListAddButton).setOnClickListener {
            val todoDescText = view!!.findViewById<EditText>(R.id.TodoListAddText)
            runBlocking { withContext(Dispatchers.Default) { lifeEfficiencyClient.addTodo(todoDescText.text.toString()) } }
            setupTodoList()
            todoDescText.setText("")
        }
    }
}
