package ca.taoxie.findmytrails;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpPageActivity extends AppCompatActivity {
    String[] items;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ListView listView;
    EditText editText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
        listView = (ListView) findViewById(R.id.listview);
        editText = (EditText) findViewById(R.id.txtsearch);


        items = new String[]{"How do I find a trail?", "How do I rate a trail?", "How do I find the address?", "How do I sign out?"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtitem, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListClickHandler());



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    initList();
                } else {
                    //perform search
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();




    }

    public void initList() {
        items = new String[]{"How do I find a trail?", "How do I rate a trail?", "How do I find the address?", "How do I sign out?"};
        listItems = new ArrayList<>(Arrays.asList(items));
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtitem, listItems);
        listView.setAdapter(adapter);
    }

    public void searchItem(String textToSearch) {
        if(listItems!=null) {
            for (String item : items) {
                if (!item.contains(textToSearch)) {
                    listItems.remove(item);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

   //for onclick in listview
    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tv = (TextView) view.findViewById(R.id.txtitem);
            if(tv.getText().equals("How do I find a trail?")) {


                if (findViewById(R.id.FindTrail_A).getVisibility() != View.VISIBLE) {
                    ((TextView) findViewById(R.id.FindTrail_A)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) findViewById(R.id.FindTrail_A)).setVisibility(View.INVISIBLE);
                }
            }
            if(tv.getText().equals("How do I rate a trail?")){
                if (findViewById(R.id.Rate_TrailA).getVisibility() != View.VISIBLE) {
                    ((TextView) findViewById(R.id.Rate_TrailA)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) findViewById(R.id.Rate_TrailA)).setVisibility(View.INVISIBLE);
                }
            }

            if(tv.getText().equals("How do I find the address?")){
                if (findViewById(R.id.Find_addressA).getVisibility() != View.VISIBLE) {
                    ((TextView) findViewById(R.id.Find_addressA)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) findViewById(R.id.Find_addressA)).setVisibility(View.INVISIBLE);
                }
            }

            if (tv.getText().equals("How do I sign out?")){
                if(findViewById(R.id.Sign_outA).getVisibility() != View.VISIBLE){
                    ((TextView) findViewById(R.id.Sign_outA)).setVisibility(View.VISIBLE);
                }
                else {
                    ((TextView) findViewById(R.id.Sign_outA)).setVisibility(View.INVISIBLE);
                }
            }
        }

    }




        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("HelpPage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

   