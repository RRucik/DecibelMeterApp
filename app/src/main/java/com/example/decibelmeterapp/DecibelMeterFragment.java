package com.example.decibelmeterapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import de.nitri.gauge.Gauge;


public class DecibelMeterFragment extends Fragment
{
    TextView decibelTextView;
    TextView decibelMinTextView;
    TextView decibelMaxTextView;
    TextView messageTextView;
    CustomMediaRecorder customMediaRecorder;
    CountDownTimer startTimer;
    CountDownTimer decibelTimer;
    Gauge gauge;
    LineChart chart;
    Thread thread;
    Typeface typeface;
    boolean threadOn = true;
    boolean initializationComplete = false;
    private int decibels;
    private int minDecibels;
    private int maxDecibels;
    String[] decibelListTitles;
    public static final int PERMISSION_ALL = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        decibels = 0;
        minDecibels = 100;
        maxDecibels = 0;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_decibel_meter, container, false);

        decibelTextView = view.findViewById(R.id.soundLoudnessTextView);
        decibelMinTextView = view.findViewById(R.id.soundLoudnessMinTextView);
        decibelMaxTextView = view.findViewById(R.id.soundLoudnessMaxTextView);
        messageTextView = view.findViewById(R.id.messageTextView);

        Resources res = getResources();
        decibelListTitles = res.getStringArray(R.array.decibel_titles);

        gauge = view.findViewById(R.id.gauge);
        gauge.setDeltaTimeInterval(1);

        typeface = ResourcesCompat.getFont(getActivity(), R.font.digital);

        chart = view.findViewById(R.id.chart);
        setChartSettings();

        if(checkPermission())
        {
            startRecordingAndDisplaying();
        }
        return view;
    }

    public void startRecordingAndDisplaying()
    {
        customMediaRecorder = new CustomMediaRecorder();
        customMediaRecorder.initializeRecorder();

        LineData data = new LineData();
        chart.setData(data);
        initializeThread();
        addFirstEntries();
        checkIfInitializationComplete();
        initializeAndStartDecibelTimer();
    }

    // Start other counters after this one is finished - it repairs wrong min val when starting fragment
    public void checkIfInitializationComplete()
    {
        startTimer = new CountDownTimer(1000, 100)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {}
            public void onFinish()
            {
                initializationComplete = true;
            }
        }.start();
    }

    public void initializeAndStartDecibelTimer()
    {
        decibelTimer = new CountDownTimer(Long.MAX_VALUE, 100)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                if(initializationComplete)
                {
                    loadAndShowDecibels();
                }
            }
            public void onFinish()
            {}
        };
        decibelTimer.start();
    }

    public void loadAndShowDecibels()
    {
        decibels = customMediaRecorder.getDecibels();
        if(decibels >= 0)
        {
            addEntry(decibels);
            gauge.moveToValue(decibels);

            if(threadOn)
            {
                displayDecibels();
                updateAndDisplayMinDecibels();
                updateAndDisplayMaxDecibels();
                setMessage();
                threadOn = false;
            }
        }
    }

    public void displayDecibels()
    {
        decibelTextView.setText(decibels + " dB");
    }

    public void updateAndDisplayMinDecibels()
    {
        if (decibels < minDecibels)
        {
            minDecibels = decibels;
            decibelMinTextView.setText(minDecibels + " dB");
        }
    }

    public void updateAndDisplayMaxDecibels()
    {
        if (decibels > maxDecibels)
        {
            maxDecibels = decibels;
            decibelMaxTextView.setText(maxDecibels + " dB");
        }
    }

    public void setMessage()
    {
        int position = (int) (decibels/10.0);
        messageTextView.setText(decibelListTitles[position]);
    }

    private void setChartSettings()
    {
        // Turn off description
        chart.getDescription().setEnabled(false);

        // Disable touch
        chart.setTouchEnabled(false);


        // Turn off legend
        chart.getLegend().setEnabled(false);

        // Turning off X and right Y axis
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        // Setting up left Y axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(100);
        leftAxis.setXOffset(10f);
        leftAxis.setTypeface(typeface);
        leftAxis.setTextSize(24);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);

        // Turning off borders
        chart.setDrawBorders(false);
    }

    private void addFirstEntries()
    {
        for(int i = 0; i < 30; i++)
        {
            addEntry(40);
        }
    }

    private void addEntry(int decibels)
    {
        LineData data = chart.getData();

        if (data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null)
            {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), decibels), 0);
            data.notifyDataChanged();

            // Notify chart about data change
            chart.notifyDataSetChanged();

            // Limit number of visible entries
            chart.setVisibleXRangeMaximum(30);

            // Move to the latest entry
            chart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.02f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawFilled(true);
        set.setDrawCircles(false);
        set.setColor(Color.rgb(16, 165, 245));
        set.setFillColor(Color.rgb(16, 165, 245));
        set.setFillAlpha(100);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        return set;
    }

    public boolean checkPermission()
    {
        int RECORD_AUDIO_PERMISSION = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        int WRITE_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ArrayList<String> PERMISSION_LIST = new ArrayList<>();
        if((RECORD_AUDIO_PERMISSION != PackageManager.PERMISSION_GRANTED))
        {
            PERMISSION_LIST.add(Manifest.permission.RECORD_AUDIO);
        }
        if((WRITE_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED))
        {
            PERMISSION_LIST.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!PERMISSION_LIST.isEmpty())
        {
            requestPermissions(PERMISSION_LIST.toArray(new String[PERMISSION_LIST.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean record = false;
        boolean storage =  false;
        switch (requestCode)
        {
            case  PERMISSION_ALL:
            {
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < permissions.length; i++)
                    {
                        if (permissions[i].equals(Manifest.permission.RECORD_AUDIO))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            {
                                record = true;
                            }
                            else
                            {
                                Toast.makeText(getActivity().getApplicationContext(), "Please allow Microphone permission", Toast.LENGTH_LONG).show();
                            }
                        }
                        else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                storage = true;
                            }
                            else
                            {
                                Toast.makeText(getActivity().getApplicationContext(), "Please allow Storage permission", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                if (record && storage)
                {
                    startRecordingAndDisplaying();
                }
            }
        }
    }

    private void initializeThread()
    {

        if (thread != null)
        {
            thread.interrupt();
        }
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    threadOn = true;
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (thread != null)
        {
            thread.interrupt();
        }

        if(startTimer != null)
        {
            startTimer.cancel();
        }

        if(decibelTimer != null)
        {
            decibelTimer.cancel();
        }

        if(customMediaRecorder != null)
        {
            customMediaRecorder.onStop();
        }

        initializationComplete = false;
    }
}