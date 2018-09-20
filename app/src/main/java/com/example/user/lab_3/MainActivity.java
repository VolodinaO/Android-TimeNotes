package com.example.user.lab_3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private TextView tv;

    private Button buttonStatistic;
    private DatePicker datePickerStart;
    private DatePicker datePickerEnd;
    private RecyclerView recyclerCategory;
    private RecyclerView recyclerRecord;
    private FloatingActionButton floatingActionButton;
    private EditText editText;
    private RadioButton radioMonth;
    private RadioButton radioVar;
    private TextView topCount;
    private TextView topTime;
    private TextView topSum;
    private LinearLayout linear;
    private List<CheckBox> checkBox = new ArrayList<>();

    private ContentValues cv;
    List<Category> category = new ArrayList<>();
    List<Record> records = new ArrayList<>();
    Context context;
    int position;
    String dateStart,dateEnd;

    List<Integer> tabs = new ArrayList<>();

    AlertDialog al;
    AlertDialog.Builder ad;
    View layoutView;
    AlertDialog all;
    AlertDialog.Builder add;

    TabHost tabHost;

    ////////////////////////////////////////
    private int[] COLORS = new int[] { Color.GREEN, Color.BLUE,Color.MAGENTA, Color.CYAN, Color.RED, Color.DKGRAY, Color.WHITE, Color.YELLOW, Color.LTGRAY };
    private double[] VALUES;// = new double[] { 10, 11, 12, 13 };
    private String[] NAME_LIST;// = new String[] { "A", "B", "C", "D" };
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    /////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = MainActivity.this;

        //////////////////////////////////////////

        cv = new ContentValues();

        String today = getTodayDate();
        int month = Integer.valueOf(today.substring(3, 5));
        int day = Integer.valueOf(today.substring(0, 2));
        int year = Integer.valueOf(today.substring(6));
        dateStart = "01."+month+"."+year;
        dateEnd = "31."+month+"."+year;

        updateCategory();
        updateRecords();
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(getString(R.string.records));
        tabHost.addTab(tabSpec);

//        tabSpec = tabHost.newTabSpec("tag2");
//        tabSpec.setContent(R.id.tab2);
//        tabSpec.setIndicator(getString(R.string.category));
//        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator(getString(R.string.statistics));
        tabHost.addTab(tabSpec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                Log.d("LOG_TAG", "!!!! ");
                if(tabs.get(tabs.size()-1)!=tabHost.getCurrentTab())
                    tabs.add(tabHost.getCurrentTab());
            }
        });

        tabHost.setCurrentTab(0);

        tabs.add(0);
        Log.d("LOG_TAG", "add tabs : " + 0); //////

        ///////////////////////////////////////////////

