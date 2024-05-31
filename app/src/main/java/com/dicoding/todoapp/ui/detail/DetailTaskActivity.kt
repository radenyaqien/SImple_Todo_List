package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

    private val viewModel: DetailTaskViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory.getInstance(this)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val id = intent.getIntExtra(TASK_ID, 0)
        viewModel.setTaskId(id)

        viewModel.task.observe(this) {
            findViewById<TextInputEditText>(R.id.detail_ed_title).setText(it.title)
            findViewById<TextInputEditText>(R.id.detail_ed_description).setText(it.description)
            findViewById<TextInputEditText>(R.id.detail_ed_due_date).setText(
                DateConverter.convertMillisToString(
                    it.dueDateMillis
                )
            )
        }
        findViewById<Button>(R.id.btn_delete_task).setOnClickListener {
            viewModel.deleteTask()
        }

    }
}