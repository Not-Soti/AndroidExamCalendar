package com.example.examcalendar.MonthActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.examcalendar.R;

import java.time.Month;
import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;
import me.grantland.widget.AutofitTextView;

/**
 * Class that draws a cell of the MonthActivity grid and its components
 */

public class MonthDaySquare extends LinearLayout {

    private Context context;
    //private TextView examNameTextView;
    //private ListView examNameListView;
    private LinearLayout examNameLinearLayout;
    private TextView dayTextView;
    //private String examStr;
    private ArrayList<String> examListStr;
    private String dayStr; //Strings to set on the view after inflating
    private int type; //Tells if a particular day is normal, a exam one or a holiday one
    private int month, year; //needed to start new activities from the menu popup
    public static final int NORMAL = 0;
    public static final int EXAM = 1;
    public static final int HOLIDAY = 2;

    private float textSize; //text size from the resources

    public MonthDaySquare(Context context) {
        super(context);
        this.context = context;
        initializeViews(context);
    }

    public MonthDaySquare(Context context, ArrayList<String> examStr, String dayStr, int type, int month, int year) {
        super(context);
        this.context = context;
        this.examListStr = examStr;
        this.dayStr = dayStr;
        this.type = type;
        this.month = month;
        this.year = year;

        textSize = getResources().getDimension(R.dimen.examTextSizeMonthGrid);

        initializeViews(context);
    }

    public MonthDaySquare(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeViews(context);
    }

    public MonthDaySquare(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initializeViews(context);
    }

    /**
     * Inflates the view in the layout
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_square, this);

        //examNameTextView = (TextView) this.findViewById(R.id.ExamNameText);
        //examNameListView = this.findViewById(R.id.ExamNameList);
        examNameLinearLayout = this.findViewById(R.id.LinearLayoutExamNames);
        dayTextView = this.findViewById(R.id.DayText);

        //examNameTextView.setText(examStr);
        //Add exams to the list
        //ArrayAdapter<String> listAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, examListStr);
        //examNameListView.setAdapter(listAdapter);
        //setListViewHeightBasedOnItems(examNameListView); //sets the heigth so it's not scrollable

        //adding exams to the linear layout
        for(String exam : examListStr){
            //TextView examView = new TextView(context);
            AutofitTextView examView = new AutofitTextView(context); //Class from the maven repo


            examView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            examView.setMaxTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            int minTextSize = (int)(24*75/100); //75% of the value
            examView.setMinTextSize(minTextSize);

            examView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            examView.setMaxLines(2);
            examView.setText(exam);

            examNameLinearLayout.addView(examView);
        }

        dayTextView.setText(dayStr);

        //Sets the background as defined by /res/drawable/month_week_square
        this.setBackgroundResource(checkTypeForBackground(this.type));

        //Setting click listeners for the view
        final Context auxContext = context;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenuFunctionallity(auxContext, view);
            }
        });

        /*
        //Setting the same click listener so clicking the list of exams also works
        examNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setMenuFunctionallity(MonthDaySquare.this.context, view);
            }
        });
        */

    }

    /**
     * Method to set the same click listener
     * on this view and on the list
     */
    private void setMenuFunctionallity(final Context auxContext, View view) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(auxContext, view);
        //Inflate the popup xml file
        popup.getMenuInflater().inflate(R.menu.month_cell_popup_menu, popup.getMenu());

        //registering actions on clicking the menu items
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.addExamPopupMenu:
                        i = new Intent(auxContext, DialogAddExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.editExamPopupMenu:
                        i = new Intent(auxContext, DialogEditExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.deleteExamPopupMenu:
                        i = new Intent(auxContext, DialogDeleteExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.addHolidaysPopupMenu:
                        i = new Intent(auxContext, DialogAddHolidays.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.deleteHolidaysPopupMenu:
                        i = new Intent(auxContext, DialogDeleteHolidays.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    /**
     * Checks the type of the day and returns the correct background to set
     */
    private int checkTypeForBackground(int i) {
        int ret = 0;
        switch (i) {
            case 0:
                ret = R.drawable.month_day_square_normal;
                break;
            case 1:
                ret = R.drawable.month_day_square_exam;
                break;
            case 2:
                ret = R.drawable.month_day_square_holiday;
                break;
        }
        return ret;
    }

    /*
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float textSize = getResources().getDimension(R.dimen.examTextSizeMonthGrid);
        examNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
    */

    /*
    public TextView getExamNameTextView(){
        return examNameTextView;
    }*/
    /*
    public ListView getExamNameListView() {
        return examNameListView;
    }*/

    public TextView getDayTextView() {
        return dayTextView;
    }


    /**
     * Sets atributes for the exam name and the day
     */
    /* NO FUNCIONA
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        examNameTextView.setText(examStr);
        dayTextView.setText(dayStr);
    }
    */


    /**
     * COPIED FROM https://stackoverflow.com/questions/1778485/android-listview-display-all-available-items-without-scroll-with-static-header
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    /*
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
*/
}
