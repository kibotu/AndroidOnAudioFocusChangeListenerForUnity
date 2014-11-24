using UnityEngine;
using System.Collections;
using UnityEngine.UI;

namespace OnAudioFocusChangedListener.Android 
{
	public class Test : MonoBehaviour {

		public GameObject Content;
		public GameObject prefab;

		public void StartListening() 
		{
			Debug.Log("StartListening");
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
			AddLog ("UnregisterAndroidCallbackListener");
			AndroidMusicHandler.UnregisterAndroidCallbackListener ();
		}

		public void IsMusicOn() 
		{
			var text = (AndroidMusicHandler.IsMusicActive ? "Music is on." : "Music is off.") + "\n";
			Debug.Log("UnregisterAndroidCallbackListener " + text);
			AddLog (text);
		}

		public void AddLog(string msg) {
			var t = Instantiate(prefab) as GameObject;
			t.GetComponent<Text>().text = msg;
			t.transform.SetParent(Content.transform, false);
		}
	}
}