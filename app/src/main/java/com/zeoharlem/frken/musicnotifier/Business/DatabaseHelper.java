package com.zeoharlem.frken.musicnotifier.Business;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.zeoharlem.frken.musicnotifier.Models.Folders;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION   = 2;

    // Logcat tag
    private static final String LOG             = "DatabaseHelper";

    public static final String DATABASE_NAME    = "musicnotifier.db";

    //Table Names for the DAtabase
    private static final String TABLE_FOLDER    = "folders";
    private static final String TABLE_FILES_ROW = "musicfiles";

    //Common columns for the Tables
    private static final String KEY_ID          = "id";
    private static final String CREATED_AT      = "created_at";

    //Folders table - column names
    private static final String FOLDER_NAME     = "folder_name";
    private static final String STORAGE_TYPE    = "storagetype";

    //MusicFiles table -column names
    private static final String MUSIC_FILES_COL = "filename";
    private static final String FOLDER_PATH_COL = "folder_path";
    private static final String INTENT_REQ_CODE = "intent_req_code";
    private static final String STATUS_COL      = "status";

    //Table create Statements
    //FOLDERS TABLE Statement
    private static final String CREATE_FOLDER_TABLE = "CREATE TABLE "
            +TABLE_FOLDER+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +FOLDER_NAME+" TEXT, "+STORAGE_TYPE+" TEXT, "+CREATED_AT+" DATETIME)";

    private static final String CREATE_TABLE_FILES_ROW  = "CREATE TABLE "
            +TABLE_FILES_ROW+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +MUSIC_FILES_COL+" TEXT, "+FOLDER_PATH_COL+" TEXT, "+INTENT_REQ_CODE+" INT, "
            +STATUS_COL+" INTEGER, "+CREATED_AT+" DATETIME)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FOLDER_TABLE);
        db.execSQL(CREATE_TABLE_FILES_ROW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FOLDER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FILES_ROW);
        onCreate(db);
    }

    /**
     * delete Query for DB
     */
    public void deleteQueryFolderRow(){
        SQLiteDatabase db   = this.getWritableDatabase();
        String clearDbQuery = "DELETE FROM "+TABLE_FOLDER;
        db.execSQL(clearDbQuery);
        db.close();
    }

    public void dropTables(){
        SQLiteDatabase db   = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FOLDER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FILES_ROW);
        db.execSQL(CREATE_FOLDER_TABLE);
        db.execSQL(CREATE_TABLE_FILES_ROW);
    }

    public void deleteQueryFilesRow(){
        SQLiteDatabase db   = this.getWritableDatabase();
        String clearDbQuery = "DELETE FROM "+TABLE_FILES_ROW;
        db.execSQL(clearDbQuery);
        db.close();
    }

    public int deleteAfilesRow(String id){
        SQLiteDatabase db   = this.getWritableDatabase();
        String[] whereArgs  = {id};
        int count           = db.delete(TABLE_FILES_ROW, KEY_ID + " =? ", whereArgs);
        return count;
    }

    /**
     *
     * @param folders {@link Folders}
     * @return boolean
     */
    public boolean createFolder(Folders folders){
        SQLiteDatabase db   = this.getWritableDatabase();
        ContentValues cV    = new ContentValues();
        cV.put(FOLDER_NAME, folders.getFolderName());
        cV.put(STORAGE_TYPE, folders.getStorageType());
        cV.put(CREATED_AT, getDateTime());
        long result         = db.insert(TABLE_FOLDER, null, cV);
        return result != -1;
    }

    /**
     *
     * @param musicFile {@link MusicFile}
     * @return boolean
     */
    public boolean insertMusicFiles(MusicFile musicFile){
        SQLiteDatabase db   =  this.getWritableDatabase();
        ContentValues cV    = new ContentValues();
        cV.put(MUSIC_FILES_COL, musicFile.getFilename());
        cV.put(FOLDER_PATH_COL, musicFile.getFolderPath());
        cV.put(INTENT_REQ_CODE, musicFile.getIntentReqCode());
        cV.put(CREATED_AT, musicFile.getCreatedAt());
        cV.put(STATUS_COL, musicFile.getStatus());
        long result         = db.insert(TABLE_FILES_ROW, null, cV);
        return result != -1;
    }

    /**
     *
     * @param folders {@link Folders}
     * @param context this
     * @return boolean
     */
    public boolean getDirectoryLimit(Folders folders, Context context){
        SQLiteDatabase db   = this.getReadableDatabase();
        String selectQuery  = "SELECT * FROM "+TABLE_FOLDER+" LIMIT 1";
        Cursor cursor       = db.rawQuery(selectQuery, null);
        if(cursor != null && cursor.moveToFirst()){
            folders.setFolderName(cursor.getString(cursor.getColumnIndex(FOLDER_NAME)));
            folders.setStorageType(cursor.getString(cursor.getColumnIndex(STORAGE_TYPE)));
            folders.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     *
     * @return ArrayList<MusicFile></MusicFile>
     */
    public ArrayList<MusicFile> getAllMusicFilesRow(){
        SQLiteDatabase db   = this.getReadableDatabase();
        ArrayList<MusicFile> arrayList  = new ArrayList<>();
        String seleteQuery  = "SELECT * FROM "+TABLE_FILES_ROW;
        Cursor cursor       = db.rawQuery(seleteQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    MusicFile musicFile = new MusicFile();
                    musicFile.setStatus(cursor.getString(cursor.getColumnIndex(STATUS_COL)));
                    musicFile.setCreatedAt(cursor.getString(cursor.getColumnIndex(CREATED_AT)));
                    musicFile.setFolderPath(cursor.getString(cursor.getColumnIndex(FOLDER_PATH_COL)));
                    musicFile.setFilename(cursor.getString(cursor.getColumnIndex(MUSIC_FILES_COL)));
                    musicFile.setIntentReqCode(cursor.getInt(cursor.getColumnIndex(INTENT_REQ_CODE)));
                    musicFile.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));

                    arrayList.add(musicFile);
                }
                while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }
        return arrayList;
    }

    public int deleteToDoRow(String columnId){
        SQLiteDatabase db   = this.getWritableDatabase();
        return db.delete(TABLE_FILES_ROW, KEY_ID + " =? ", new String[]{ columnId });
    }

    /**
     *
     * @param musicFile MusicFile
     * @return int
     */
    public int updateFileRowTable(MusicFile musicFile){
        SQLiteDatabase db   = this.getWritableDatabase();
        ContentValues cv    = new ContentValues();
        cv.put(MUSIC_FILES_COL, musicFile.getFilename());
        cv.put(CREATED_AT, musicFile.getCreatedAt());
        return db.update(TABLE_FILES_ROW, cv, KEY_ID + " =? ", new String[]{ musicFile.getId() });
    }

    public int getMusicFileTableCount(String timeSet){
        Cursor cursor   = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + TABLE_FILES_ROW + " WHERE " + CREATED_AT + "=?";
            cursor = db.rawQuery(query, new String[]{timeSet});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return 0;
    }

    //Close Datbase COnnection
    public void closeDB() {
        SQLiteDatabase db   = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date                   = new Date();
        return dateFormat.format(date);
    }
}
