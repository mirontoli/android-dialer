package eu.chuvash.android.dialer;

import java.io.File;
import java.util.HashMap;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * @author Anatoly Mironov 
 * 
 * This class is a class which handles sounds for the
 *         dialpad. Due the limitations of MediaPlayer (max 8 mp3) this class
 *         will use SoundPool to handle media files. Inspired by:
 *         http://www.anddev
 *         .org/using_soundpool_instead_of_mediaplayer-t3115.html and:
 *         http://groups
 *         .google.com/group/android-developers/msg/6accbd96bf87eb8e
 * 
 */
public class DialPadSound {

	private static final String TAG = "DialPadSound";
	// sound int
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int NINE = 9;
	public static final int STAR = 10;
	public static final int POUND = 11;

	private String directory;
	private final boolean useSDcard = true;
	// in order to use sound from package
	// put sound files into /res/raw directory
	// and uncomment the else state in addAllSounds()
	private Context context;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	private int volume;
	protected boolean released = false;

	// singleton pattern
	private static DialPadSound INSTANCE;

	public static DialPadSound getInstance(Context context) {
		if (INSTANCE == null || INSTANCE.released) {
			INSTANCE = new DialPadSound(context);
		}
		return INSTANCE;
	}
	public static DialPadSound getNewInstance(Context context) {
		INSTANCE = new DialPadSound(context);
		return INSTANCE;
	}
	private DialPadSound(Context context) {
		this.context = context;
		int numberOfSounds = 12;
		this.soundPool = new SoundPool(numberOfSounds,
				AudioManager.STREAM_MUSIC, 40);
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		soundPoolMap = new HashMap<Integer, Integer>();
		
		addAllSounds();
	}

	private void addAllSounds() {
		if (useSDcard) {
			addSound(DialPadSound.ZERO, "zero.mp3");
			addSound(DialPadSound.ONE, "one.mp3");
			addSound(DialPadSound.TWO, "two.mp3");
			addSound(DialPadSound.THREE, "three.mp3");

			addSound(DialPadSound.FOUR, "four.mp3");
			addSound(DialPadSound.FIVE, "five.mp3");
			addSound(DialPadSound.SIX, "six.mp3");
			addSound(DialPadSound.SEVEN, "seven.mp3");

			addSound(DialPadSound.EIGHT, "eight.mp3");
			addSound(DialPadSound.NINE, "nine.mp3");
			addSound(DialPadSound.STAR, "star.mp3");
			addSound(DialPadSound.POUND, "pound.mp3");
		} else {
//			addSound(DialPadSound.ZERO, R.raw.zero);
//			addSound(DialPadSound.ONE, R.raw.one);
//			addSound(DialPadSound.TWO, R.raw.two);
//			addSound(DialPadSound.THREE, R.raw.three);
//
//			addSound(DialPadSound.FOUR, R.raw.four);
//			addSound(DialPadSound.FIVE, R.raw.five);
//			addSound(DialPadSound.SIX, R.raw.six);
//			addSound(DialPadSound.SEVEN, R.raw.seven);
//
//			addSound(DialPadSound.EIGHT, R.raw.eight);
//			addSound(DialPadSound.NINE, R.raw.nine);
//			addSound(DialPadSound.STAR, R.raw.star);
//			addSound(DialPadSound.POUND, R.raw.pound);
		}
	}

//	private void addSound(int SOUND, int resid) {
//		Context context = (DialPadActivity) getContext();
//		int soundId = soundPool.load(context, resid, 1);
//		soundPoolMap.put(SOUND, soundId);
//	}

	private void addSound(int SOUND, String fileName) {
		if (directory == null) {
			directory = SettingsActivity.getPreferredSoundDir(context);
		}
		if (!directory.equals("")) {
			String path = directory + "/" + fileName;
			// check if file exists
			if (new File(path).exists()) {
				int soundId = soundPool.load(path, 1);
				soundPoolMap.put(SOUND, soundId);
			}
		}
	}
	public void play(int SOUND) {
		try {
			int soundId = soundPoolMap.get(SOUND);
			soundPool.play(soundId, volume, volume, 1, 0, 1f);
		} catch (NullPointerException ex) {
			Log.e(TAG, ex.toString());
		}
	}

	public void stop(int SOUND) {
		try {
			soundPool.stop(SOUND);
		} catch (NullPointerException ex) {
			Log.e(TAG, ex.toString());
		}
	}

	public void release() {
		released = true;
		soundPool.release();
	}
}
