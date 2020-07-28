package pe.edu.tecsup.evaluacionfinal.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import pe.edu.tecsup.evaluacionfinal.MainActivity;
import pe.edu.tecsup.evaluacionfinal.R;

public class MeasuresFragment extends Fragment {

    public static final String TEMP = "temperatura";
    public static final String HUM = "humedad";
    public static final double TEMPERATURE_LIMIT_HIGH = 23;
    public static final double TEMPERATURE_LIMIT_LOW = 18;
    public static final double HUMIDITY_LIMIT_LOW = 40;
    public static final double HUMIDITY_LIMIT_HIGH = 55;
    private String temp = "0";
    private String hum;
    private long prevTemp;
    private long prevHum;
    private DatabaseReference tempRef;
    private DatabaseReference humRef;
    private TextView txtTemperature;
    private TextView txtHumidity;
    private ImageView imgLine1;
    private ImageView imgLine2;
    private BarChart barChart;
    private String[] variables = new String[]{TEMP, HUM};
    private Map<String, Float> sensorMeasures = new HashMap<>();
    private List<Float> measures = new ArrayList<>();
    private int[] colors = new int[]{Color.rgb(14, 255, 230), Color.rgb(255, 203, 43)};

    private DatabaseReference databaseReference;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("ON ATTACH");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("ON CREATE FRAGMENT");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tempRef = database.getReference("temp");
        humRef = database.getReference("hum");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measures, container, false);

        System.out.println("ON CREATE VIEW");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("ON VIEW CREATED");

        barChart = view.findViewById(R.id.barChart);
        txtTemperature = view.findViewById(R.id.txtTemperature);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        imgLine1 = view.findViewById(R.id.imgLine1);
        imgLine2 = view.findViewById(R.id.imgLine2);

        // Read temperature from the database
        tempRef.addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("PRIMERA DATA DE FIREBASE");
                System.out.println(dataSnapshot.getValue(Long.class));
                Long value = dataSnapshot.getValue(Long.class);
                System.out.println("TEMP IS NULL? " + temp);
                prevTemp = Long.parseLong(temp == "0" ? String.valueOf(value) : temp);
                temp = String.valueOf(value);
                sensorMeasures.put("temp", Float.parseFloat(temp));
                createChart();

                System.out.println("PREV" + prevTemp);
                System.out.println("CURRENT" + temp);

                txtTemperature.setText(String.valueOf(prevTemp));

                if (value > TEMPERATURE_LIMIT_HIGH || value < TEMPERATURE_LIMIT_LOW ) {
                    txtTemperature.setTextColor(Color.RED);
                } else {
                    txtTemperature.setTextColor(Color.BLACK);
                }

                if (Long.parseLong(temp) > prevTemp) {
                    System.out.println(Long.parseLong(temp));
                    imgLine1.setImageResource(R.drawable.ic_baseline_arrow_upward_60);
                } else if (Long.parseLong(temp) < prevTemp) {
                    imgLine1.setImageResource(R.drawable.ic_baseline_arrow_downward_60);
                } else {
                    imgLine1.setImageResource(R.drawable.ic_baseline_remove_60);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        // Read humidity from the database
        humRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                if (value == null)
                    value = 0L;
                prevHum = Long.parseLong(hum == null ? String.valueOf(value) : hum);
                hum = String.valueOf(value);
                sensorMeasures.put("hum", Float.parseFloat(hum));
                createChart();

                txtHumidity.setText(String.valueOf(prevHum));

                if (value > HUMIDITY_LIMIT_HIGH || value < HUMIDITY_LIMIT_LOW) {
                    txtHumidity.setTextColor(Color.RED);
                } else {
                    txtHumidity.setTextColor(Color.BLACK);
                }

                if (Long.parseLong(hum) > prevHum) {
                    imgLine2.setImageResource(R.drawable.ic_baseline_arrow_upward_60);
                } else if (Long.parseLong(hum) < prevHum) {
                    imgLine2.setImageResource(R.drawable.ic_baseline_arrow_downward_60);
                } else {
                    imgLine2.setImageResource(R.drawable.ic_baseline_remove_60);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    // Grafico de barras
    private Chart getChart(Chart chart, String description, int textColor, int background, int animationY) {
        chart.getDescription().setText(description);
        chart.getDescription().setTextSize(15);
        chart.setBackgroundColor(background);
        chart.animateY(animationY);
        legend(chart);

        return chart;
    }

    private void legend(Chart chart) {
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        ArrayList<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < variables.length; i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = variables[i];
            entries.add(entry);
        }
        legend.setCustom(entries);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<BarEntry> getBarEntries() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        measures.clear();
        measures.add(0, sensorMeasures.get("temp")!= null ? Objects.requireNonNull(sensorMeasures.get("temp")) : 0);
        measures.add(1, sensorMeasures.get("hum") != null ? Objects.requireNonNull(sensorMeasures.get("hum")) : 0);
        for (int i = 0; i < sensorMeasures.values().stream().collect(Collectors.toList()).size(); i++) {
            if (measures.get(i) == 0)
                entries.add(new BarEntry(i, 0));
            entries.add(new BarEntry(i, measures.get(i)));
        }
        return entries;
    }

    private void axisX(XAxis axis) {
        axis.setGranularityEnabled(true);
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setValueFormatter(new IndexAxisValueFormatter(variables));
    }

    private void axisYLeft(YAxis axis) {
        axis.setSpaceTop(90);
        axis.setAxisMinimum(0);
    }

    private void axisYRight(YAxis axis) {
        axis.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createChart() {
        barChart = (BarChart) getChart(barChart, "", Color.BLACK, Color.TRANSPARENT, 800);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(true);
        barChart.setData(getBarChartData());
        barChart.invalidate();

        axisX(barChart.getXAxis());
        axisYLeft(barChart.getAxisLeft());
        axisYRight(barChart.getAxisRight());
    }

    private DataSet getData(DataSet dataSet) {
        dataSet.setColors(colors);
        dataSet.setValueTextSize((Color.WHITE));
        dataSet.setValueTextSize(10);
        return dataSet;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private BarData getBarChartData() {
        BarDataSet barDataSet = (BarDataSet) getData(new BarDataSet(getBarEntries(), ""));
        barDataSet.setBarShadowColor(Color.TRANSPARENT);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.45f);
        return barData;
    }
}