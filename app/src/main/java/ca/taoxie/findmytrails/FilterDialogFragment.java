package ca.taoxie.findmytrails;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by TaoX on 2016-10-15.
 */

public class FilterDialogFragment extends DialogFragment {

    private TextView filter_greetings;

    private SwitchCompat swt_all;
    private SwitchCompat swt_hiking;
    private SwitchCompat swt_mountain;
    private SwitchCompat swt_camp;
    private SwitchCompat swt_cur;
    private EditText city_edit;

    private TextView range_textView;
    private SeekBar range_seekBar;

    private FancyButton cancel_button;
    private FancyButton search_button;

    private static final int initialRange = 10;

    public FilterDialogFragment() {

    }

    protected static FilterDialogFragment newInstance() {
        FilterDialogFragment f = new FilterDialogFragment();
        Bundle args = new Bundle();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.filter_fragment_dialog, container, false);
        //filter_greetings = (TextView) v.findViewById(R.id.filter_greetings);
        search_button = (FancyButton) v.findViewById(R.id.search_button);

        city_edit = (EditText)v.findViewById(R.id.city_editText);
        swt_all = (SwitchCompat)v.findViewById(R.id.switch_all);
        swt_all.setChecked (true);
        swt_cur = (SwitchCompat)v.findViewById(R.id.switch_current);
        swt_cur.setChecked (true);
        swt_camp = (SwitchCompat)v.findViewById(R.id.switch_camp);
        swt_camp.setChecked (true);
        swt_mountain = (SwitchCompat)v.findViewById(R.id.switch_mountain);
        swt_mountain.setChecked (true);
        swt_hiking = (SwitchCompat)v.findViewById(R.id.switch_hiking);
        swt_hiking.setChecked (true);
        swt_cur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            city_edit.setEnabled(true);

                        }else{
                            city_edit.setEnabled(false);
                        }
            }
        });
        swt_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {

                    case R.id.switch_all:

                        if(!isChecked){

                            swt_camp.setChecked(false);
                            swt_mountain.setChecked(false);
                            swt_hiking.setChecked(false);
                        }else{

                            swt_camp.setChecked(true);
                            swt_mountain.setChecked(true);
                            swt_hiking.setChecked(true);

                        }
                        break;

                    default:
                        break;
                }
            }
        });




        range_textView = (TextView) v.findViewById(R.id.range_textView);
        range_seekBar = (SeekBar) v.findViewById(R.id.range_seekBar);
        range_seekBar.setProgress(initialRange);
        range_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range_textView.setText(getResources().getString(R.string.range_within) + " " + progress + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        range_textView.setText(getResources().getString(R.string.range_within) + " " + initialRange + " Miles");

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> urls = urlGenerator();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.updateMap(urls);
                dismiss();
            }
        });
        cancel_button = (FancyButton) v.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    private List<String> urlGenerator() {
        int range = range_seekBar.getProgress();
        MainActivity mainActivity = (MainActivity) getActivity();
        Location location=null;
        Address dest;
        double lat,lon;
        lat = 39.995128;
        lon = -75.238648;
        if (swt_cur.isChecked()) {
            location = mainActivity.getLastKnownLocation();
            if(location!=null){
                lat = location.getLatitude();
                lon = location.getLongitude();
            }

        }
        else{
            String city = city_edit.getText().toString();
            Geocoder geocoder = new Geocoder(mainActivity);
            try {
                dest =geocoder.getFromLocationName(city, 1).get(0);
                lat = dest.getLatitude();
                lon = dest.getLongitude();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        List<String> urls = new ArrayList<>();

        if (swt_all.isChecked()) {
            String url = "https://trailapi-trailapi.p.mashape.com/?lat=" +
                    lat + "&lon=" + lon + "&radius=" + range;
            urls.add(url);
            return urls;
        }
        if (swt_hiking.isChecked()) {
            String url = "https://trailapi-trailapi.p.mashape.com/?lat=" +
                    lat + "&lon=" + lon +
                    "&q[activities_activity_type_name_eq]=hiking" + "&radius=" + range;
            urls.add(url);
        }
        if (swt_mountain.isChecked()) {
            String url = "https://trailapi-trailapi.p.mashape.com/?lat=" +
                    lat + "&lon=" + lon +
                    "&q[activities_activity_type_name_eq]=mountain+biking" + "&radius=" + range;
            urls.add(url);
        }
        if (swt_camp.isChecked()) {
            String url = "https://trailapi-trailapi.p.mashape.com/?lat=" +
                    lat + "&lon=" + lon +
                    "&q[activities_activity_type_name_eq]=camping" + "&radius=" + range;
            urls.add(url);
        }


        return urls;
    }


}
