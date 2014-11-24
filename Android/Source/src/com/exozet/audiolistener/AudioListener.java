package com.exozet.audiolistener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import com.unity3d.player.UnityPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.media.AudioManager.OnAudioFocusChangeListener;

@TargetApi(Build.VERSION_CODES.FROYO)
public class AudioListener {

    /**
     * Unhide android api: check is stream is active now (AudioManager.STREAM_RING, AudioManager.STREAM_NOTIFICATION...),
     * uses reflection
     *
     * @param audioStream
     * @return
     */
    public static boolean isStreamActive(int audioStream) {
        Class<?> audioSystemClazz = null;
        Boolean res = false;
        try {
            audioSystemClazz = Class.forName("android.media.AudioSystem");
            if (null != audioSystemClazz) {
                // isStreamActive
                Method method = audioSystemClazz.getDeclaredMethod("isStreamActive", new Class<?>[]{int.class, int.class});
                if (null != method) {
                    res = (Boolean) method.invoke(null, audioStream, 0);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static boolean IsAndroidMusicPlaying(final Activity context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive();
    }

    // region OnAudioFocusChangeListener

    private static OnAudioFocusChangeListener audioFocusChangeListener;

    public static void RegisterUnityAndroidCallbackListener(final Activity context) {

        if (audioFocusChangeListener != null) {
            Log.w("AudioManager", "RegisterUnityAndroidCallbackListener: OnAudioFocusChangeListener already assigned.");
            return;
        }

        // create listener
        audioFocusChangeListener = new OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {
                UnityPlayer.UnitySendMessage("AndroidMusicHandler", "OnChangedState", String.valueOf(focusChange));
            }
        };

        // assign listener
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).requestAudioFocus(audioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }

    public static void UnregisterUnityAndroidCallbackListener(final Activity context) {

        if (audioFocusChangeListener == null) {
            Log.w("AudioManager", "UnregisterUnityAndroidCallbackListener: OnAudioFocusChangeListener not yet assigned.");
            return;
        }

        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).abandonAudioFocus(audioFocusChangeListener);
    }

    // endregion
}