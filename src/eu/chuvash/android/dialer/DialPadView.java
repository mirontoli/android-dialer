package eu.chuvash.android.dialer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TableLayout;

public class DialPadView extends TableLayout {
	//private static final String TAG = "DialPadView";
	private DialPadSound dpSound = null;

	public DialPadView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// it loads asyncroniously
		//initDpSound();

		// http://stackoverflow.com/questions/974680/android-onkeydown-problem
		this.requestFocus();
		// is already set in main.xml: android:focusableInTouchMode="true";
		// this.setFocusableInTouchMode(true);

		// http://developer.android.com/guide/topics/ui/custom-components.html#compound
		// http://maohao.wordpress.com/2009/08/27/building-mix-up-custom-component-android/
		// http://stackoverflow.com/questions/1476371/android-writing-a-custom-compound-component
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dialpad, this);
	}
	public void initDpSound() {
		dpSound = DialPadSound.getInstance(getContext());
	}
	public void renewDpSound() {
		dpSound = DialPadSound.getNewInstance(getContext());
	}
	public void finalizeDpSound() {
		dpSound = null;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		// TODO fix the ALT and SHIFT combinations
		// 
		// this hint I got from Mathias at Mid Sweden University
		// KeyCharacterMap map = KeyCharacterMap.load(event.getDeviceId());
		// int c = map.get(keyCode, altPressed ? KeyEvent.META_ALT_ON : 0);
		// altPressed = event.isAltPressed();
		ImageButton button = getButton(keyCode);
		if (button != null) {
			button.setSelected(true);
			button.setPressed(true);
			((DialPadActivity) getContext())
					.putNumberToEditText(button.getId());
			playSoundEffect(button.getId());
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		super.onKeyUp(keyCode, event);
		ImageButton button = getButton(keyCode);
		if (button != null) {
			button.setSelected(false);
			button.setPressed(false);
			return true;
		}
		return false;
	}

	@Override
	public void playSoundEffect(int buttonId) {
		if (dpSound != null) {
			switch (buttonId) {
			case R.id.dial_button0:
				dpSound.play(DialPadSound.ZERO);
				break;
			case R.id.dial_button1:
				dpSound.play(DialPadSound.ONE);
				break;
			case R.id.dial_button2:
				dpSound.play(DialPadSound.TWO);
				break;
			case R.id.dial_button3:
				dpSound.play(DialPadSound.THREE);
				break;
			case R.id.dial_button4:
				dpSound.play(DialPadSound.FOUR);
				break;
			case R.id.dial_button5:
				dpSound.play(DialPadSound.FIVE);
				break;
			case R.id.dial_button6:
				dpSound.play(DialPadSound.SIX);
				break;
			case R.id.dial_button7:
				dpSound.play(DialPadSound.SEVEN);
				break;
			case R.id.dial_button8:
				dpSound.play(DialPadSound.EIGHT);
				break;
			case R.id.dial_button9:
				dpSound.play(DialPadSound.NINE);
				break;
			case R.id.dial_button_s:
				dpSound.play(DialPadSound.STAR);
				break;
			case R.id.dial_button_p:
				dpSound.play(DialPadSound.POUND);
			}
		}
	}

	public ImageButton getButton(int keyCode) {
		ImageButton button = null;
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
			button = (ImageButton) findViewById(R.id.dial_button0);
			break;
		case KeyEvent.KEYCODE_1:
			button = (ImageButton) findViewById(R.id.dial_button1);
			break;
		case KeyEvent.KEYCODE_2:
			button = (ImageButton) findViewById(R.id.dial_button2);
			break;
		case KeyEvent.KEYCODE_3:
			button = (ImageButton) findViewById(R.id.dial_button3);
			break;
		case KeyEvent.KEYCODE_4:
			button = (ImageButton) findViewById(R.id.dial_button4);
			break;
		case KeyEvent.KEYCODE_5:
			button = (ImageButton) findViewById(R.id.dial_button5);
			break;
		case KeyEvent.KEYCODE_6:
			button = (ImageButton) findViewById(R.id.dial_button6);
			break;
		case KeyEvent.KEYCODE_7:
			button = (ImageButton) findViewById(R.id.dial_button7);
			break;
		case KeyEvent.KEYCODE_8:
			button = (ImageButton) findViewById(R.id.dial_button8);
			break;
		case KeyEvent.KEYCODE_9:
			button = (ImageButton) findViewById(R.id.dial_button9);
			break;
		case KeyEvent.KEYCODE_STAR:
			button = (ImageButton) findViewById(R.id.dial_button_s);
			break;
		case KeyEvent.KEYCODE_POUND:
			button = (ImageButton) findViewById(R.id.dial_button_p);
		}
		return button;
	}
}
