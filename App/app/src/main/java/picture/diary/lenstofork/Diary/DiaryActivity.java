package picture.diary.lenstofork.Diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import picture.diary.lenstofork.Utils.FragmentController;
import picture.diary.lenstofork.R;

public class DiaryActivity extends AppCompatActivity {
    // widgets
    private View tabLayout;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private TextView dateText;

    // variables
    private Calendar currentDate;
    private SimpleDateFormat simpleDateFormat;
    private String dateString = "";

    // constants
    public static final String EXTRA_CURRENT_DATE = "EXTRA_CURRENT_DATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        tabLayout = findViewById(R.id.tab);
        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarDialog();
            }
        });
        simpleDateFormat = new SimpleDateFormat("_MM_dd_yyyy");

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Lens to Fork");

        //------ Set Up DiaryFragment
        handleExtras();

        final FragmentController controller = new FragmentController(getSupportFragmentManager());
        controller.openFragment(setUpFragment(), DiaryFragment.TAG + dateString);

        //------ Widgets
        leftArrow = (ImageView) findViewById(R.id.img_left_arrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.DAY_OF_MONTH, -1);
                dateString = simpleDateFormat.format(currentDate.getTime());

                updateTabLayoutText();
                controller.openFragment(setUpFragment(), DiaryFragment.TAG + dateString);
            }
        });

        rightArrow = (ImageView) findViewById(R.id.img_right_arrow);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.DAY_OF_MONTH, 1);
                dateString = simpleDateFormat.format(currentDate.getTime());

                updateTabLayoutText();
                controller.openFragment(setUpFragment(), DiaryFragment.TAG + dateString);
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
            dateText.setText(sdf.format(currentDate.getTime()));
        }
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

    /**
     * Updates the text on the TabLayout to match the current date
     */
    private void updateTabLayoutText(){
        Calendar today = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int todayYear = today.get(Calendar.YEAR);

        if (today.equals(currentDate)){
            dateText.setText("Today");
        }
        // same year
        else if(currentYear - todayYear == 0 ){
            SimpleDateFormat sdf = new SimpleDateFormat("EE, MMM d");
            dateText.setText(sdf.format(currentDate.getTime()));
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
            dateText.setText(sdf.format(currentDate.getTime()));
        }
    }

    /**
     * Sets up the diary fragment to show the entries that correspond to the current date
     *
     * @return an initialized DiaryFragment object with the current date
     */
    private DiaryFragment setUpFragment(){
        // get Today's date
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateStr = sdf.format(currentDate.getTime());

        return DiaryFragment.newInstance(dateStr);
    }

    public static Intent newInstance(Activity activity, String dateString){
        Intent intent = new Intent(activity, DiaryActivity.class);
        intent.putExtra(EXTRA_CURRENT_DATE, dateString);
        return intent;
    }

    /**
     * Handles the extras that the Activity were initalized with
     */
    private void handleExtras(){
        if(getIntent().getExtras() == null){
            currentDate = Calendar.getInstance();
            dateString = simpleDateFormat.format(currentDate.getTime());
        }
        else{
            dateString = getIntent().getStringExtra(EXTRA_CURRENT_DATE);
            String[] dateSplit = dateString.split("-");
            int year = Integer.valueOf(dateSplit[2]);
            int month = Integer.valueOf(dateSplit[0])-1; // january is 0, must subtract by 1
            int day = Integer.valueOf(dateSplit[1]);

            // 	set(int year, int month, int date)
            currentDate = Calendar.getInstance();
            currentDate.set(year, month, day);
        }
    }

    /**
     * Opens a dialog that displays a CalendarView so that the user can pick a date to display.
     * The method handles opening the fragment to the corresponding date.
     */
    private void calendarDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_date_picker);

        // Initialize CalendarView
        CalendarView calendarView = (CalendarView) dialog.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month,
                                            int dayOfMonth)
            {
                // change to selected date
                currentDate.set(year, month, dayOfMonth);
                dateString = simpleDateFormat.format(currentDate.getTime());

                updateTabLayoutText();
                FragmentController controller = new FragmentController(getSupportFragmentManager());
                controller.openFragment(setUpFragment(), DiaryFragment.TAG + dateString);

                dialog.dismiss();
            }
        });

        dialog.show();
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