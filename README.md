Android OnAudioFocusChangeListener for Unity
============================================

### Introduction

Android plugin library that informs unity about audio focus changes. It also has a reliable way of telling if other applications are currently playing music within unity.

![Screenshot](https://raw.githubusercontent.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/master/Screenshot.png)

### Why use this library?

Because otherwise unity's fmod implementation will screw up android's [AudioManager.html#isMusicActive()](http://goo.gl/Aavbhr) and forcing it to lie and it always returns true, even if no music is being played on the device at all.

### Why is this cool?

Now you can enhance the usability for the end-user by muting your application music automatically when the user listens to music in the background while using your app and more importantly automatically turn the music back on, once mp3 player in the background has finished playing.

### Ready to use asset with demo scene:

[OnAudioFocusChangeListener.unitypackage](https://github.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/blob/master/OnAudioFocusChangeListener.unitypackage?raw=true)

### Ready to use lib:

[OnAudioFocusChangeListener.jar](https://github.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/blob/master/OnAudioFocusChangeListener.jar?raw=true)

### How to install

Whether you directly import the [OnAudioFocusChangeListener.unitypackage](https://github.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/blob/master/OnAudioFocusChangeListener.unitypackage?raw=true) asset bundle or you add the files directly:

For the direct approach you will need the 3 files:

1) [Assets/Plugins/Android/OnAudioFocusChangeListener.jar](https://raw.githubusercontent.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/master/Unity/Assets/Plugins/Android/OnAudioFocusChangeListener.jar)

2) [Assets/Plugins/Android/AndroidManifest.xml](https://raw.githubusercontent.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/master/Unity/Assets/Plugins/Android/AndroidManifest.xml)

3) [AndroidMusicHandler.cs](https://raw.githubusercontent.com/kibotu/AndroidOnAudioFocusChangeListenerForUnity/master/Unity/Assets/OnAudioFocusChangeListener/AndroidMusicHandler.cs)

### How to use

(Note: Have a look at the demo scene.)

Find out if music is playing:

    bool musicIsActive = AndroidMusicHandler.IsMusicActive;

Register a callback listener:

    AndroidMusicHandler.RegisterAndroidCallbackListener ((state) => {

        var stateText = "";

        switch(int.Parse(state)) {
            case 1:  stateText = "AUDIOFOCUS_GAIN"; break;
            case -1:  stateText = "AUDIOFOCUS_LOSS"; break;
            case -2:  stateText = "AUDIOFOCUS_LOSS_TRANSIENT"; break;
            case -3:  stateText = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"; break;
            default: stateText = state; break;
        }
        Debug.Log(stateText);
        AddLog (stateText);
    });

And finally unregister the callback when you're done.

    AndroidMusicHandler.UnregisterAndroidCallbackListener ();

Enjoy. :)

### Known issues:

- AndroidMusicHandler.isMusicActive blocks main thread for at least 150ms to start being reliable while unity is active, is there any other way? please let me know! :(

### Made and tested for Unity3D 4.6.03f

### Contact
* [Jan Rabe](mailto:janrabe@kibotu.net)
