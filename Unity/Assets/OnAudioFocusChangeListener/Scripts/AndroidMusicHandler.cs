using UnityEngine;
using System.Collections;
using System;

namespace OnAudioFocusChangedListener.Android
{
	//#if UNITY_ANDROID && !UNITY_EDITOR
	public class AndroidMusicHandler : MonoBehaviour {

		#region delegate

		// forwards UnitySendMessages from AudioListener to delegates
		private static GameObject androidMessageReceiver;

		public delegate void MusicPlayStateChanged(string state);

		public static Action<string> OnMusicPlayStateChanged;

		#endregion

		#region native android 

		private static AndroidJavaClass _androidAudioListener = null;

		private static AndroidJavaClass AndroidAudioListener
		{
			get
			{
				return _androidAudioListener ?? (_androidAudioListener = new AndroidJavaClass("net.kibotu.audiolistener.AudioListener"));
			}
		}
		
		private static AndroidJavaObject _activity;
		public static AndroidJavaObject AndroidActivity {
			get {
				return _activity ?? (_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity"));
			}
		}

		private static bool IsAndroidMusicPlaying ()
		{	
			return AndroidAudioListener.CallStatic<bool> ("IsAndroidMusicPlaying", AndroidActivity);
		} 

		private static void RegisterUnityAndroidCallbackListener ()
		{
			if (androidMessageReceiver != null) 
				return;

			androidMessageReceiver = new GameObject("AndroidMusicHandler");
			androidMessageReceiver.AddComponent<AndroidMusicHandler>();

			AndroidAudioListener.CallStatic ("RegisterUnityAndroidCallbackListener", AndroidActivity);
		}

		private static void UnregisterUnityAndroidCallbackListener ()
		{
			if (androidMessageReceiver != null) {
				Destroy(androidMessageReceiver);
			}

			AndroidAudioListener.CallStatic ("UnregisterUnityAndroidCallbackListener", AndroidActivity);
		}

		#endregion

		#region public api

		public static bool IsMusicActive {
			get { 
				if (Application.platform == RuntimePlatform.Android) {
					return IsAndroidMusicPlaying ();
				}
				return false;
			}
		}

		public static void RegisterAndroidCallbackListener (Action<string> MusicPlayStateChanged) 
		{
			if (Application.platform == RuntimePlatform.Android) {
				OnMusicPlayStateChanged += MusicPlayStateChanged;
				RegisterUnityAndroidCallbackListener();
			}
		}

		public static void UnregisterAndroidCallbackListener() 
		{
			if (Application.platform == RuntimePlatform.Android) {
				UnregisterUnityAndroidCallbackListener();
			}
		}

		/// <summary>
		/// 
		/// Called on the listener to notify it the audio focus for this listener has been changed. 
		/// The focusChange value indicates whether the focus was gained, whether the focus was lost, 
		/// and whether that loss is transient, or whether the new focus holder will hold it for an 
		/// unknown amount of time. When losing focus, listeners can use the focus change information 
		/// to decide what behavior to adopt when losing focus. A music player could for instance elect 
		/// to lower the volume of its music stream (duck) for transient focus losses, and pause otherwise.
		/// 
		/// Used to indicate a gain of audio focus, or a request of audio focus, of unknown duration.
		/// AUDIOFOCUS_GAIN = 1
		/// 
		/// Used to indicate a loss of audio focus of unknown duration.
		/// AUDIOFOCUS_LOSS = -1
		/// 
		/// Used to indicate a transient loss of audio focus.
		/// AUDIOFOCUS_LOSS_TRANSIENT = -2
		/// 
		/// Used to indicate a transient loss of audio focus where the loser of the audio focus can
		/// lower its output volume if it wants to continue playing (also referred to as "ducking"), as 
		/// the new focus owner doesn't require others to be silent.
		/// AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3
		/// 
		/// </summary>
		/// <param name="state">State.</param>
		public void OnChangedState(string state)
		{
			Debug.Log ("OnChangedState state: " + state);

			if (OnMusicPlayStateChanged != null)
			{
				OnMusicPlayStateChanged(state);
			}
		}

		#endregion
		
		public void OnDestroy() {
			UnregisterUnityAndroidCallbackListener ();
		}
	}
	//#endif
}