//        recyclerCategory = (RecyclerView)findViewById(R.id.recycler_category); //
//        recyclerCategory.setLayoutManager(new LinearLayoutManager(this)); //
//        recyclerCategory.setAdapter(new CategoryAdaper()); //

        floatingActionButton = (FloatingActionButton)findViewById(R.id.action_add_record);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecordActivity.class);
                intent.putExtra(RecordActivity.STATE,"new");
                intent.putExtra(RecordActivity.PHOTO,"f");
                startActivity(intent);
            }
        });

        recyclerRecord = (RecyclerView)findViewById(R.id.recycler_record);
        recyclerRecord.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecord.setAdapter(new RecordAdaper());

        recyclerRecord.getAdapter().notifyDataSetChanged();
        ////////////////////////////////////////////////////
        radioMonth = (RadioButton)findViewById(R.id.radio_month);
        radioMonth.setChecked(true);
        radioMonth.setOnClickListener(radioButtonClickListener);
        radioVar = (RadioButton)findViewById(R.id.radio_var);
        radioVar.setChecked(false);
        radioVar.setOnClickListener(radioButtonClickListener);
        datePickerStart = (DatePicker)findViewById(R.id.date_start);
        datePickerStart.setEnabled(false);
        datePickerEnd = (DatePicker)findViewById(R.id.date_end);
        datePickerEnd.setEnabled(false);
        buttonStatistic = (Button)findViewById(R.id.button_statistic);
        buttonStatistic.setEnabled(false);

        topCount = (TextView)findViewById(R.id.text_top_count);
        updateTopCount();
        topTime = (TextView)findViewById(R.id.text_top_time);
        updateTopTime();


        linear = (LinearLayout)findViewById(R.id.linear_check);
        topSum = (TextView)findViewById(R.id.text_top_sum);
        addCheckBox();

        printGraphic();
    }

    ////////////////////////////////////////////////////////

    public void printGraphic() {
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        List<String> cat = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String query = "SELECT name FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            cat.add(cursor.getString(0));
        }
        cursor.close();

        mSeries.clear();

        //tv.setText(cat.size());

        VALUES = Statistic.getTimeAllCatogory(dateStart,dateEnd);
        NAME_LIST = new String[cat.size()];
        for(int i = 0; i<cat.size();i++)
            NAME_LIST[i] = cat.get(i);

        for (int i = 0; i < VALUES.length; i++) {
            mSeries.add(NAME_LIST[i] + " " + String.format("%.2f",VALUES[i]), VALUES[i]);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
        }

        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(context, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection == null) {
                        //Toast.makeText(context,"No chart element was clicked",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //Toast.makeText(context,"Chart element data point index "+ (seriesSelection.getPointIndex()+1) + " was clicked" + " point value="+ seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        //Toast.makeText(context,"No chart element was long pressed", Toast.LENGTH_SHORT);
                        return false;
                    }
                    else {
                        //Toast.makeText(context,"Chart element data point index "+ seriesSelection.getPointIndex()+ " was long pressed",Toast.LENGTH_SHORT);
                        return true;
                    }
                }
            });
            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        }
        else
        {
            mChartView.repaint();
        }

    }

    ////////////////////////////////////////////////////////

    public void updateCategory(){
        category.clear();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String query = "SELECT * FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            category.add(new Category(Integer.valueOf(cursor.getString(0)),cursor.getString(1)));
        }
        cursor.close();
        if(radioMonth != null) {
            radioMonth.setChecked(true);
            radioVar.setChecked(false);
            datePickerStart.setEnabled(false);
            datePickerEnd.setEnabled(false);
            buttonStatistic.setEnabled(false);
        }
    }

    public void updateRecords(){
        records.clear();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String query = "SELECT * FROM Record";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            records.add(new Record(
                    Integer.valueOf(cursor.getString(0)),
                    Integer.valueOf(cursor.getString(1)),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)));
        }
        cursor.close();

        //tv.setText(records.size());
    }

    public void addCheckBox(){
        checkBox.clear();
        linear.removeAllViews();
        CheckBox cb;
        for(int i = 0; i<category.size();i++){
            cb = new CheckBox(context);
            cb.setId(i);
            cb.setOnClickListener(checkBoxClickListener);
            cb.setText(category.get(i).getName());
            checkBox.add(cb);
        }

        for(CheckBox c:checkBox)
            linear.addView(c);

        updateTopSum();
    }

    View.OnClickListener checkBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateTopSum();
        }
    };

    @Override
    public void onBackPressed() {
        if(tabs.size()>1){
            tabs.remove(tabs.size()-1);
            tabHost.setCurrentTab(tabs.get(tabs.size()-1));
        }
        else this.finish();

    }

    /////////////////////////////////////////////////
//    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//        public TextView textNameCategory;
//        private CardView cv;
//
//        public CategoryViewHolder(View itemView) {
//            super(itemView);
//            cv = (CardView)itemView.findViewById(R.id.cv);
//            textNameCategory = (TextView) itemView.findViewById(R.id.text_name_category);
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            LayoutInflater li = LayoutInflater.from(context);
//            layoutView = li.inflate(R.layout.edit_category, null);
//            TextView tv;
//            position = this.getLayoutPosition();
//
//            ad = new AlertDialog.Builder(context);
//            ad.setView(layoutView);
//            ad.setTitle("Edit category");
//            ad.setCancelable(false);
//
//            editText = (EditText)layoutView.findViewById(R.id.edit_name_category);
//            editText.setText(category.get(position).getName());
//
//            al = ad.create();
//            al.show();
//        }
//
//        /*public MeetingViewHolder(ViewGroup parent) {
//            super(getLayoutInflater().inflate(R.layout.meeting_item, parent, false));
//            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
//            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
//            stringTextView = (TextView) itemView.findViewById(R.id.stringTextView);
//        }*/
//    }

