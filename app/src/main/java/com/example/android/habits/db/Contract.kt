package com.example.android.habits.db

import android.provider.BaseColumns

/*
    contract class
 */

val DATABASE_NAME = "habittrainer.db" // database file
val DATABASE_VERSION = 10

object HabitEntry : BaseColumns {
    val TABLE_NAME = "habit"
    val TITLE_COL = "title"
    val DESCR_COL = "description"
    val IMAGE_COL = "image"

}