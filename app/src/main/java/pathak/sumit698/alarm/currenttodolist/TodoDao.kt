package pathak.sumit698.alarm.currenttodolist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTask(task: TodoEntity)

    @Update
    suspend fun updateTask(task: TodoEntity)

    @Delete
    suspend fun deleteTask(task: TodoEntity)

   @Query("DELETE FROM todo_table WHERE id = :id")

   suspend fun deleteBYId(id: Int)

    @Query("SELECT * FROM todo_table")
    fun getAllTasks(): LiveData<List<TodoEntity>>

}