package specialneeds.speech;

import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.text.DateFormat;
import  java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import specialneeds.speech.Email.EmailActivity;
import specialneeds.speech.MakeCall.MakeAcall;

public class MainActivity extends AppCompatActivity implements OnInitListener {
    Button button_dial;
    Button button_contact;
    Button button_music;
    Button button_date;
    Button button_email;
    Button button_call;


    boolean audio, double_click, flush;
    SharedPreferences Pref;
    int flag_prev = 0, flag_curr = 0;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        addListenerOnButton();
    }

    private void addListenerOnButton() {
        final Context context = this;

        //Pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this); //to check preferences
        //audio = Pref.getBoolean("pref_audio", true);
        audio=true;
        //double_click = Pref.getBoolean("pref_double_click", true);
        double_click=true;
        //flush = Pref.getBoolean("pref_flush", true);
        flush=true;

        if(double_click && audio){

            button_dial = (Button) findViewById(R.id.dialPadButt);
            button_dial.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    flag_curr=1;
                    if(flag_prev != flag_curr)
                    {
                        say(button_dial.getText().toString());
                        flag_prev = flag_curr;
                    }
                    else
                    {
                        say("Showing Dial pad");
                        Intent intent = new Intent (context, DialPad.class);
                        startActivity(intent);
                    }
                }
            });
            button_contact= (Button) findViewById(R.id.button2);
            button_contact.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    flag_curr=2;
                    if(flag_prev != flag_curr)
                    {
                        say(button_contact.getText().toString());
                        flag_prev = flag_curr;
                    }
                    else
                    {
                        say("Showing Contact List");
                        Intent intent = new Intent (context, ContacList.class);
                        startActivity(intent);
                    }
                }
            });
            button_music= (Button) findViewById(R.id.playMusicButt);
            button_music.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    flag_curr=3;
                    if(flag_prev != flag_curr)
                    {
                        say(button_music.getText().toString());
                        flag_prev = flag_curr;
                    }
                    else
                    {
                        say("Showing music player");
                        Intent intent = new Intent (context, MusicPlayer.class);
                        startActivity(intent);
                    }
                }
            });

            button_email=(Button)findViewById(R.id.sendEmailBt);
            button_email.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  say("Showing send Email Screen");
                    flag_curr=4;
                    if(flag_prev != flag_curr)
                    {
                        say(button_email.getText().toString());
                        flag_prev = flag_curr;
                    }
                    else
                    {
                        say("Showing send Email Screen");
                        Intent intent = new Intent (context, EmailActivity.class);
                        startActivity(intent);
                    }

                }
            });

            button_call=(Button)findViewById(R.id.makeCall) ;
            button_call.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    flag_curr=5;
                    if(flag_prev != flag_curr)
                    {
                        say(button_call.getText().toString());
                        flag_prev = flag_curr;
                    }
                    else
                    {
                        say("Showing call Screen");
                        Intent intent = new Intent (context, MakeAcall.class);
                        startActivity(intent);
                    }


                }
            });



            button_date = (Button) findViewById(R.id.Date);
        	button_date.setOnClickListener(new OnClickListener() {
        		@Override
        		public void onClick(View arg0) {

        				String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());



                    say(currentDateTimeString);


        		}
        	});



        }
        else{


        }

    }





    public void say(String text2say){
        if(audio)     //work only if audio is on
        {
            if(flush)
            {
                tts.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                tts.speak(text2say, TextToSpeech.QUEUE_ADD, null);
            }
            while (tts.isSpeaking())
            {}
        }

    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void onDestroy() {
        say("Application Closed");


        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        addListenerOnButton();
    }

}
