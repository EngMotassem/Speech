package specialneeds.speech.MakeCall;

import android.Manifest;
import android.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.TextView;

import java.util.ArrayList;

import specialneeds.speech.ContacList;
import specialneeds.speech.DialPad;
import specialneeds.speech.Email.EmailActivity;
import specialneeds.speech.MusicPlayer;
import specialneeds.speech.R;

/*
Strater activity that
 */

public class MakeAcall extends Activity {

    // define commands for call , message,email ,dial,music

    public static final String CALL_COMMAND = "call";
    public static final String MESSAGE_COMMAND = "message";
    public static  final String EMAIL_COMMAMD="ma";
    public static  final String LIST_COMMAND="cont";
    public static  final String Dial_COMMAND="ad";
    public static  final String Music_COMMAND="music";



    private static final int REQUEST_CODE = 1234;
    TextView mMatchedSpeechItems;
    ArrayList<String> matches_text;

    String currentCommand = null;
    String subCommandName = null;
    String subCommandText = null;
// vibration
    Vibrator v1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_make_acall);

    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    protected void onStart() {
        super.onStart();
       mMatchedSpeechItems = (TextView) findViewById(R.id.textview);

        startSpeak(" welcome please speak. for making a call " +
                "say call and for sending message " +
                "say send message" +
                "for email say email" +
                "for music say music " +
                " for contact list "+
                "say contact list"
        );

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mMatchedSpeechItems.setText("Text");
        currentCommand=null;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startSpeak(" application closed");

    }

// start speak function to make respond to user
    public void startSpeak(String message) {
        TexttoSpeech tts = new TexttoSpeech(this, message, new TexttoSpeech.onSpeakComplete() {
            @Override
            public void onComplete() {
               Utils.showToast(getApplicationContext(), "speak completed.");
                startRecognition();
            }
        });
    }
    // recognize user's voice  inputs function to make respond to user
    public void startRecognition() {
// vibration service
        v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v1.vibrate(200);
// check command type
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // start activity based on user input connected with onActivityResult

        startActivityForResult(intent, REQUEST_CODE);

    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

      // action done based on user voice
            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (String s : matches_text) {
                Utils.print("Reading:  " + s);
                mMatchedSpeechItems.setText(mMatchedSpeechItems.getText().toString() + "\n" + s);

                // check if voice command is not null
                if (currentCommand == null) {
                     // call command

                    if (s.toLowerCase().contains(CALL_COMMAND)) {


                        currentCommand = CALL_COMMAND;
                       Utils.showToast(this, "Please say contact name");
                        startSpeak("Please say contact name");
                    } else if (s.toLowerCase().contains(MESSAGE_COMMAND)) {
                        currentCommand = MESSAGE_COMMAND;
                       Utils.showToast(this, "Please say sender name.");
                        startSpeak("Please say sender name");
                    }
                    // if Email command goto email activity


                    else if (s.toLowerCase().contains(EMAIL_COMMAMD)) {
                        currentCommand = EMAIL_COMMAMD;

                        Intent emailIntent=new Intent(MakeAcall.this, EmailActivity.class);
                        startActivity(emailIntent);
                        break;
                    }
                    // if Dial command goto DialPad activity
                    else if (s.toLowerCase().contains(Dial_COMMAND)) {
                        currentCommand = Dial_COMMAND;

                        Intent emailIntent=new Intent(MakeAcall.this, DialPad.class);
                        startActivity(emailIntent);
                        break;
                    }

                    // if music command goto MusicPlayer activity
                    else if (s.toLowerCase().contains(Music_COMMAND)) {
                        currentCommand = Music_COMMAND;
                        //Utils.showToast(this, "Please say sender name.");
                        //startSpeak("Please say sender name");
                        Intent emailIntent=new Intent(MakeAcall.this, MusicPlayer.class);
                        startActivity(emailIntent);
                        break;
                    }
                    // if list  command goto ContacList activity

                    else if (s.toLowerCase().contains(LIST_COMMAND)) {
                        currentCommand = LIST_COMMAND;

                        Intent emailIntent=new Intent(MakeAcall.this, ContacList.class);
                        startActivity(emailIntent);
                        break;
                    }

                    else {
                        startRecognition();
                        currentCommand = null;
                    }

                    break;
                }
                else if (currentCommand.equalsIgnoreCase(CALL_COMMAND)) {

                    makeCall(s);
                    break;
                }
                else if (currentCommand.equalsIgnoreCase(MESSAGE_COMMAND)) {
                    sendMessage(s);
                    break;
                }



                else if (currentCommand.equalsIgnoreCase(Music_COMMAND)) {

                    Intent emailIntent=new Intent(MakeAcall.this, MusicPlayer.class);
                    startActivity(emailIntent);
                    break;
                }
                else if (currentCommand.equalsIgnoreCase(Dial_COMMAND)) {

                    Intent emailIntent=new Intent(MakeAcall.this, DialPad.class);
                    startActivity(emailIntent);
                    break;
                }
                else if (currentCommand.equalsIgnoreCase(EMAIL_COMMAMD)) {

                    Intent emailIntent=new Intent(MakeAcall.this, EmailActivity.class);
                    startActivity(emailIntent);
                    break;
                }
                else if (currentCommand.equalsIgnoreCase(LIST_COMMAND)) {

                    Intent emailIntent=new Intent(MakeAcall.this, ContacList.class);
                    startActivity(emailIntent);
                    break;
                }

            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // send message  function
    public void sendMessage(String name) {
        Utils.print("send messagexxxx: " + name);
        if (subCommandName != null) {
            Utils.print("sending message....");
            startSpeak("Sending message");
          v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
           v1.vibrate(1000);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(subCommandName, null, name, null, null);
            subCommandName = null;
        } else {

            String phone = CommandActions.fetchContacts(this, name);
            if (phone != null) {
               Utils.showToast(this, "Got number... Say message body...");
                startSpeak("Please say message body..");
                subCommandName = phone;
            } else {
//                startRecognition();
                Utils.showToast(this, "Phone number not found. Try again, make sure your are in silent place.");
                startSpeak("Phone number not found. Try again, make sure your are in silent place.");

            }
        }
    }
// make call function
    public void makeCall(String name) {
        Utils.print("Searching to call: " + name);
        String phone = CommandActions.fetchContacts(this, name);
        if (phone != null) {

           // Utils.showToast(this, "Got number... calling...");
            startSpeak("Calling");
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
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
            startActivity(intent);
          v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v1.vibrate(1000);


        }else{
            Utils.showToast(this, "Phone number not found. Try again, make sure your are in silent place.");
            startSpeak("Phone number not found. Try again, make sure your are in silent place.");

        }
    }
}