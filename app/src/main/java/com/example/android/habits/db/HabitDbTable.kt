package com.example.android.habits.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import android.util.Log
import com.example.android.habits.Habit
import java.io.ByteArrayOutputStream

/*
    db method class
 */

class HabitDbTable(context: Context) {

    private val Tag = HabitDbTable::class.java.simpleName

    private val dbHelper = HabitTrainerDb(context)

    fun store(habit: Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(HabitEntry.TITLE_COL, habit.title)
            put(HabitEntry.DESCR_COL, habit.description)
            put(HabitEntry.IMAGE_COL, toByteArray(habit.image))
        }

        val id = db.transaction {
            it.insert(HabitEntry.TABLE_NAME, null, values)
        }
        return id
    }

    fun readAllHabits(): List<Habit> {
        val colunms = arrayOf(BaseColumns._ID, HabitEntry.TITLE_COL, HabitEntry.DESCR_COL, HabitEntry.IMAGE_COL)

        val order = "${BaseColumns._ID} ASC"

        val db = dbHelper.writableDatabase

        val cursor = db.doQuery(HabitEntry.TABLE_NAME, colunms, orderBy = order)

        return parseHabitsFrom(cursor)
    }

    private fun parseHabitsFrom(cursor: Cursor): MutableList<Habit> {
        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(HabitEntry.TITLE_COL)
            val desc = cursor.getString(HabitEntry.DESCR_COL)
            val bitmap = cursor.getBitmap(HabitEntry.IMAGE_COL)

            habits.add(Habit(title, desc, bitmap))
        }
        cursor.close()

        return habits
    }


    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}

private fun SQLiteDatabase.doQuery(table: String,
                                   columns: Array<String>,
                                   selection: String? = null,
                                   selectinArgs: Array<String>? = null,
                                   groupBy: String? = null,
                                   having: String? = null,
                                   orderBy: String? = null): Cursor {

    return query(table, columns, selection, selectinArgs, groupBy, having, orderBy)
}

private fun Cursor.getString(columnName: String): String {
    return this.getString(getColumnIndex(columnName))
}

private fun Cursor.getBitmap(columnName: String): Bitmap{
    val bytes = getBlob(getColumnIndex(columnName)) // returns a bytArray
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size) // returns a bitmap
}

private inline fun <T> SQLiteDatabase.transaction(function: (SQLiteDatabase) -> T): T { // T can also be Unit
    beginTransaction()
    val result = try {
        val returnValue = function(this)
        setTransactionSuccessful()

        returnValue
    } finally {
        endTransaction()
    }
    close()

    return result
}