//    class CategoryAdaper extends RecyclerView.Adapter<CategoryViewHolder> {
//
//        @Override
//        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.category_item, parent, false);
//            CategoryViewHolder viewHolder = new CategoryViewHolder(itemLayoutView);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(CategoryViewHolder holder, int position) {
//            holder.textNameCategory.setText(category.get(position).getName());
//        }
//
//        @Override
//        public int getItemCount() {
//            return category.size();
//        }
//    }

    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textRefCategory;
        public TextView textTimeStart;
        public TextView textTimeEnd;
        public TextView textTime;
        public TextView textDate;
        private CardView cv;

        public RecordViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            textRefCategory = (TextView) itemView.findViewById(R.id.text_ref_category);
            textTimeStart = (TextView) itemView.findViewById(R.id.text_time_start);
            textTimeEnd = (TextView) itemView.findViewById(R.id.text_time_end);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textDate = (TextView) itemView.findViewById(R.id.text_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = this.getLayoutPosition();
            Intent intent = new Intent(MainActivity.this,RecordActivity.class);
            intent.putExtra(RecordActivity.STATE,"open");
            intent.putExtra(RecordActivity.PHOTO,"");
            intent.putExtra(RecordActivity.RECORD,records.get(position));
            startActivity(intent);
        }

        /*public MeetingViewHolder(ViewGroup parent) {
            super(getLayoutInflater().inflate(R.layout.meeting_item, parent, false));
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            stringTextView = (TextView) itemView.findViewById(R.id.stringTextView);
        }*/
    }

    class RecordAdaper extends RecyclerView.Adapter<RecordViewHolder> {

        @Override
        public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.time_item, parent, false);
            RecordViewHolder viewHolder = new RecordViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, int position) {
            SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
            String query = "SELECT name FROM Category where _id='"+records.get(position).getCategoryId()+"'";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                holder.textRefCategory.setText(cursor.getString(0));
            }
            cursor.close();
            holder.textTimeStart.setText(records.get(position).getTimeStart());
            holder.textTimeEnd.setText(records.get(position).getTimeEnd());
            holder.textTime.setText(records.get(position).getTime());
            holder.textDate.setText(records.get(position).getDate());
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }


    ////////////////////////////////////////////////////////
    public void onClickCancel(View view){
        al.cancel();
    }

    ///////////////////////////////////////////////////////////////
//    public void onClickAddCategory(View view){
//        LayoutInflater li = LayoutInflater.from(context);
//        layoutView = li.inflate(R.layout.create_category, null);
//        TextView tv;
//
//        ad = new AlertDialog.Builder(context);
//        ad.setView(layoutView);
//        ad.setTitle("Create category");
//        ad.setCancelable(false);
//        editText = (EditText)layoutView.findViewById(R.id.edit_name_category);
//        al = ad.create();
//        al.show();
//    }

//    public void onClickCreateCategory(View view){
//        //добавление значения
//        String query = "INSERT INTO Category (name) VALUES ('"+editText.getText().toString()+"')";
//        DbHelper.getInstance().getWritableDatabase().execSQL(query);
//        al.cancel();
//        updateCategory();
//        addCheckBox();
//        printGraphic();
//        recyclerCategory.getAdapter().notifyDataSetChanged();
//    }

//    public void onClickSaveCategory(View view){
//
//        editText = (EditText)layoutView.findViewById(R.id.edit_name_category);
//        //tv.setText(editText.getText().toString());
//        //изменение значения
//        cv.clear();
//        cv.put("name", editText.getText().toString());
//        DbHelper.getInstance().getWritableDatabase()
//                .update("Category", cv, "_id = ?", new String[]{String.valueOf(category.get(position).getId())});
//        al.cancel();
//        updateCategory();
//        addCheckBox();
//        printGraphic();
//        recyclerCategory.getAdapter().notifyDataSetChanged();
//    }

