package specialneeds.speech;
/*
this class for showing user contact list
 */
import android.Manifest;
import android.app.ExpandableListActivity;
import android.content.ActivityNotFoundException;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContacList extends ExpandableListActivity implements TextToSpeech.OnInitListener {

    int flagcallcontact = 0;
    boolean audio, double_click, flush;
    SharedPreferences Pref;
    String contactclicked;
    TextToSpeech tts;
    private static final String[] CONTACTS_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };
    private static final int GROUP_ID_COLUMN_INDEX = 0;

    private static final String[] PHONE_NUMBER_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private static final int TOKEN_GROUP = 0;
    private static final int TOKEN_CHILD = 1;

    private static final class QueryHandler extends AsyncQueryHandler {
        private CursorTreeAdapter mAdapter;

        public QueryHandler(Context context, CursorTreeAdapter adapter) {
            super(context.getContentResolver());
            this.mAdapter = adapter;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TOKEN_GROUP:
                    mAdapter.setGroupCursor(cursor);
                    break;

                case TOKEN_CHILD:
                    int groupPosition = (Integer) cookie;
                    mAdapter.setChildrenCursor(groupPosition, cursor);
                    break;
            }
        }
    }

    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        // Note that the constructor does not take a Cursor. This is done to avoid querying the
        // database on the main thread.
        public MyExpandableListAdapter(Context context, int groupLayout,
                                       int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                                       int[] childrenTo) {

            super(context, null, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                    childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Given the group, we return a cursor for all the children within that group

            // Return a cursor that points to this contact's phone numbers
            Uri.Builder builder = ContactsContract.Contacts.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, groupCursor.getLong(GROUP_ID_COLUMN_INDEX));
            builder.appendEncodedPath(ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
            Uri phoneNumbersUri = builder.build();

            mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(), phoneNumbersUri,
                    PHONE_NUMBER_PROJECTION, ContactsContract.CommonDataKinds.Phone.MIMETYPE + "=?",
                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}, null);

            return null;
        }
    }

    private QueryHandler mQueryHandler;
    private CursorTreeAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, this);
        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(
                this,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME}, // Name for group layouts
                new int[]{android.R.id.text1},
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, // Number for child layouts
                new int[]{android.R.id.text1});

        setListAdapter(mAdapter);
        final ExpandableListView lv = getExpandableListView();
        //Pref = PreferenceManager.getDefaultSharedPreferences(ContacList.this); //to check preferences
        //audio = Pref.getBoolean("pref_audio", true);
        audio=double_click=flush=true;
        //double_click = Pref.getBoolean("pref_double_click", true);
        //flush = Pref.getBoolean("pref_flush", true);
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPosition, int childPosition, long id) {

                String contact = ((Cursor) mAdapter.getGroup(groupPosition)).getString(1);
                String number = " " + ((TextView) view).getText();
                String text = contact + number;
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
                if (double_click && audio) {
                    if (flagcallcontact == 0 || contactclicked.equals(text) == false) {
                        flagcallcontact = 1;
                        contactclicked = text;
                        say("Press again to call " + contact + " on number " + number);
                    } else if (flagcallcontact == 1 && contactclicked.equals(text)) {
                        flagcallcontact = 0;
                        say("Calling " + contact);
                        call(number);
                    }
                } else {
                    say("Calling " + contact);
                    call(number);
                }
                return false;
            }


        });
        lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                                        @Override
                                        public void onGroupExpand(int groupPosition) {
                                            String text = ((Cursor) mAdapter.getGroup(groupPosition)).getString(1) + " expanded";
                                            // When clicked, show a toast with the TextView text
                                            Toast.makeText(getApplicationContext(), text,
                                                    Toast.LENGTH_SHORT).show();
                                            say(text);

                                        }

                                    }
        );
        lv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                                          @Override
                                          public void onGroupCollapse(int groupPosition) {
                                              String text = ((Cursor) mAdapter.getGroup(groupPosition)).getString(1) + " collapsed";
                                              // When clicked, show a toast with the TextView text
                                              Toast.makeText(getApplicationContext(), text,
                                                      Toast.LENGTH_SHORT).show();
                                              say(text);

                                          }

                                      }
        );

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (scrollState == 0) {
                    int index = lv.getFirstVisiblePosition();
                    String text = ((Cursor) mAdapter.getGroup(index)).getString(1) + " on top of list";
                    // When clicked, show a toast with the TextView text
                    Toast.makeText(getApplicationContext(), text,
                            Toast.LENGTH_SHORT).show();
                    say(text);
                }
            }
        });

        mQueryHandler = new QueryHandler(this, mAdapter);

        // Query for people
        mQueryHandler.startQuery(TOKEN_GROUP, null, ContactsContract.Contacts.CONTENT_URI, CONTACTS_PROJECTION,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, null);
    }

    @Override
    protected void onDestroy() {
        say("Showing Main menu");
        super.onDestroy();


        mAdapter.changeCursor(null);
        mAdapter = null;



    }

    public void say(String text2say) {
        if (audio) {
            if (flush) {
                tts.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                tts.speak(text2say, TextToSpeech.QUEUE_ADD, null);
            }
        }

    }

    @Override
    public void onInit(int status) {


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


}
