using UnityEngine;
using System.Collections;
using UnityEngine.UI;

namespace OnAudioFocusChangedListener.Android 
{
	public class Test : MonoBehaviour {

		public GameObject Content;
		public GameObject Prefab;
		public AudioSource Music;

		public void StartListening() 
		{
			Debug.Log("RegisterAndroidCallbackListener");
			AddLog ("StartListening");
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
		}

		public void StopListening() 
		{
			Debug.Log("UnregisterAndroidCallbackListener");
			AddLog ("StopListening");
			AndroidMusicHandler.UnregisterAndroidCallbackListener ();
		}

		public void IsMusicOn() 
		{
			var text = (AndroidMusicHandler.IsMusicActive ? "Music is on." : "Music is off.") + "\n";
			Debug.Log("AndroidMusicHandler.IsMusicActive " + text);
			AddLog (text);
		}

		public void AddLog(string msg) {
			var t = Instantiate(Prefab) as GameObject;
			t.GetComponent<Text>().text = msg;
			t.transform.SetParent(Content.transform, false);
		}

		public void StartMusic() {
			AddLog ("Start Music");
			Music.Play ();
		}

		public void StopMusic() {
			AddLog ("Stop Music");
			Music.Stop ();
		}

		public void StartFMOD() {
			AddLog ("Start FMOD");
			AndroidMusicHandler.EnableFMOD ();
		}
		
		public void StopFMOD() {
			AddLog ("Stop FMOD");
			AndroidMusicHandler.DisableFMOD ();
		}
	}
}