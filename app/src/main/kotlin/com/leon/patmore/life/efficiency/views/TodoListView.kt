package com.leon.patmore.life.efficiency.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.leon.patmore.life.efficiency.R
import com.leon.patmore.life.efficiency.client.LifeEfficiencyClient
import com.leon.patmore.life.efficiency.client.domain.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception

class TodoListView(view: View,
                   button: Button,
                   private val addTodoButton: Button,
                   private val todoDescText: EditText,
                   private val listView: ListView,
                   private val context: Context,
                   private val lifeEfficiencyClient: LifeEfficiencyClient) :
        ActiveView(view, button) {

    override fun onActive() {
        setupTodoList()
        setupAddTodoButton()
    }

    private fun setupTodoList() {
        val todoList = runBlocking {
            withContext(Dispatchers.Default) {
                lifeEfficiencyClient.todoList()
            }
        }
        val listAdapter = object: ArrayAdapter<TodoItem>(context, R.layout.todo_item, todoList) {
            val inflater = LayoutInflater.from(context)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: inflater.inflate(R.layout.todo_item, parent, false)
                val todoItem = this.getItem(position)!!
                val textField = view.findViewById(R.id.textField) as TextView
                val text = "${todoItem.status}: ${todoItem.desc}"
                textField.text = text
                val completeButton = view.findViewById<Button>(R.id.TodoItemCompleteButton)
                completeButton.setOnClickListener {
                    runBlocking { withContext(Dispatchers.Default) { lifeEfficiencyClient.updateTodoItemStatus(todoItem.id, "done") } }
                    setupTodoList()
                }
                val inProgressButton = view.findViewById<Button>(R.id.TodoItemInProgressButton)
                inProgressButton.setOnClickListener {
                    runBlocking { withContext(Dispatchers.Default) { lifeEfficiencyClient.updateTodoItemStatus(todoItem.id, "in_progress") } }
                    setupTodoList()
                }
                return view
            }
        }
        listView.adapter = listAdapter
    }

    private fun setupAddTodoButton() {
        addTodoButton.setOnClickListener {
            runBlocking { withContext(Dispatchers.Default) { lifeEfficiencyClient.addTodo(todoDescText.text.toString()) } }
            setupTodoList()
            todoDescText.setText("")
        }
    }

}
