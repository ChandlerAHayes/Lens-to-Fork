package picture.diary.lenstofork.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseDimensionsHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database.db";

    //--------- Dimensions Table
    public static final String TABLE_DIMENSIONS = "dimensions";
    // the tag that matches the fragment/activity that it belongs to
    public static final String KEY_TAG = "tag";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WIDTH = "width";

    public DatabaseDimensionsHandler(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //------ Create Commands
        String createDimensions = "CREATE TABLE " + TABLE_DIMENSIONS + " (" +
                KEY_TAG + " TEXT PRIMARY KEY, " +
                KEY_WIDTH + " INTEGER, " +
                KEY_HEIGHT + " INTEGER)";

        db.execSQL(createDimensions);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIMENSIONS);
        onCreate(db);
    }

    public void addDimensions(String tag, int width, int height){
        SQLiteDatabase db = this.getWritableDatabase();

        // insert data into ContentValues
        ContentValues values = new ContentValues();
        values.put(KEY_TAG, tag);
        values.put(KEY_WIDTH, width);
        values.put(KEY_HEIGHT, height);

        // insert new row in table
        db.insert(TABLE_DIMENSIONS, null, values);
    }

    /**
     * Returns the width and height that matches the tag.
     *
     * @param tag the tag that belongs to an activity/fragment
     * @return an int array where index 0 contains the width & index 1 contains the height. Returns
     *          null if the tag does not exist within the database
     */
    public int[] getDimensions(String tag){
        SQLiteDatabase db = this.getReadableDatabase();

        // query dimensions that match given tag
        Cursor cursor = db.query(TABLE_DIMENSIONS, new String[]{KEY_TAG, KEY_WIDTH, KEY_HEIGHT},
                KEY_TAG + "=?", new String[] {tag}, null, null,
                null, null);

        // handle results
        if(cursor.moveToFirst()){
            int[] results = new int[2];

            int widthIndex = cursor.getColumnIndex(KEY_WIDTH);
            results[0] = cursor.getInt(widthIndex);
            int heightIndex = cursor.getColumnIndex(KEY_HEIGHT);
            results[1] = cursor.getInt(heightIndex);

            cursor.close();
            return results;
        }
        else{
            cursor.close();
            return null;
        }
    }

    public void deleteDimensions(String tag){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DIMENSIONS, KEY_TAG + "=?", new String[]{tag});
    }

    public int updateDimensions(String tag, int width, int height){
        SQLiteDatabase db = this.getWritableDatabase();

        // insert updated values
        ContentValues values = new ContentValues();
        values.put(KEY_WIDTH, width);
        values.put(KEY_HEIGHT, height);

        return db.update(TABLE_DIMENSIONS, values, KEY_TAG + "=?", new String[]{tag});
    }

    public boolean doesDimensionsExists(String tag){
        SQLiteDatabase db = this.getReadableDatabase();

        // query dimensions that match given tag
        Cursor cursor = db.query(TABLE_DIMENSIONS, new String[]{KEY_TAG}, KEY_TAG + "=?",
                new String[] {tag}, null, null,null, null);

        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }
}
