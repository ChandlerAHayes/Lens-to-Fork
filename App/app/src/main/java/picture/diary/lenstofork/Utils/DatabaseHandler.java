package picture.diary.lenstofork.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import picture.diary.lenstofork.Diary.Entry.CaptionColor;
import picture.diary.lenstofork.Diary.Entry.CaptionPosition;
import picture.diary.lenstofork.Diary.Entry.Entry;
import picture.diary.lenstofork.Diary.Entry.EntryHandler;

/**
 * https://www.javatpoint.com/android-sqlite-tutorial
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database.db";
    private Context context;

    //-------- Entries Table
    public static final String TABLE_ENTRY = "entries";
    public static final String KEY_ID = "id";
    public static final String KEY_IMG = "image_file_path";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_CAPTION_COLOR = "caption_color";
    public static final String KEY_CAPTION_POSITION = "caption_position";
    public static final String KEY_DESCRIPTION = "description";

    //------- EntryHandler Table
    public static final String TABLE_ENTRY_HANDLER = "entry_handler";
    public static final String KEY_DATE = "date";
    public static final String KEY_ENTRY0 = "entry0";
    public static final String KEY_ENTRY1 = "entry1";
    public static final String KEY_ENTRY2 = "entry2";
    public static final String KEY_ENTRY3 = "entry3";
    public static final String KEY_ENTRY4 = "entry4";
    public static final String KEY_ENTRY5 = "entry5";

    //--------- Dimensions Table
    public static final String TABLE_DIMENSIONS = "dimensions";
    // the tag that matches the fragment/activity that it belongs to
    public static final String KEY_TAG = "tag";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WIDTH = "width";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //------ Create Commands
        String createEntries = "CREATE TABLE " + TABLE_ENTRY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_IMG + " TEXT, " +
                KEY_TITLE + " TEXT, " +
                KEY_CAPTION + " TEXT, " +
                KEY_CAPTION_COLOR + " TEXT, " +
                KEY_CAPTION_POSITION + " TEXT, " +
                KEY_DESCRIPTION + " TEXT)";

        String createHandler =  "CREATE TABLE " + TABLE_ENTRY_HANDLER + "(" +
                KEY_DATE + " TEXT PRIMARY KEY," +
                KEY_ENTRY0 + " INTEGER, " +
                KEY_ENTRY1 + " INTEGER, " +
                KEY_ENTRY2 + " INTEGER, " +
                KEY_ENTRY3 + " INTEGER, " +
                KEY_ENTRY4 + " INTEGER, " +
                KEY_ENTRY5 + " INTEGER)";

        String createDimensions = "CREATE TABLE " + TABLE_DIMENSIONS + " (" +
                KEY_TAG + " TEXT PRIMARY KEY, " +
                KEY_WIDTH + " INTEGER, " +
                KEY_HEIGHT + " INTEGER)";

        // execute commands
        db.execSQL(createEntries);
        db.execSQL(createHandler);
        db.execSQL(createDimensions);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY_HANDLER);
        onCreate(db);
    }

    //------- EntryHandler Methods
    /**
     * With a given handler takes all of the entries and inserts the entries and the handler
     * into the database into its corresponding tables
     *
     * @param handler the handler and its entries to be entered in a database
     */
    public void addEntries(EntryHandler handler){
        SQLiteDatabase db = this.getWritableDatabase();

        Entry[] entries = handler.getEntries();
        Long[] entryIDs = new Long[6];

        // make a new row in the Handler table
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, handler.getStringDate());

        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
            // stop when all of the entries are made but have not reached the Entry limit
            if(entries[i] == null){
                break;
            }

            entryIDs[i] = addEntry(entries[i], db);
            values.put("entry" + i, entryIDs[i]);
        }

        // insert row and close database connection
        db.insert(TABLE_ENTRY_HANDLER, null, values);

        // update entries to have ids
        handler.updateEntries(entryIDs);
    }

    /**
     * Retrieves the EntryHandler from the database that has the same date as the date (string
     * version) given
     *
     * @param dateStr the String version of the date that matches the EntryHandler to retrieve
     * @return an EntryHandler object
     */
    public EntryHandler getEntryHandler(String dateStr){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRY_HANDLER, new String[]{KEY_ENTRY0, KEY_ENTRY1,
                        KEY_ENTRY2, KEY_ENTRY3, KEY_ENTRY4, KEY_ENTRY5}, KEY_DATE + "=?",
                new String[]{dateStr}, null, null, null, null);

        // get the handler with its ID references to all of its entries
        if(cursor.moveToFirst()){
            // get the date and create the EntryHandler object
            EntryHandler handler = new EntryHandler(dateStr);

            int index = cursor.getColumnIndex(KEY_ENTRY0);
            handler.addEntry(getEntry(cursor.getLong(index)));

            index = cursor.getColumnIndex(KEY_ENTRY1);
            handler.addEntry(getEntry(cursor.getLong(index)));

            index = cursor.getColumnIndex(KEY_ENTRY2);
            handler.addEntry(getEntry(cursor.getLong(index)));

            index = cursor.getColumnIndex(KEY_ENTRY3);
            handler.addEntry(getEntry(cursor.getLong(index)));

            index = cursor.getColumnIndex(KEY_ENTRY4);
            handler.addEntry(getEntry(cursor.getLong(index)));

            index = cursor.getColumnIndex(KEY_ENTRY5);
            handler.addEntry(getEntry(cursor.getLong(index)));

            cursor.close();
            return handler;
        }
        else{
            cursor.close();
            return null;
        }

    }

    /**
     * Returns all of the EntryHandler objects stored in the database
     *
     * @return the list of all the EntryHandler
     */
    public List<EntryHandler> getAllEntryHandlers(){
        List<EntryHandler> handlerList = new ArrayList<EntryHandler>();

        String query = "SELECT * FROM " + TABLE_ENTRY_HANDLER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String date = cursor.getString(0);
                EntryHandler handler = new EntryHandler(date);

                // get all of the entries associated with the handler & add it to the handler object
                int pos = 1;
                while(cursor.getString(pos) != null && pos < 7){
                    long entryID = cursor.getLong(pos);
                    Entry entry = getEntry(entryID);
                    handler.addEntry(entry);
                    pos++;
                }

                // add handler w/ all of its entries to list
                handlerList.add(handler);
            } while(cursor.moveToNext());
        }

        cursor.close();
        return handlerList;
    }

    /**
     * Deletes the row from the EntryHandler table that matches the given handler
     *
     * @param handler the EntryHandler to be deleted
     */
    public void deleteEntryHandler(EntryHandler handler){
        SQLiteDatabase db = this.getWritableDatabase();

        // delete it's foreign keys (the entries)
        Entry[] entries = handler.getEntries();
        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
            if(entries[i] == null){
                break;
            }

            deleteEntry(entries[i], db);
        }

        String date = handler.getStringDate();
        db.delete(TABLE_ENTRY_HANDLER, KEY_DATE + "=?", new String[]
                {date});
    }

    /**
     *
     * @return
     */
    public int updateEntryHandler(EntryHandler handler){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //------ Compare Old Entries with New Entries
        EntryHandler oldHandler = getEntryHandler(handler.getStringDate());
        ArrayList<Entry> oldEntries = new ArrayList<>(Arrays.asList(oldHandler.getEntries()));
        ArrayList<Entry> newEntries = new ArrayList<>(Arrays.asList(handler.getEntries()));

        // delete old entries and add new entries
        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
            if(oldEntries.get(i) != null){
                deleteEntry(oldEntries.get(i), db);
            }
            if(newEntries.get(i) != null){
                newEntries.get(i).setId(addEntry(newEntries.get(i), db));
                values.put("entry"+i, newEntries.get(i).getId());
            }
        }

        return db.update(TABLE_ENTRY_HANDLER, values, KEY_DATE + " =?",
                new String[]{handler.getStringDate()});
    }

    /**
     * Tells if an EntryHandler that has the given dateStr exists within the database
     *
     * @param dateStr the string version of the date that matches the wanted EntryHandler
     * @return returns true if the EntryHandler exists, false otherwise
     */
    public boolean doesEntryHandlerExist(String dateStr){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRY_HANDLER, new String[]{KEY_DATE},
                KEY_DATE + "=?", new String[]{dateStr}, null, null,
                null, null);
        boolean doesExists = cursor.getCount() > 0;
        cursor.close();
        return doesExists;
    }

    //------- Entry Methods

    /**
     * Add the given entry to the database
     *
     * @param entry the entry to add
     * @param db the database connection
     * @return returns the id (Long) of the newly added entry
     */
    private Long addEntry(Entry entry, SQLiteDatabase db){
        // make a new row in the Entries table
        ContentValues values = new ContentValues();
        values.put(KEY_IMG, entry.getImageFilePath());
        values.put(KEY_TITLE, entry.getTitle());
        values.put(KEY_CAPTION, entry.getCaption());
        values.put(KEY_CAPTION_COLOR, entry.getCaptionColor().getColorString());
        values.put(KEY_CAPTION_POSITION, entry.getCaptionPosition().getValue());
        values.put(KEY_DESCRIPTION, entry.getDescription());

        //insert row and save the id in the entryIDs variables
        long id = db.insert(TABLE_ENTRY, null, values);
        return id;
    }

    /**
     * Retrieves the entry that matches the given ID using the given database. This method is used
     * to populate an EntryHandler object with entries.
     *
     * @param id the id of the entry to retrieve
     * @return an Entry object
     */
    public Entry getEntry(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRY, new String[]{KEY_ID, KEY_IMG, KEY_TITLE, KEY_CAPTION,
                        KEY_DESCRIPTION, KEY_CAPTION_COLOR, KEY_CAPTION_POSITION},
                KEY_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        if(cursor.moveToFirst()){
            //-------- Get Entry Attributes
            int index = cursor.getColumnIndex(KEY_IMG);
            String img = cursor.getString(index);

            index = cursor.getColumnIndex(KEY_TITLE);
            String title = cursor.getString(index);

            index = cursor.getColumnIndex(KEY_CAPTION);
            String caption = cursor.getString(index);

            cursor.getColumnIndex(KEY_DESCRIPTION);
            String description = cursor.getString(index);

            index = cursor.getColumnIndex(KEY_CAPTION_COLOR);
            String colorString = cursor.getString(index);

            index = cursor.getColumnIndex(KEY_CAPTION_POSITION);
            String positionString = cursor.getString(index);

            cursor.close();

            //--------- Create Entry
            Entry entry = new Entry(id, img, title, caption, description,
                    CaptionColor.getCaptionColor(colorString),
                    CaptionPosition.getCaptionPosition(positionString));
            return entry;
        }
        else{
            cursor.close();
            return null;
        }

    }

    /**
     * Returns all of the entries from the Entry table
     *
     * @return returns all of the entries from the Entry Table
     */
    public List<Entry> getAllEntries(){
        List<Entry> list = new ArrayList<Entry>();

        String query = "SELECT * FROM " + TABLE_ENTRY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                //-------- Get Entry Attributes
                int index = cursor.getColumnIndex(KEY_ID);
                Long id = cursor.getLong(index);

                index = cursor.getColumnIndex(KEY_IMG);
                String img = cursor.getString(index);

                index = cursor.getColumnIndex(KEY_TITLE);
                String title = cursor.getString(index);

                index = cursor.getColumnIndex(KEY_CAPTION);
                String caption = cursor.getString(index);

                index = cursor.getColumnIndex(KEY_CAPTION_COLOR);
                String description = cursor.getString(index);

                index = cursor.getColumnIndex(KEY_CAPTION_POSITION);
                String colorString = cursor.getString(index);

                cursor.getColumnIndex(KEY_DESCRIPTION);
                String positionString = cursor.getString(index);

                //-------- Create Entry
                Entry entry = new Entry(id, img, title, caption, description,
                        CaptionColor.getCaptionColor(colorString),
                        CaptionPosition.getCaptionPosition(positionString));

                list.add(entry);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Deletes the row for the given entry from the Entry table
     *
     * @param entry the entry to be deleted from the database
     * @param db the open connection to the database
     */
    private void deleteEntry(Entry entry, SQLiteDatabase db){
        db.delete(TABLE_ENTRY, KEY_ID + "=?", new String[]{
                String.valueOf(entry.getId())});
    }


    //-------- Dimensions Methods

    /**
     * Adds the given dimensions to the database
     *
     * @param tag the Tag that matches the activity/fragment that the dimensions belong to
     * @param width the width of the activity/fragment
     * @param height the height of the activity/fragment
     */
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

    /**
     * Deletes the dimensions that has the matching tag from the database
     *
     * @param tag the tag of the activity/fragment dimensions to delete
     */
    public void deleteDimensions(String tag){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DIMENSIONS, KEY_TAG + "=?", new String[]{tag});
    }

    /**
     * Updates the dimensions with the given tag to the values given.
     *
     * @param tag the activity/fragment that has the same tag
     * @param width the new width
     * @param height the new height
     * @return the number of rows affected
     */
    public int updateDimensions(String tag, int width, int height){
        SQLiteDatabase db = this.getWritableDatabase();

        // insert updated values
        ContentValues values = new ContentValues();
        values.put(KEY_WIDTH, width);
        values.put(KEY_HEIGHT, height);

        return db.update(TABLE_DIMENSIONS, values, KEY_TAG + "=?", new String[]{tag});
    }

    /**
     * Tells if dimensions that match the given tag exists.
     *
     * @param tag the tag that belongs to a specific activity/fragment
     * @return true if the dimensions exists, false otherwise
     */
    public boolean doesDimensionsExists(String tag){
        SQLiteDatabase db = this.getReadableDatabase();

        // query dimensions that match given tag
        Cursor cursor = db.query(TABLE_DIMENSIONS, new String[]{KEY_TAG}, KEY_TAG + "=?",
                new String[] {tag}, null, null,null, null);

        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        else{
            cursor.close();
            return false;
        }

    }
}
