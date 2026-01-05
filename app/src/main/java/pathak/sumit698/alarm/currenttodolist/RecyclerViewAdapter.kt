package pathak.sumit698.alarm.currenttodolist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.text.category

class RecyclerViewAdapter(var context: Context,var list: MutableList<CustomDetails>,var database: TodoDatabase): RecyclerView.Adapter<pathak.sumit698.alarm.currenttodolist.RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.customadapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(list.get(position).title)
        holder.category.setText(list.get(position).category)
        holder.datetime.setText(list.get(position).datetime)

        holder.itemView.setOnClickListener {
            var dialog: Dialog = Dialog(context)
            dialog.setContentView(R.layout.update_data)
            var cancel: Button = dialog.findViewById(R.id.cancel)
            var update: Button = dialog.findViewById(R.id.update)
            var editTitle: EditText = dialog.findViewById(R.id.editTitle)
            var editType: EditText = dialog.findViewById(R.id.editType)
            editTitle.setText(list.get(position).title)
            editType.setText(list.get(position).category)
            dialog.show()

            update.setOnClickListener {
                val title = editTitle.text.toString()
                val type = editType.text.toString()

                var isValid = true

                if (title.isBlank()) {
                    editTitle.error = "Please enter Title"
                    isValid = false
                }

                if (type.isBlank() || (
                            !type.equals("All", ignoreCase = true) &&
                                    !type.equals("Work", ignoreCase = true) &&
                                    !type.equals("Home", ignoreCase = true) &&
                                    !type.equals("Personal", ignoreCase = true)
                            )
                ) {
                    editType.error = "Please enter correct Type"
                    isValid = false
                }


                if (isValid) {
                    list[position] = CustomDetails(title, type, list[position].datetime, list[position].id)
                    notifyItemChanged(position)

                    GlobalScope.launch {
                        database.todoDao().updateTask(TodoEntity(
                            id = list[position].id,
                            title = title,
                            type = type,
                            dateTime = list[position].datetime,
                            date = list[position].datetime
                        ))
                    }
                    dialog.dismiss()
                }
            }


            cancel.setOnClickListener {
                dialog.dismiss()
            }


        }

        holder.itemView.setOnLongClickListener {
            var alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            alertDialog.setTitle("Delete")
            alertDialog.setMessage("Are you sure want to delete it?")
            alertDialog.setIcon(R.drawable.outline_delete_24)
            alertDialog.setPositiveButton("yes") { dialogInterface: DialogInterface, i: Int ->
                val taskId = list[position].id
                GlobalScope.launch {
                    database.todoDao().deleteBYId(taskId)
                }
                list.removeAt(position)
                notifyItemRemoved(position)
                dialogInterface.dismiss()

            }
            alertDialog.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            alertDialog.show()
            true

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tvTask1Title)
        var category: TextView = itemView.findViewById(R.id.tvTask1Category)
        var datetime: TextView = itemView.findViewById(R.id.tvTask1Date)

    }
}
