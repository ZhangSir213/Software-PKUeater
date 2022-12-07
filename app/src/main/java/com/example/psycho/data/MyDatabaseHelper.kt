package com.example.psycho.data
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MyDatabaseHelper (var context: Context, name: String, version: Int):SQLiteOpenHelper(context, name, null, version)
{
    public var createUser="create table User(" +
            "uid integer primary key autoincrement," +
            "weight real," +
            "height real,"+
            "birthday text,"+
            "name text," +
            "state int,"+
            "gender int,"+
            "avoidance int,"+
            "loginFirst int,"+
            "login int,"+
            "budget real,"+
            "password text,"+
            "step int,"+
            "menu text)"

    override fun onCreate(db: SQLiteDatabase?) {
//        下面这个todo 如果不注释掉的话就会报错。
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db?.execSQL(createUser)
        Toast.makeText(context,"Create Success",Toast.LENGTH_LONG).show()
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db?.execSQL("drop table if exists User")
        onCreate(db)
    }

}

