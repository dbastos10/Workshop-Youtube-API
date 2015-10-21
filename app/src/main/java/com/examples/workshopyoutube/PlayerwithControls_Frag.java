package com.examples.workshopyoutube;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


public class PlayerwithControls_Frag extends Fragment implements
        View.OnClickListener,
        TextView.OnEditorActionListener,
        CompoundButton.OnCheckedChangeListener, Spinner.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;

    private static final ListEntry[] ENTRIES = {
            new ListEntry("People Are Awesome", "T6a-X9x8YNw", false),
            new ListEntry("Surface Book", "XVfOe5mFbAE", false),
            new ListEntry("Playlist: All Android Central Videos", "PL525f8ds9RvsdCc-B77MZBG1PgwYemIxt", true)};

    private static final String KEY_CURRENTLY_SELECTED_ID = "currentlySelectedId";

    private YouTubePlayerSupportFragment youTubePlayerView;
    private YouTubePlayer player;
    private ArrayAdapter<ListEntry> videoAdapter;
    private Spinner videoChooser;
    private Button playButton;
    private Button pauseButton;
    private EditText skipTo;
    private RadioGroup styleRadioGroup;
    private int currentlySelectedPosition;
    private String currentlySelectedId;


    public static PlayerwithControls_Frag newInstance() {
        PlayerwithControls_Frag fragment = new PlayerwithControls_Frag();
        return fragment;
    }

    public PlayerwithControls_Frag() {
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
        View v = inflater.inflate(R.layout.fragment_playerwith_controls_, container, false);

        videoChooser = (Spinner) v.findViewById(R.id.video_chooser);
        playButton = (Button) v.findViewById(R.id.play_button);
        pauseButton = (Button) v.findViewById(R.id.pause_button);
        skipTo = (EditText) v.findViewById(R.id.skip_to_text);

        styleRadioGroup = (RadioGroup) v.findViewById(R.id.style_radio_group);
        ((RadioButton) v.findViewById(R.id.style_default)).setOnCheckedChangeListener(this);
        ((RadioButton) v.findViewById(R.id.style_minimal)).setOnCheckedChangeListener(this);
        ((RadioButton) v.findViewById(R.id.style_chromeless)).setOnCheckedChangeListener(this);

        videoAdapter = new ArrayAdapter<ListEntry>(getActivity(), android.R.layout.simple_spinner_item, ENTRIES);
        videoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        videoChooser.setOnItemSelectedListener(this);
        videoChooser.setAdapter(videoAdapter);

        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        skipTo.setOnEditorActionListener(this);

        youTubePlayerView = new YouTubePlayerSupportFragment();
        getFragmentManager().beginTransaction().replace(R.id.youtubeFragment, youTubePlayerView).commit();

        youTubePlayerView.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;

                if (!b) {
                    playVideoAtSelection();
                }
                setControlsEnabled(true);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        setControlsEnabled(false);
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


    // METODOS ADICIONADOS
    private void playVideoAtSelection() {
        ListEntry selectedEntry = videoAdapter.getItem(currentlySelectedPosition);
        if (selectedEntry.id != currentlySelectedId && player != null) {
            currentlySelectedId = selectedEntry.id;
            if (selectedEntry.isPlaylist) {
                player.cuePlaylist(selectedEntry.id);
            } else {
                player.cueVideo(selectedEntry.id);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        currentlySelectedPosition = pos;
        playVideoAtSelection();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

    @Override
    public void onClick(View v) {
        if (v == playButton) {
            player.play();
        } else if (v == pauseButton) {
            player.pause();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == skipTo) {
            int skipToSecs = parseInt(skipTo.getText().toString(), 0);
            player.seekToMillis(skipToSecs * 1000);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(skipTo.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && player != null) {
            switch (buttonView.getId()) {
                case R.id.style_default:
                    player.setPlayerStyle(PlayerStyle.DEFAULT);
                    break;
                case R.id.style_minimal:
                    player.setPlayerStyle(PlayerStyle.MINIMAL);
                    break;
                case R.id.style_chromeless:
                    player.setPlayerStyle(PlayerStyle.CHROMELESS);
                    break;
            }
        }
    }

    private void setControlsEnabled(boolean enabled) {
        playButton.setEnabled(enabled);
        pauseButton.setEnabled(enabled);
        skipTo.setEnabled(enabled);
        videoChooser.setEnabled(enabled);
        for (int i = 0; i < styleRadioGroup.getChildCount(); i++) {
            styleRadioGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    private static final int parseInt(String intString, int defaultValue) {
        try {
            return intString != null ? Integer.valueOf(intString) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static final class ListEntry {
        public final String title;
        public final String id;
        public final boolean isPlaylist;

        public ListEntry(String title, String videoId, boolean isPlaylist) {
            this.title = title;
            this.id = videoId;
            this.isPlaylist = isPlaylist;
        }

        @Override
        public String toString() {
            return title;
        }

    }
}


