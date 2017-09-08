package com.example.minhd.demoappimagelock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Foder.sqlite";

    // Contacts table name
    private static final String TABLE_NAME = "Foder";

    // Contacts Table Columns names
    private static final String KEY_ID = "IdFoder";
    private static final String KEY_NAME = "NameFoder";
    private static final String KEY_DATE_CREATED = "DateCreatedFoder";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public boolean deleteFolder(Folder paramFolder) {
        int i = 1;
        SQLiteStatement localSQLiteStatement = getWritableDatabase().compileStatement("DELETE FROM Folder WHERE IdFolder= ? ");
        localSQLiteStatement.clearBindings();
        localSQLiteStatement.bindString(i, paramFolder.getIdFolder());
        if (localSQLiteStatement.executeUpdateDelete() == 0)
            i = 0;
        return true;
    }

    public Cursor getData(String paramString) {
        return getReadableDatabase().rawQuery(paramString, null);
    }

    public ArrayList<Folder> getListFolder() {
        ArrayList localArrayList = new ArrayList();
        Cursor localCursor = getData("SELECT * FROM Folder");
        if ((localCursor != null) && (localCursor.getCount() != 0))
            localCursor.moveToFirst();
        while (true) {
            if (localCursor.isAfterLast())
                return localArrayList;
            String str1 = localCursor.getString(localCursor.getColumnIndex("IdFolder"));
            String str2 = localCursor.getString(localCursor.getColumnIndex("NameFolder"));
            localCursor.getString(localCursor.getColumnIndex("DateCreatedFolder"));
            localArrayList.add(new Folder(str1, str2, localCursor.getInt(localCursor.getColumnIndex("TypeOfFolder"))));
            localCursor.moveToNext();
        }
    }

    public void insertFolder(Folder paramFolder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, paramFolder.getIdFolder());
        values.put(KEY_NAME,paramFolder.getNameFolder());
        values.put(KEY_DATE_CREATED, paramFolder.getDateCreated());

        db.insert(TABLE_NAME, null, values) ;
        db.close();
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        String CREATE_FODER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " CHAR PRIMARY KEY," + KEY_NAME + " CHAR,"
                + KEY_DATE_CREATED + " CHAR" + ")";
        paramSQLiteDatabase.execSQL(CREATE_FODER_TABLE);
    }

    public void queryData(String paramString) {
        getWritableDatabase().execSQL(paramString);
    }

    public void updateFolder(Folder paramFolder) {
        SQLiteStatement localSQLiteStatement = getWritableDatabase().compileStatement("UPDATE Folder SET NameFolder= ? DateCreatedFolder= ? TypeOfFolder= ?  WHERE IdFolder= ? ");
        localSQLiteStatement.clearBindings();
        localSQLiteStatement.bindString(1, paramFolder.getNameFolder());
        localSQLiteStatement.bindString(2, paramFolder.getDateCreated());
        localSQLiteStatement.bindString(3, String.valueOf(paramFolder.getImageFolder()));
        localSQLiteStatement.bindString(4, paramFolder.getIdFolder());
        localSQLiteStatement.executeUpdateDelete();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_foder_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_foder_table);

        onCreate(db);
    }
}
