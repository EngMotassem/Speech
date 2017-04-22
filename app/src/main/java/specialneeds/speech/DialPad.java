package specialneeds.speech;
/*
this class for showing voice dial pad
 */

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DialPad extends AppCompatActivity implements TextToSpeech.OnInitListener {

    EditText mEditor;
    TextToSpeech tts;
    String clicked;
    int flagdial = 0;
    int flagcall = 0;
    boolean audio, double_click, flush;
    SharedPreferences Pref;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_pad);
        mEditor = (EditText) findViewById(R.id.EditTextPhoneNumber);
        tts = new TextToSpeech(this, this);
        //say("On Dialer Pad. Press Buttons twice to Enter.");
        /**
         * A call-back for when the user presses the number buttons.
         */
        Pref = PreferenceManager.getDefaultSharedPreferences(DialPad.this); //to check preferences
        audio = Pref.getBoolean("pref_audio", true);
        double_click = Pref.getBoolean("pref_double_click", true);
        flush = Pref.getBoolean("pref_flush", true);
        View.OnClickListener mDialPadListener = new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button) v;
                String buttonText = b.getText().toString();
                if (double_click && audio) {
                    if (flagdial == 0 && buttonText.equalsIgnoreCase("Back Space")) {
                        flagcall = 0;
                        if (mEditor.getText().toString().length() > 0) {
                            clicked = buttonText;
                            say(buttonText);
                            flagdial = 1;
                        } else {
                            say("Back Space. No Digit to be removed");
                        }
                    } else if (flagdial == 1 && buttonText.equalsIgnoreCase("Back Space") && clicked.equals(buttonText)) {
                        StringBuffer previousNumber = new StringBuffer(mEditor.getText().toString());
                        say(previousNumber.charAt(previousNumber.length() - 1) + " removed");
                        mEditor.setText(previousNumber.deleteCharAt(previousNumber.length() - 1));
                        flagdial = 0;
                    } else if (flagdial == 0 || clicked.equals(buttonText) == false) {
                        flagcall = 0;
                        clicked = buttonText;
                        say(buttonText);
                        flagdial = 1;
                    } else if (flagdial == 1 && clicked.equalsIgnoreCase(buttonText)) {
                        StringBuffer previousNumber = new StringBuffer(mEditor.getText().toString());
                        CharSequence phoneDigit = ((Button) v).getText();
                        mEditor.setText(previousNumber.append(phoneDigit));
                        say(buttonText + " entered");
                        flagdial = 0;
                    }
                } else {
                    if (buttonText.equalsIgnoreCase("Back Space")) {
                        flagcall = 0;
                        if (mEditor.getText().toString().length() > 0) {
                            StringBuffer previousNumber = new StringBuffer(mEditor.getText().toString());
                            say(previousNumber.charAt(previousNumber.length() - 1) + " removed");
                            mEditor.setText(previousNumber.deleteCharAt(previousNumber.length() - 1));
                        } else {
                            say("Back Space. No Digit to be removed");
                        }
                    } else {
                        StringBuffer previousNumber = new StringBuffer(mEditor.getText().toString());
                        CharSequence phoneDigit = ((Button) v).getText();
                        mEditor.setText(previousNumber.append(phoneDigit));
                        say(buttonText + " entered");

                    }
                }
            }
        };
        /**
         * 	A call-back for when the user presses the call button.
         */

        View.OnClickListener mPhoneCallListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (double_click && audio) {
                    if (mEditor.getText().toString().length() > 0) {
                        flagdial = 0;
                        if (flagcall == 0) {
                            say("Press again to call " + mEditor.getText().toString());
                            flagcall = 1;
                        } else if (flagcall == 1) {
                            flagcall = 0;
                            say("Calling");
                            call(mEditor.getText().toString());
                        }
                    } else {
                        say("No number entered to call");
                    }
                } else {
                    if (mEditor.getText().toString().length() > 0) {

                        say("Calling " + mEditor.getText().toString());
                        call(mEditor.getText().toString());

                    } else {
                        say("No number entered to call");
                    }
                }
            }
        };

        // 	Hook up button presses to the appropriate event handler.

        ((Button) findViewById(R.id.button1)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button2)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button3)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button4)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button5)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button6)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button7)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button8)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button9)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button10)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button11)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button12)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button14)).setOnClickListener(mDialPadListener);
        ((Button) findViewById(R.id.button13)).setOnClickListener(mPhoneCallListener);

        mEditor.setOnTouchListener(new View.OnTouchListener() {    //to disable open of keypad on touch of text box

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return true;
            }
        });

    }

    private void call(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("dialing-example", "Call failed", activityException);
        }
    }
    public void say(String text2say){
        if(audio)
        {
            if(flush)
            {
                tts.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                tts.speak(text2say, TextToSpeech.QUEUE_ADD, null);
            }
        }

    }

    @Override
    public void onInit(int status) {

        //say("Hello World");

    }

    @Override
    public void onDestroy() {
        say("Showing Main menu");
		/*if (tts != null) {
			tts.stop();
			tts.shutdown();
		}*/

        super.onDestroy();
    }
}