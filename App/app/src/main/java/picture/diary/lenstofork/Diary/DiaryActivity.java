package picture.diary.lenstofork.Diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import Entry.EntryHandler;
import picture.diary.lenstofork.Diary.Utils.DatabaseHandler;
import picture.diary.lenstofork.Diary.Utils.FragmentController;
import picture.diary.lenstofork.R;

public class DiaryActivity extends AppCompatActivity {
    // widgets
    private ImageView leftArrow;
    private ImageView rightArrow;
    private TextView dateText;

    // other variables
    private Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Lens to Fork");

        //------ Set Up DiaryFragment
        currentDate = Calendar.getInstance();
        final FragmentController controller = new FragmentController(getSupportFragmentManager());
        controller.openFragment(setUpFragment(), DiaryFragment.TAG);

        //------ Widgets
        leftArrow = (ImageView) findViewById(R.id.img_left_arrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.set(Calendar.DAY_OF_MONTH, -1);
                controller.openFragment(setUpFragment(), DiaryFragment.TAG);
            }
        });

        rightArrow = (ImageView) findViewById(R.id.img_right_arrow);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.set(Calendar.DAY_OF_MONTH, 1);
                controller.openFragment(setUpFragment(), DiaryFragment.TAG);
            }
        });

        // set text on date widget
        dateText = (TextView) findViewById(R.id.txt_title);
        Calendar today = Calendar.getInstance();
        if(getStringDate(today).equals(getStringDate(currentDate))){
            dateText.setText("Today");
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            dateText.setText(sdf.format(currentDate));
        }
    }

    private DiaryFragment setUpFragment(){
        // get Today's date
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateStr = sdf.format(currentDate.getTime());

        // check if EntryHandler exists in database
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        EntryHandler entryHandler = null;
        if(databaseHandler.doesEntryHandlerExist(dateStr)){
            // get EntryHandler from database
            entryHandler = databaseHandler.getEntryHandler(dateStr);
        }
        else{
            // create EntryHandler for today
            entryHandler = new EntryHandler(dateStr);
        }

        return DiaryFragment.newInstance(entryHandler);
    }

    /**
     * Formats the date into a string in the following format: MM-DD-YYYY
     *
     * @return returns the string representation of the date
     */
    public String getStringDate(Calendar date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(date.getTime());
    }
}

/*
*
* https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot
*
public Bitmap screenShot(View view) {
    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
            view.getHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    view.draw(canvas);
    return bitmap;
}
*/