package com.example.onboardweighingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.onboardweighingapp.api.GoogleSheetsAPI;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onboardweighingapp.databinding.ActivityMainBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private TableLayout tableLayout;
    private Handler handler = new Handler(); // Create a handler instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView textView = findViewById(R.id.refreshBtn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set a delay of 1 second (1000 milliseconds)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearOverviewData(); // Clear the overview data
                        clearTableData(); // Clear the table data
                        fetchData(); // Fetch new data
                    }
                }, 1000); // Delay duration
            }
        });

        ImageView refreshButton = findViewById(R.id.imageView); // Ensure this is the correct ID
        tableLayout = findViewById(R.id.tableLayout);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set a delay of 1 second (1000 milliseconds)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearOverviewData(); // Clear the overview data
                        clearTableData(); // Clear the table data
                        fetchData(); // Fetch new data
                    }
                }, 1000); // Delay duration
            }
        });


        fetchData(); // Initial data fetch

    }


    private void fetchData() {
        GoogleSheetsAPI api = new GoogleSheetsAPI();

        // Fetch Onboard Weighing Scale overview
        // Fetch Onboard Weighing Scale overview
        api.fetchData(new GoogleSheetsAPI.DataCallback() {
            @Override
            public void onSuccess(String data) {
                Log.d("MainActivity", "Fetched Data: " + data);
                runOnUiThread(() -> setDataToOverview(data));
            }

            @Override
            public void onFailure(IOException e) {
                Log.e("MainActivity", "Failed to fetch data overview", e);
            }
        }, true);

        // Fetch Onboard Weighing Scale data logger
        api = new GoogleSheetsAPI();
        api.fetchData(new GoogleSheetsAPI.DataCallback() {
            @Override
            public void onSuccess(String data) {
                Log.d("MainActivity", "Fetched Data: " + data);
                runOnUiThread(() -> parseJsonAndUpdateUI(data));
            }

            @Override
            public void onFailure(IOException e) {
                Log.e("MainActivity", "Failed to fetch data logger", e);
            }
        }, false);
    }

    private void setDataToOverview(String jsonData) {

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        try {
            JsonElement jsonElement = JsonParser.parseString(jsonData);
            JsonArray valuesArray = jsonElement.getAsJsonObject().getAsJsonArray("values");

            // Check if valuesArray is not null and has at least one element
            if (valuesArray != null && valuesArray.size() > 0) {
                // Start from index 0 since there's only one data row
                for (int i = 0; i < valuesArray.size(); i++) {
                    JsonArray rowArray = valuesArray.get(i).getAsJsonArray();
                    // Ensure rowArray has enough elements before accessing them
                    if (rowArray.size() >= 5) {
                        // Fetch TextViews outside of the loop for better performance
                        TextView asOfDate = findViewById(R.id.asOfDate);
                        TextView date = findViewById(R.id.date);
                        TextView time = findViewById(R.id.time);
                        TextView weight = findViewById(R.id.weight);
                        TextView switchs = findViewById(R.id.switchs);

                        // Set text values from the rowArray
                        asOfDate.setText(currentDate);
                        date.setText(rowArray.get(0).getAsString()); // Consider if this should be different
                        time.setText(rowArray.get(1).getAsString());
                        weight.setText(rowArray.get(3).getAsString());
                        switchs.setText(rowArray.get(4).getAsString());
                    }
                }
            }


        } catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON data", e);
        }

    }


    private void parseJsonAndUpdateUI(String jsonData) {
        try {

            JsonElement jsonElement = JsonParser.parseString(jsonData);
            JsonArray valuesArray = jsonElement.getAsJsonObject().getAsJsonArray("values");

            if (valuesArray != null && valuesArray.size() > 0) {
                // Clear existing rows
                tableLayout.removeViews(1, tableLayout.getChildCount() - 1); // Keep header

                List<DataRow> dataRows = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                for (int i = 1; i < valuesArray.size(); i++) { // Start from i = 1 to skip the header row
                    JsonArray rowArray = valuesArray.get(i).getAsJsonArray();
                    if (rowArray.size() >= 5) {
                        DataRow dataRow = new DataRow(
                                rowArray.get(0).getAsString(), // Date
                                rowArray.get(1).getAsString(), // Time
                                rowArray.get(3).getAsString(), // Weight
                                rowArray.get(4).getAsString()  // Switch Status
                        );

                        if(dataRow.getDate().contains("Date")) {
                            continue;
                        }
                        dataRows.add(dataRow);
                    }
                }

                    // Sort dataRows by date in descending order
                    Collections.sort(dataRows, new Comparator<DataRow>() {
                        @Override
                        public int compare(DataRow row1, DataRow row2) {
                            try {
                                Date date1 = dateFormat.parse(row1.getDate());
                                Date date2 = dateFormat.parse(row2.getDate());
                                return date2.compareTo(date1); // Descending order
                            } catch (ParseException e) {
                                Log.e("MainActivity", "Date parsing error", e);
                                return 0; // If there's an error, consider them equal
                            }
                        }
                    });

                    for (int i = 0; i < dataRows.size(); i++) {
                        DataRow row = dataRows.get(i);
                        TableRow tableRow = new TableRow(this);
                        tableRow.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        // Alternate row colors
                        if (i % 2 == 0) {
                            tableRow.setBackgroundColor(Color.parseColor("#f0f0f0")); // Light gray for even rows
                        } else {
                            tableRow.setBackgroundColor(Color.WHITE); // White for odd rows
                        }

                        // Add TextViews to the row
                        addTextViewToRow(tableRow, row.getDate(), 1f, 0);
                        addTextViewToRow(tableRow, row.getTime(), 1f, 1);
                        addTextViewToRow(tableRow, row.getWeight(), 1f, 2);
                        addTextViewToRow(tableRow, row.getSwitchStatus(), 1f, 3);

                        tableLayout.addView(tableRow);
                    }
            } else {
                Log.e("MainActivity", "No data found in valuesArray.");
            }

        }  catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON data logger", e);
        }
    }

    private void addTextViewToRow(TableRow tableRow, String text, float weight, int index) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setBackgroundResource(R.drawable.border); // Set border background

        // Set padding for the first column (date column)
        if (index == 0) { // For column 0
            textView.setPadding(20, 10, 10, 10); // Adjust left padding as needed
        } else {
            textView.setPadding(20, 10, 10, 10); // Standard padding for other columns
        }

        // Create LayoutParams
        TableRow.LayoutParams params;
        params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, // Wrap content for column 0
                TableRow.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params);
        tableRow.addView(textView);
    }

    private void clearTableData() {
        // Clear all rows from the table except for the header
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
    }


    private void clearOverviewData() {
        // Clear the overview data by resetting the TextViews
        TextView asOfDate = findViewById(R.id.asOfDate);
        TextView date = findViewById(R.id.date);
        TextView time = findViewById(R.id.time);
        TextView weight = findViewById(R.id.weight);
        TextView switchs = findViewById(R.id.switchs);

        // Reset TextViews to empty or default values
        asOfDate.setText("");
        date.setText("");
        time.setText("");
        weight.setText("");
        switchs.setText("");
    }

}