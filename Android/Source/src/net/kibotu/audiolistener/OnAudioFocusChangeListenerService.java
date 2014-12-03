package net.kibotu.audiolistener;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import org.fmod.FMODAudioDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class OnAudioFocusChangeListenerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private static AudioManager audioManager;
    private static final String TAG = OnAudioFocusChangeListenerService.class.getSimpleName();
    public static boolean DEBUG = true;
    private static OnAudioFocusChangeListenerService instance;

    // region service

    @Override
    public void onCreate() {
        instance = this;
        Log("Starting OnAudioFocusChangeListener as background service.");
        audioManager = ((AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE));
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        audioManager.abandonAudioFocus(this);
    }

    // endregion

    // region onAudioFocusChange

    public static void RegisterUnityAndroidCallbackListener() {
        Log("RegisterUnityAndroidCallbackListener");
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public static int UnregisterUnityAndroidCallbackListener() {
        Log("UnregisterUnityAndroidCallbackListener");
        return audioManager.abandonAudioFocus(instance);
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {

        if (DEBUG) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log("Audio Focus AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log("Audio Focus AUDIOFOCUS_GAIN");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log("Audio Focus AUDIOFOCUS_LOSS");
                    break;
                default:
                    Log("Audio Focus " + focusChange);
                    break;
            }
        }

        try {
            // inform unity of the changed status
            UnityPlayer.UnitySendMessage("AndroidMusicHandler", "OnChangedState", String.valueOf(focusChange));
        } catch (final Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }
    }

    // endregion

    // region music is active

    public static boolean isMusicActive() {
        return isMusicActive(150L); // arbitrary number, Note: 100ms wasn't enough on Samsung Galaxy S5
    }

    /**
     * Returns if other applications are using the music stream.
     *
     * @param sleepTime - Approximate time for fmod to stop in order to use isMusicActive reliably.
     * @return <code>true</code> if other apps are using the music stream right now.
     */
    public static boolean isMusicActive(final long sleepTime) {
        // 1) stop fmod, so the android audio manager can figure out if other applications are running music
        stopFMOD();

        // 2) sadly there is no proper call back from the FMODAudioDevice class,
        // In theory we could use fmod.isRunning(),
        /*
        while (getFmodAudioDevice().isRunning()) {
            try {
                Thread.currentThread().sleep(sleepTime);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        // but sadly it's not actually telling us when fmod is done consuming the audio stream
        // and leads to always 'music is active'
        // so we just wait it out, potentially unreliable, if you know a better way, please let me know!
        try {
            Log("sleep because fmod is running: " + getFmodAudioDevice().isRunning());
            Thread.currentThread().sleep(sleepTime);
            Log("resume");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log("stopped fmod running=" + getFmodAudioDevice().isRunning());

        // 3) check if music is playing
        boolean isPlaying = ((AudioManager) UnityPlayer.currentActivity.getSystemService(AUDIO_SERVICE)).isMusicActive();

        Log("isMusicActive: " + isPlaying);

        Log("start fmod");
        // 4) resume fmod
        getFmodAudioDevice().start();

        Log("started fmod");

        // 5) return findings
        return isPlaying;
    }

    // endregion

    // region fmod

    public static void startFMOD() {
        FMODAudioDevice fmod = getFmodAudioDevice();
        if (fmod == null) {
            Log("startFMOD: fmod == null");
            return;
        }
        fmod.start();
        Log("Started FMOD. " + fmod.isRunning());
    }

    public static void stopFMOD() {
        final FMODAudioDevice fmod = getFmodAudioDevice();
        if (fmod == null) {
            Log("stopFMOD: fmod == null");
            return;
        }
        fmod.stop();
        Log("Stopped FMOD. " + fmod.isRunning());
    }

    /**
     * Dirty reflection action to get access to fmod.
     * Assuming: private mUnityPlayer.r instanceof FMODAudioDevice
     *
     * @return org.fmod.FMODAudioDevice instance from UnityPlayer
     */
    @Nullable
    private static FMODAudioDevice getFmodAudioDevice() {

        final UnityPlayer unityPlayer = ManualUnityActivity.unityPlayer;
        if (unityPlayer == null) {
            Log("ManualUnityActivity.unityPlayer == null");
            return null;
        }

        try {
            final Field r = UnityPlayer.class.getDeclaredField("r");
            r.setAccessible(true);
            return (FMODAudioDevice) r.get(ManualUnityActivity.unityPlayer);
        } catch (final NoSuchFieldException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    // endregion

    // region log

    public static void Log(@NotNull final String msg) {
        if (DEBUG)
            Log.v(TAG, msg);
    }

    // endregion
}