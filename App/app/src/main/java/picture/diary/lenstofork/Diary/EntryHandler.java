package picture.diary.lenstofork.Diary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EntryHandler implements Serializable {
    private Calendar date; // the date that the images are for
    private int numEntries; // number of entries
    private Entry[] entries = new Entry[ENTRY_LIMIT];

    public static final int ENTRY_LIMIT = 6;


    public EntryHandler(Calendar date){
        this.date = date;
        numEntries = 0;

        for(int i =0; i<ENTRY_LIMIT; i++){
            entries[i] = null;
        }

    }

    /**
     * Converts the string version of the date into a Calendar object. Creates the object.
     *
     * @param dateStr should be in the format of: MM-DD-YYYY
     */
    public EntryHandler(String dateStr){
        // convert the string representation of the date into a Calendar object
        date = Calendar.getInstance();
        int month = Integer.valueOf(dateStr.substring(0, 2))-1;
        int day = Integer.valueOf(dateStr.substring(3,5));
        int year = Integer.valueOf(dateStr.substring(6));

        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.YEAR, year);

        for(int i =0; i<ENTRY_LIMIT; i++){
            entries[i] = null;
        }
    }

    //-------- Handling Entries
    /**
     * Adds a new entry to the EntryHandler. If the limit of 6 entries has been reached, then
     * the method will return false.
     *
     * @param image the image for the entry
     * @param title the entry's title. Can be an empty string
     * @param note the entry's note. Can be an empty string
     * @return  true if there is room for another entry, false otherwise
     */
    public boolean addEntry(String image, String title, String note){
        // cannot have more than 6 entries
        if(numEntries == ENTRY_LIMIT){
            return false;
        }
        else{
            Entry newEntry = new Entry(image, title, note);
            entries[numEntries] = newEntry;
            numEntries++;
            return true;
        }
    }

    /**
     * Adds a new entry to the EntryHandler. If the limit of 6 entries has been reached, then
     * the method will return false.
     *
     * @param entry the entry to add
     * @return true if there is room for another entry, false otherwise
     */
    public boolean addEntry(Entry entry){
        if(numEntries == ENTRY_LIMIT){
            return false;
        }
        else{
            entries[numEntries] = entry;
            numEntries++;
            return true;
        }
    }

    public boolean removeEntry(int position){
        // cannot remove an entry with there are none
        if(numEntries == 0){
            return false;
        }

        // shift all the entries after its position over 1 to "delete" the entry
        int currPosition = position;
        while(currPosition < numEntries){
            // make the current position equal to its entry to the right
            entries[currPosition] = entries[currPosition+1];
            currPosition++;
        }
        // need to delete the tail end of the entries to remove the duplicate
        entries[currPosition-1] = null;

        // decrement numEntries
        numEntries--;
        return true;
    }

    /**
     * Updates all of the handler's entries so that they have ids that match the database
     *
     * @param ids the ids to go with the entries
     */
    public void updateEntries(Long[] ids){
        if (ids.length > ENTRY_LIMIT){
            return;
        }

        int pos = 0;
        while(entries[pos] != null && pos < ENTRY_LIMIT){
            entries[pos].setId(ids[pos]);
            pos++;
        }
    }

    /**
     * Updates the changes that occurred in a specific entry.
     *
     * @param position the position of the entry that needs to be updated
     * @param entry the entry that has changed
     */
    public void updateEntry(int position, Entry entry){
        entries[position] = entry;
    }

    //------- Getters
    public int getNumberOfEntries(){
        return numEntries;
    }

    public Calendar getDate() {
        return date;
    }

    /**
     * Formats the date into a string in the following format: MM-DD-YYYY
     *
     * @return returns the string representation of the date
     */
    public String getStringDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(date.getTime());
    }

    public Entry[] getEntries(){
        return entries;
    }

    public Entry getEntry(int position){
        return entries[position];
    }
}
