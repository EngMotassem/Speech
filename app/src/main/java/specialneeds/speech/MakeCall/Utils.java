package specialneeds.speech.MakeCall;

import android.content.Context;
import android.widget.Toast;

// make messages

public class Utils {

    public static void showToast(Context context, String message){
        Toast.makeText(context , message, Toast.LENGTH_LONG).show();
    }

    public static void print(String message){
        System.out.println(message);
    }
}