package picture.diary.lenstofork.Diary.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import picture.diary.lenstofork.Diary.Entry;
import picture.diary.lenstofork.Diary.EntryHandler;

/**
 * https://www.javatpoint.com/android-sqlite-tutorial
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database.db";

    //-------- Entries Table
    public static final String TABLE_ENTRY = "entries";
    public static final String KEY_ID = "id";
    public static final String KEY_IMG = "image_file_path";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NOTE = "note";

    //------- EntryHandler Table
    public static final String TABLE_ENTRY_HANDLER = "entry_handler";
    public static final String KEY_DATE = "date";
    public static final String KEY_ENTRY0 = "entry0";
    public static final String KEY_ENTRY1 = "entry1";
    public static final String KEY_ENTRY2 = "entry2";
    public static final String KEY_ENTRY3 = "entry3";
    public static final String KEY_ENTRY4 = "entry4";
    public static final String KEY_ENTRY5 = "entry5";


    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //------ Create
        String createEntries = "CREATE TABLE " + TABLE_ENTRY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_IMG + " TEXT, " +
                KEY_TITLE + " TEXT, " +
                KEY_NOTE + " TEXT)";
        String createHandler =  "CREATE TABLE " + TABLE_ENTRY_HANDLER + "(" +
                KEY_DATE + " TEXT PRIMARY KEY," +
                KEY_ENTRY0 + " INTEGER, " +
                KEY_ENTRY1 + " INTEGER, " +
                KEY_ENTRY2 + " INTEGER, " +
                KEY_ENTRY3 + " INTEGER, " +
                KEY_ENTRY4 + " INTEGER, " +
                KEY_ENTRY5 + " INTEGER)";


        // execute commands
        db.execSQL(createEntries);
        db.execSQL(createHandler);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY_HANDLER);
        onCreate(db);
    }

    //------- Adding Data to the Database
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
        ContentValues valuesHandler = new ContentValues();
        valuesHandler.put(KEY_DATE, handler.getStringDate());

        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
            // stop when all of the entries are made but have not reached the Entry limit
            if(entries[i] == null){
                break;
            }

            entryIDs[i] = addEntry(entries[i], db);
            valuesHandler.put("entry" + i, entryIDs[i]);
        }

        // insert row and close database connection
        db.insert(TABLE_ENTRY_HANDLER, null, valuesHandler);

        // update entries to have ids
        handler.updateEntries(entryIDs);
    }

    private Long addEntry(Entry entry, SQLiteDatabase db){
        // make a new row in the Entries table
        ContentValues values = new ContentValues();
        values.put(KEY_IMG, entry.getImageFilePath());
        values.put(KEY_TITLE, entry.getTitle());
        values.put(KEY_NOTE, entry.getNote());

        //insert row and save the id in the entryIDs variables
        long id = db.insert(TABLE_ENTRY, null, values);
        return id;
    }

    //------- Getting Data from the Database
    /**
     * Retrieves the EntryHandler from the database that has the same date as the date (string
     * version) given
     *
     * @param dateStr the String version of the date that matches the EntryHandler to retrieve
     * @return an EntryHandler object
     */
    public EntryHandler getEntryHandler(String dateStr){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRY_HANDLER, new String[]{KEY_DATE, KEY_ENTRY0, KEY_ENTRY1,
                        KEY_ENTRY2, KEY_ENTRY3, KEY_ENTRY4, KEY_ENTRY5}, KEY_DATE + "=?",
                new String[]{dateStr}, null, null, null, null);

        // get the handler with its ID references to all of its entries
        if(cursor.moveToFirst()){
            // get the date and create the EntryHandler object
            EntryHandler handler = new EntryHandler(dateStr);

            // get all of the entries associated with the handler and add it to the handler obj
            int pos = 1; // pos 1 is the position of the first entry (entry0)
            while(cursor.getString(pos) != null && pos < 7){
                long entryID = cursor.getLong(pos);
                Entry currentEntry = getEntry(entryID);
                handler.addEntry(currentEntry);
                pos++;
            }

            cursor.close();
            return handler;
        }
        else{
            cursor.close();
            return null;
        }

    }

    /**
     * Retrieves the entry that matches the given ID using the given database. This method is used
     * to populate an EntryHandler object with entries.
     *
     * @param id the id of the entry to retrieve
     * @return an Entry object
     */
    private Entry getEntry(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRY, new String[]{
                KEY_ID, KEY_IMG, KEY_TITLE, KEY_NOTE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        if(cursor.moveToFirst()){
            String img = cursor.getString(1);
            String title = cursor.getString(2);
            String note = cursor.getString(3);

            cursor.close();
            return new Entry(id, img, title, note);
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
     * Returns all of the entries from the Entry table
     *
     * @return
     */
    public List<Entry> getAllEntries(){
        List<Entry> list = new ArrayList<Entry>();

        String query = "SELECT * FROM " + TABLE_ENTRY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                /**
                 * Column indices:
                 * 0 --> id
                 * 1 --> image file path
                 * 2 --> title
                 * 3 --> note
                 */

                Long id = cursor.getLong(0);
                String img = cursor.getString(1);
                String title = cursor.getString(2);
                String note = cursor.getString(3);
                Entry entry = new Entry(id, img, title, note);
                list.add(entry);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    //-------- Delete Entries
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
     * Deletes the row for the given entry from the Entry table
     *
     * @param entry the entry to be deleted from the database
     * @param db the open connection to the database
     */
    private void deleteEntry(Entry entry, SQLiteDatabase db){
        db.delete(TABLE_ENTRY, KEY_ID + "=?", new String[]{
                String.valueOf(entry.getId())});
    }

    //------- Update Entries

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

//        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
//            // if oldEntry is not in the newEntries list, delete it from the Entry table
//            Entry oldEntry = oldEntries.get(i);
//            Entry newEntry = newEntries.get(i);
//            if(newEntry == null && oldEntry == null){
//                // do nothing b/c both entries are null
//                continue;
//            }
//            else if(newEntry == null && oldEntry != null){
//                // delete old entry because it should be null
//                deleteEntry(oldEntry, db);
//            }
//            else if(oldEntry == null && newEntry != null){
//                // add the entry to the Entry table
//                newEntry.setId(addEntry(newEntry, db));
//            }
//            else {
//                // delete the removed entry from the Entry table & add the new entry
//                if(!newEntries.contains(oldEntry)){
//                    deleteEntry(oldEntry, db);
//                    // add the entry to the Entry table
//                    long newID = addEntry(newEntries.get(i), db);
//                    newEntries.get(i).setId(newID);
//                }
//                // update the changed entry
//                else if(oldEntries.contains(newEntry)){
//                    // update the entry in the Entry table
//                    updateEntry(newEntry, db);
//                }
//            }
//
//            // put the current position form the newEntries list into values
//            if(newEntries.get(i) != null){
//                values.put("entry"+i, newEntries.get(i).getId());
//            }
//        }

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
     * Updates the values for an Entry row item using the given entry.
     *
     * @param entry the entry
     * @param db the open connection to the database
     * @return number of rows affected
     */
    private int updateEntry(Entry entry, SQLiteDatabase db){
        // update the entry's attributes in the database
        ContentValues values = new ContentValues();
        values.put(KEY_IMG, entry.getImageFilePath());
        values.put(KEY_TITLE, entry.getTitle());
        values.put(KEY_NOTE, entry.getNote());

        // update row
        return db.update(TABLE_ENTRY, values, KEY_ID + "=?",
                new String[]{String.valueOf(entry.getId())});
    }

    //-------- Miscellaneous Methods
    public boolean doesEntryHandlerExist(String dateStr){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRY_HANDLER, new String[]{KEY_DATE, KEY_ENTRY0, KEY_ENTRY1,
                        KEY_ENTRY2, KEY_ENTRY3, KEY_ENTRY4, KEY_ENTRY5}, KEY_DATE + "=?",
                new String[]{dateStr}, null, null, null, null);
        boolean doesExists = cursor.getCount() > 0;
        cursor.close();
        return doesExists;
    }
}