//    public void onClickDeleteCategory(View view){
//        //удаление значения
//        add = new AlertDialog.Builder(context);
//        add.setTitle("Удалить категорию");
//        add.setMessage("Удалить категорию и все записи связанные с ней?");
//        add.setCancelable(false);
//
//        add.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String insertQuery = "DELETE FROM Record WHERE category_id='"+category.get(position).getId()+"'";
//                DbHelper.getInstance().getWritableDatabase().execSQL(insertQuery);
//
//                DbHelper.getInstance().getWritableDatabase()
//                        .delete("Category","_id = ?", new String[]{String.valueOf(category.get(position).getId())});
//                updateCategory();
//                updateRecords();
//                addCheckBox();
//                printGraphic();
//
//                recyclerRecord.getAdapter().notifyDataSetChanged();
//                recyclerCategory.getAdapter().notifyDataSetChanged();
//            }
//        });
//
//        add.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                all.cancel();
//            }
//        });
//
//        all = add.create();
//        all.show();
//
//        al.cancel();
//        recyclerCategory.getAdapter().notifyDataSetChanged();
//    }
//
//    public void onClickAddRecord(View view){
//        Intent intent = new Intent(MainActivity.this,RecordActivity.class);
//        intent.putExtra(RecordActivity.STATE,"new");
//        intent.putExtra(RecordActivity.PHOTO,"f");
//        startActivity(intent);
//    }
    ///////////////////////////////////////////////////////////////

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radio_month:
                    onClickRadioMonth();
                    break;
                case R.id.radio_var:
                    onClickRadioVar();
                    break;
                default:
                    break;
            }
        }
    };

    public void updateTopCount(){
        List<String> list = Statistic.getTopCount(dateStart,dateEnd);
        String s = "";
        for(String ss:list)
            s +=ss+"\n";
        topCount.setText(s);
        //tv.setText(String.valueOf(list.size()));
    }

    public void updateTopTime(){
        List<String> list = Statistic.getTopTime(dateStart,dateEnd);
        String s = "";
        for(String ss:list)
            s +=ss+"\n";
        topTime.setText(s);
    }

    public void updateTopSum(){
        List<Integer> cat = new ArrayList<>();
        String query;
        Cursor cursor;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        for(CheckBox check:checkBox){
            if(check.isChecked()) {
                query = "SELECT _id FROM Category where name='" + check.getText() + "'";
                cursor = db.rawQuery(query, null);
                while (cursor.moveToNext()) {
                    cat.add(cursor.getInt(0));
                }
                cursor.close();
            }
        }

        String sum = Statistic.getSumTime(cat,dateStart,dateEnd);
        topSum.setText(sum);
    }

    public void onClickRadioMonth(){
        datePickerStart.setEnabled(false);
        datePickerEnd.setEnabled(false);
        buttonStatistic.setEnabled(false);

        String today = getTodayDate();
        int month = Integer.valueOf(today.substring(3, 5));
        int day = Integer.valueOf(today.substring(0, 2));
        int year = Integer.valueOf(today.substring(6));
        dateStart = "01."+month+"."+year;
        dateEnd = "31."+month+"."+year;

        updateTopCount();
        updateTopTime();
        updateTopSum();
        printGraphic();
    }

    public void onClickRadioVar(){
        datePickerStart.setEnabled(true);
        datePickerEnd.setEnabled(true);
        buttonStatistic.setEnabled(true);
    }

    public static String getTodayDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }

    public void onClickStatistic(View view){
        String month,day,year;

        if (String.valueOf(datePickerStart.getMonth() + 1).length() == 1)
            month = "0" + String.valueOf(datePickerStart.getMonth() + 1);
        else month = String.valueOf(datePickerStart.getMonth() + 1);
        if (String.valueOf(datePickerStart.getDayOfMonth()).length() == 1)
            day = "0" + String.valueOf(datePickerStart.getDayOfMonth());
        else day = String.valueOf(datePickerStart.getDayOfMonth());
        year = String.valueOf(datePickerStart.getYear());
        dateStart = String.valueOf(day + "." + month + "." + year);

        if (String.valueOf(datePickerEnd.getMonth() + 1).length() == 1)
            month = "0" + String.valueOf(datePickerEnd.getMonth() + 1);
        else month = String.valueOf(datePickerEnd.getMonth() + 1);
        if (String.valueOf(datePickerEnd.getDayOfMonth()).length() == 1)
            day = "0" + String.valueOf(datePickerEnd.getDayOfMonth());
        else day = String.valueOf(datePickerEnd.getDayOfMonth());
        year = String.valueOf(datePickerEnd.getYear());
        dateEnd = String.valueOf(day + "." + month + "." + year);

        if(compareDate(dateStart,dateEnd)==1)
            Toast.makeText(context, "Отрезок времени указан неверно!", Toast.LENGTH_LONG).show();
        else{
            updateTopCount();
            updateTopTime();
            updateTopSum();
            printGraphic();
        }
    }

    public int compareDate(String d1, String d2){
        //d1>d2 - 1
        //d1=d2 - 0
        //d1<d2 - -1
        int month1 = Integer.valueOf(d1.substring(3, 5));
        int day1 = Integer.valueOf(d1.substring(0, 2));
        int year1 = Integer.valueOf(d1.substring(6));
        int month2 = Integer.valueOf(d2.substring(3, 5));
        int day2 = Integer.valueOf(d2.substring(0, 2));
        int year2 = Integer.valueOf(d2.substring(6));

        if(year1>year2) return 1;
        else if(year1<year2) return -1;
        else if(month1>month2) return 1;
        else if(month1<month2) return -1;
        else if(day1>day2) return 1;
        else if(day1<day2) return -1;
        else return 0;
    }


    ////////////////////////////////////////////////////////////////////

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent(MainActivity.this,RecordActivity.class);
//        intent.putExtra(RecordActivity.STATE,"new");
//        intent.putExtra(RecordActivity.PHOTO,"f");
//        startActivity(intent);
//
//        return true;
//    }


}
