package com.radenmas.smartpowermeter.ui;

import android.text.format.DateFormat;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.smartpowermeter.DataChart;
import com.radenmas.smartpowermeter.R;
import com.radenmas.smartpowermeter.base.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class VoltFrag extends BaseFragment {

    LineChart chart;
    LineDataSet lineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;

    @Override
    protected int getLayoutResource() {
        return R.layout.frag_chart;
    }

    @Override
    protected void myCodeHere(View view) {
        initView(view);

        Graph(500);

        chart.getDescription().setEnabled(false);
        chart.setNoDataText(getString(R.string.voltage));
        chart.setNoDataTextColor(getResources().getColor(R.color.dark_icon));
        chart.invalidate();
    }

    private void initView(View view) {
        chart = view.findViewById(R.id.chart);
    }

    public void Graph(final int limit) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("SmartEnergyMeter");
        Query query = databaseReference.orderByKey().limitToLast(limit);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> data = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        DataChart dataChart = child.getValue(DataChart.class);
                        data.add(new Entry(dataChart.getTime(), dataChart.getVolt()));
                    }
                    showChart(data);
                    lineDataSet.setDrawCircles(false);
                }
                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showChart(ArrayList<Entry> data) {
        lineDataSet.setValues(data);
        lineDataSet.setLabel("DataSet 1");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setDrawValues(false);
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextSize(10f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);

        xAxis.setLabelRotationAngle(0f);//45
        xAxis.setLabelCount(7, true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long longtime = (long) value;
                Calendar cal = Calendar.getInstance(Locale.getDefault());
                cal.setTimeInMillis(longtime * 1000);
                String s = DateFormat.format("HH:mm", cal).toString();
                return s;
            }
        });

        YAxis yAxisL = chart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisL.setDrawGridLines(true);
        yAxisL.setDrawLabels(true);
        yAxisL.setAxisMinimum(180);
        yAxisL.setAxisMaximum(260);

        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.notifyDataSetChanged();
        chart.clear();
        chart.setData(lineData);
        chart.invalidate();
        chart.moveViewTo(lineData.getEntryCount(), 50L, YAxis.AxisDependency.LEFT);
    }
}