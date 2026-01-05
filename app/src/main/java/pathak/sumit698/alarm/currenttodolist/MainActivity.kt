package pathak.sumit698.alarm.currenttodolist


import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView
    var list= mutableListOf<CustomDetails>()
    //     lateinit var currentDate:String
    var datetime:String?=null

    lateinit var selectDate: TextView

    lateinit var addTask: FloatingActionButton
    var id:Int=0

    lateinit var date: String

    lateinit  var customAdapter: RecyclerViewAdapter
    lateinit var database: TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        selectDate=findViewById(R.id.selectDate)


        database=TodoDatabase.getDatabase(this)
        Log.d("database",database.toString())
        database.todoDao().getAllTasks().observe(this) { data ->
            list.clear()

            for (item in data) {
                list.add(CustomDetails(item.title, item.type, item.dateTime, item.id))
            }

            customAdapter.notifyDataSetChanged()

            if (data.isNotEmpty()) {
                selectDate.setText(getCurrentDate())
            }
        }


        addTask=findViewById(R.id.fabAdd)
        date=getCurrentDate()

        addTask.setOnClickListener {
            datetime=customDate()
            var addDialog: Dialog= Dialog(this)
            addDialog.setContentView(R.layout.add_dialog)
            var cancel: Button=addDialog.findViewById(R.id.cancel)
            var add: Button=addDialog.findViewById(R.id.add)
            var editTitle: EditText =addDialog.findViewById(R.id.editTitle)
            var editType: EditText =addDialog.findViewById(R.id.editType)
            addDialog.show()
            add.setOnClickListener {

                var title: String = editTitle.text.toString()
                var type:String=editType.text.toString()


                if (title.isBlank() || type.isBlank()) {
                    if (type.isBlank() || (
                                !type.equals("All", ignoreCase = true) &&
                                        !type.equals("Work", ignoreCase = true) &&
                                        !type.equals("Home", ignoreCase = true) &&
                                        !type.equals("Personal", ignoreCase = true)
                                )
                    ) {
                        editType.error = "Please enter correct Type"
                    }

                    if (title.isBlank()) {
                        editTitle.error = "Please enter Title"
                    }
                } else {

                    GlobalScope.launch {
                        database.todoDao().insertTask(
                            TodoEntity(
                                title = title,
                                type = type,
                                dateTime = datetime,
                                date = date
                            )
                        )
                    }

                    addDialog.dismiss()
                }

            }

            cancel.setOnClickListener {
                addDialog.dismiss()
            }






        }

        recycler=findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)





        customAdapter= RecyclerViewAdapter(this,list,database)
        recycler.adapter=customAdapter




    }
}
fun customDate(): String {
    val calendar = Calendar.getInstance()

    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val dateStr = dateFormat.format(calendar.time)


    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeStr = timeFormat.format(calendar.time)

    val finalDateTime = "$dateStr, $timeStr"
    return finalDateTime
}
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}
