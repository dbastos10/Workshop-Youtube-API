package com.examples.workshopyoutube;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimplePlayer_Frag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimplePlayer_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimplePlayer_Frag extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private Button playVideoButton;
    private Button playPlaylistButton;
    private Button playVideoListButton;

    private EditText startIndexEditText;
    private EditText startTimeEditText;
    private CheckBox autoplayCheckBox;
    private CheckBox lightboxModeCheckBox;

    private static final String VIDEO_ID = "oAtjf6Ijmtw";
    private static final String PLAYLIST_ID =  "AGizmgReKcw&list=PLnjdmugHJD8C5reZ3Zl0MlpDlmeo1CuAO";
    private static final ArrayList<String> VIDEO_IDS = new ArrayList<String>(Arrays.asList(
            new String[] {"Wfql_DoHRKc", "eisKxhjBnZ0", "dVDk7PXNXB8"}));

    public static SimplePlayer_Frag newInstance() {
        SimplePlayer_Frag fragment = new SimplePlayer_Frag();

        return fragment;
    }

    public SimplePlayer_Frag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_simple_player_, container, false);

        playVideoButton = (Button) v.findViewById(R.id.start_video_button);
        playPlaylistButton = (Button) v.findViewById(R.id.start_playlist_button);
        playVideoListButton = (Button) v.findViewById(R.id.start_video_list_button);
        startIndexEditText = (EditText) v.findViewById(R.id.start_index_text);
        startTimeEditText = (EditText) v.findViewById(R.id.start_time_text);
        autoplayCheckBox = (CheckBox) v.findViewById(R.id.autoplay_checkbox);
        lightboxModeCheckBox = (CheckBox) v.findViewById(R.id.lightbox_checkbox);

        playVideoButton.setOnClickListener(this);
        playPlaylistButton.setOnClickListener(this);
        playVideoListButton.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        int startIndex = parseInt(startIndexEditText.getText().toString(), 0);
        int startTimeMillis = parseInt(startTimeEditText.getText().toString(), 0) * 1000;
        boolean autoplay = autoplayCheckBox.isChecked();
        boolean lightboxMode = lightboxModeCheckBox.isChecked();

        Intent intent = null;
        if (v == playVideoButton) {
            intent = YouTubeStandalonePlayer.createVideoIntent(
                    getActivity(), DeveloperKey.DEVELOPER_KEY, VIDEO_ID, startTimeMillis, autoplay, lightboxMode);
        } else if (v == playPlaylistButton) {
            intent = YouTubeStandalonePlayer.createPlaylistIntent(getActivity(), DeveloperKey.DEVELOPER_KEY,
                    PLAYLIST_ID, startIndex, startTimeMillis, autoplay, lightboxMode);
        } else if (v == playVideoListButton) {
            intent = YouTubeStandalonePlayer.createVideosIntent(getActivity(), DeveloperKey.DEVELOPER_KEY,
                    VIDEO_IDS, startIndex, startTimeMillis, autoplay, lightboxMode);
        }

        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(getActivity(), REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private int parseInt(String text, int defaultValue) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return defaultValue;
    }
}
