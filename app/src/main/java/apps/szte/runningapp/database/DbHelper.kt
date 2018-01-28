package apps.szte.runningapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import apps.szte.runningapp.dataclasses.ResultItem
import apps.szte.runningapp.dataclasses.User


class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "RunningAppDB.db"
        val TABLE_USERS = "USERS"
        val TABLE_RESULTS = "RESULTS"


        //USERS TABLE
        val USER_NAME = "NAME"
        val USER_WEIGHT = "WEIGHT"
        val USER_HEIGHT = "HEIGHT"
        val USER_AGE = "AGE"
        val USER_ID = "USERID"

        //RESULTS TABLE
        val USER_ID_IN_GPS = "USERID"
        val END_DATE_WITH_TIME = "END_DATE_WITH_TIME"
        val ID = "ID"
        val AVG_SPEED = "AVG_SPEED"
        val OVERALL_TIME = "OVERALL_TIME"
        val BURNED_CAL = "BURNED_CAL"
        val OVERALL_DISTANCE = "OVERALL_DISTANCE"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = ("CREATE TABLE " +
                TABLE_USERS + "("
                + USER_NAME + " TEXT PRIMARY KEY," +
                USER_WEIGHT
                + " TEXT," + USER_AGE + " INTEGER," +
                USER_HEIGHT + " TEXT," +
                USER_ID + " INTEGER)")

        val CREATE_RESULTS_TABLE = ("CREATE TABLE " +
                TABLE_RESULTS + "("
                + USER_ID_IN_GPS + " TEXT," +
                END_DATE_WITH_TIME
                + " DATETIME," +
                AVG_SPEED + " INTEGER,"
                + BURNED_CAL + " INTEGER,"
                + OVERALL_TIME + " INTEGER," +
                OVERALL_DISTANCE + " INTEGER," +
                ID + " INTEGER PRIMARY KEY)")

        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_RESULTS_TABLE)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun clearTable() {
        val db = this.writableDatabase
        db.execSQL("delete from " + TABLE_USERS)
    }

    fun addUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_NAME, user.name)
        values.put(USER_WEIGHT, user.weight)
        values.put(USER_AGE, user.age)
        values.put(USER_HEIGHT, user.height)

        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun addResult(userName: String, dist: Int, time: Long, avgSpeed: Int, burnedKcal: Int, date: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_ID_IN_GPS, userName)
        values.put(END_DATE_WITH_TIME, date)
        values.put(OVERALL_DISTANCE, dist)
        values.put(OVERALL_TIME, time)
        values.put(AVG_SPEED, avgSpeed)
        values.put(BURNED_CAL, burnedKcal)

        db.insert(TABLE_RESULTS, null, values)
        db.close()
    }

    fun getUserNames(): ArrayList<String> {
        var userNameList = ArrayList<String>()
        val db = this.writableDatabase
        val SELECT_USERS = "SELECT NAME FROM " + TABLE_USERS
        val cursor = db.rawQuery(SELECT_USERS, null)

        if (cursor.moveToFirst()) {
            do {
                userNameList.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        db.close()
        return userNameList
    }

    fun getUserResults(selectedUser: String): ArrayList<ResultItem> {
        var resultItemList = ArrayList<ResultItem>()
        val db = this.writableDatabase
        val SELECT_RESULTS = "SELECT " + OVERALL_DISTANCE + "," + END_DATE_WITH_TIME + "," + AVG_SPEED + "," + BURNED_CAL + "," + OVERALL_TIME + " FROM " + TABLE_RESULTS + " WHERE " + USER_ID_IN_GPS + " = '" + selectedUser + "'"
        val cursor = db.rawQuery(SELECT_RESULTS, null)

        if (cursor.moveToFirst()) {
            do {
                resultItemList.add(ResultItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)))
            } while (cursor.moveToNext())
        }
        db.close()
        return resultItemList
    }

    fun getUser(userName: String): User {
        val db = this.writableDatabase
        val SELECT_USER = "SELECT * FROM " + TABLE_USERS + " WHERE " + USER_NAME + " = '" + userName + "'"
        val cursor = db.rawQuery(SELECT_USER, null)
        //name, weight, age, height
        cursor.moveToFirst()

        return User(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3))
    }
}