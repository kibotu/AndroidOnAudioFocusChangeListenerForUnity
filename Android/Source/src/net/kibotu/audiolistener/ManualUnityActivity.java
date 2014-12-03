package net.kibotu.audiolistener;

import android.os.Bundle;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;
import org.jetbrains.annotations.Nullable;

public class ManualUnityActivity extends UnityPlayerNativeActivity {

    public static UnityPlayer unityPlayer;

    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make mUnityPlayer accessible in static context
        unityPlayer = mUnityPlayer;
    }
}