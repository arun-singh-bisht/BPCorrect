package com.protechgene.android.bpconnect.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

public class AlarmSound {

    private MediaPlayer mMediaPlayer;
    private static AlarmSound alarmSound;
    private Context context;
    private AlarmSound(Context context)
    {
        this.context = context;
    }
    public static AlarmSound getInstance(Context context)
    {
        if (alarmSound == null)
            alarmSound = new AlarmSound(context);
        return alarmSound;
    }

    public void playSound() {
        Uri alert = getAlarmUri();
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    public void stopSound()
    {
        if(mMediaPlayer!=null)
            mMediaPlayer.stop();
        mMediaPlayer = null;
        alarmSound = null;
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}
