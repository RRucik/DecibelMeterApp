package com.example.decibelmeterapp;

import android.media.MediaRecorder;
import android.util.Log;

public class CustomMediaRecorder
{
    private MediaRecorder mediaRecorder;
    private static final String TAG = "CustomMediaRecorder";
    private double ampl = 18 * Math.exp(-2);

    public void initializeRecorder()
    {
        try
        {
            // Basic MediaRecorder initialization
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.i(TAG, "Initialization success");
            return;
        }
        catch (Exception e)
        {
            // If initialization was unsuccessful due to lack of file, we free our resources in order to reduce battery consumption
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            e.printStackTrace();
        }
        Log.i(TAG, "Initialization failure");
    }

    public void onStop()
    {
        if(mediaRecorder != null)
        {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
    }

    public double getMaxAmplitude()
    {
        if(mediaRecorder != null)
        {
            return mediaRecorder.getMaxAmplitude();
        }
        return 0;
    }

    public int getDecibels()
    {
        if (mediaRecorder != null)
        {
            double amplitude = getMaxAmplitude();
            return Math.round(20 * (float) Math.log10(amplitude/ampl));
        }
        return 0;
    }
}
