package com.diev.salarymaster.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.diev.salarymaster.Model.Business;
import com.diev.salarymaster.Model.TimeWork;
import com.diev.salarymaster.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fragment_Statictis extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Fragment_Statictis() {
        // Required empty public constructor
    }

    public static Fragment_Statictis newInstance(String param1, String param2) {
        Fragment_Statictis fragment = new Fragment_Statictis();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static final String SHARED_PRE = "shared_pre";
    public static final String uuid = "uuid";
    private String userId;
    private PieChart piechart;
    private BarChart barchart;
    private ArrayList<TimeWork> dataTimeWork = new ArrayList<>();
    private ArrayList<Business> dataBusiness = new ArrayList<>();

    Spinner monthSpinner;

    Spinner yearSpinner_piechart;
    Spinner yearSpinner_barchart;
    ProgressBar progressBar_load_piechart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statictis, container, false);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        setControl(view);
        getDataBusiness();
        setEvent();

        return view;
    }

    private void setControl(View view) {
        piechart = view.findViewById(R.id.piechart);
        barchart = view.findViewById(R.id.barchart);
        yearSpinner_piechart = view.findViewById(R.id.yearSpinner);
        yearSpinner_barchart = view.findViewById(R.id.yearSpinner_barchart);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        progressBar_load_piechart = view.findViewById(R.id.progressBar_load_piechart);

        // PieChart setup
        piechart.setUsePercentValues(false);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);
        piechart.setDragDecelerationFrictionCoef(0.95f);
        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);
        piechart.setDrawCenterText(true);
        piechart.setRotationAngle(0);
        piechart.setRotationEnabled(true);
        piechart.setHighlightPerTapEnabled(true);
    }

    private void setEvent() {
        setDataDate();

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Get selected month and year
                String selectedMonth = adapterView.getItemAtPosition(position).toString();
                String selectedYear = yearSpinner_piechart.getSelectedItem().toString();
                setDataPieChart(selectedMonth + "/" + selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where nothing is selected
            }
        });

        yearSpinner_piechart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Get selected month and year
                String selectedYear = adapterView.getItemAtPosition(position).toString();
                String selectedMonth = monthSpinner.getSelectedItem().toString();
                setDataPieChart(selectedMonth + "/" + selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where nothing is selected
            }
        });
        yearSpinner_barchart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Get selected month and year
                String selectedYear = adapterView.getItemAtPosition(position).toString();
                setDataBarChart(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where nothing is selected
            }
        });
    }

    private String getCurrentMonthYear() {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();

        // Format ngày theo MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");

        // Trả về tháng và năm hiện tại dưới dạng chuỗi
        return dateFormat.format(calendar.getTime());
    }

    private void setDataDate() {
        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng được đánh số từ 0

        // Danh sách các tháng và năm
        List<String> monthList = new ArrayList<>();
        List<String> yearList = new ArrayList<>();

        // Tạo danh sách tháng từ tháng 1 đến tháng 12
        for (int month = 1; month <= 12; month++) {
            String monthString = String.format(Locale.US, "%02d", month); // Format tháng
            monthList.add(monthString);
        }

        // Tạo danh sách năm từ năm 2023 đến năm hiện tại
        for (int year = 2023; year <= currentYear; year++) {
            yearList.add(String.valueOf(year));
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Chọn tháng hiện tại
        monthSpinner.setSelection(currentMonth - 1);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner_piechart.setAdapter(yearAdapter);
        yearSpinner_barchart.setAdapter(yearAdapter);

        // Chọn năm hiện tại
        yearSpinner_piechart.setSelection(yearList.indexOf(String.valueOf(currentYear)));
        yearSpinner_barchart.setSelection(yearList.indexOf(String.valueOf(currentYear)));
    }


    private void getDataTimework(String date) {
        progressBar_load_piechart.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TimeWork").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataTimeWork.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TimeWork timeWork = dataSnapshot.getValue(TimeWork.class);
                    if (timeWork != null) {
                        dataTimeWork.add(timeWork);
                    }
                }
                setDataPieChart(date);
                setDataBarChart(date.split("/")[1]); // Pass only the year for the bar chart
                progressBar_load_piechart.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public static Map<String, Double> filterAndCalculate(List<TimeWork> timeWorks, String monthYear) {
        Map<String, Double> businessWageMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (TimeWork timeWork : timeWorks) {
            try {
                String date = timeWork.getDate();
                if (date != null) {
                    // Check if the date falls within the specified month
                    SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
                    String monthYearOfWork = monthYearFormat.format(sdf.parse(date));
                    if (monthYear.equals(monthYearOfWork)) {
                        String business = timeWork.getBusiness();
                        double totalHours = timeWork.getTotal();
                        double wagePerHour = timeWork.getWage();
                        double currentTotalWage = businessWageMap.getOrDefault(business, 0.0);
                        // Calculate total wage and add to the current total
                        double calculatedWage = totalHours * wagePerHour;
                        businessWageMap.put(business, currentTotalWage + calculatedWage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return businessWageMap;
    }

    public static Map<Integer, Double> filterByMonth(List<TimeWork> timeWorks, String year) {
        Map<Integer, Double> monthWageMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Initialize map with all months set to 0
        for (int month = 1; month <= 12; month++) {
            monthWageMap.put(month, 0.0);
        }

        for (TimeWork timeWork : timeWorks) {
            try {
                String date = timeWork.getDate();
                if (date != null) {
                    // Kiểm tra xem ngày có nằm trong năm được chỉ định không
                    SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
                    String monthYearOfWork = monthYearFormat.format(sdf.parse(date));
                    if (monthYearOfWork.endsWith(year)) {
                        int month = Integer.parseInt(monthYearOfWork.substring(0, 2)); // Get the month part as an integer
                        double total = timeWork.getTotal();
                        double wage = timeWork.getWage();
                        double currentTotalWage = monthWageMap.getOrDefault(month, 0.0);
                        monthWageMap.put(month, currentTotalWage + (total * wage));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return monthWageMap;
    }


    private void getDataBusiness() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Business").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Business business = dataSnapshot.getValue(Business.class);
                    if (business != null) {
                        dataBusiness.add(business);
                    }
                }
                getDataTimework(getCurrentMonthYear());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setDataBarChart(String year) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        Map<Integer, Double> result = filterByMonth(dataTimeWork, year);

        // Convert result to BarEntries
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            int month = entry.getKey();
            float value = entry.getValue().floatValue();
            barEntries.add(new BarEntry(month - 1, value)); // BarEntry month is zero-based
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Total Wage per Month");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);
        barchart.setData(barData);
        barchart.getDescription().setEnabled(false);
        barchart.animateX(1000);
        barchart.invalidate();
    }

    private void setDataPieChart(String monthYear) {
        Map<String, Double> result = filterAndCalculate(dataTimeWork, monthYear);

        if (result.isEmpty()) {
            // If there's no data, show "No Data"
            ArrayList<PieEntry> noDataEntry = new ArrayList<>();
            noDataEntry.add(new PieEntry(1f, "No Data"));

            PieDataSet pieDataSet = new PieDataSet(noDataEntry, "");
            pieDataSet.setColors(Color.GRAY);
            PieData pieData = new PieData(pieDataSet);
            piechart.setData(pieData);
            piechart.setCenterText("No Data");
            piechart.setCenterTextColor(Color.BLACK);
            piechart.animateY(1000);
            piechart.invalidate();
        } else {
            // If there's data, show the pie chart
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                String businessName = "";
                for (Business business : dataBusiness) {
                    if (business.getId().equals(entry.getKey())) {
                        businessName = business.getName();
                    }
                }
                double totalWage = entry.getValue();
                pieEntries.add(new PieEntry((float) totalWage, businessName)); // Use actual business names
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData pieData = new PieData(pieDataSet);
            piechart.setData(pieData);
            piechart.setCenterText("");
            piechart.animateY(1000);
            piechart.invalidate();
        }
    }

}
