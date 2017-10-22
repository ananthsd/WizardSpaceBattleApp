/* Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dandibhotla.ananth.wizardspacebattleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dandibhotla.ananth.wizardspacebattleapp.BackgroundSoundService.player;
import static com.dandibhotla.ananth.wizardspacebattleapp.Player.colorBG;
import static com.google.example.games.basegameutils.BaseGameUtils.showActivityResultError;

public class MultiplayerActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, RealTimeMessageReceivedListener,
        RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener {
        private long lastRecievedTime=0;
    /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */

    final static String TAG = "Multiplayer";

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;


    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    String mMyId = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages


    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {

            case R.id.button_sign_in:
                // user wants to sign in
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id)) {
                    Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
                }

                // start the sign-in flow
                Log.d(TAG, "Sign-in button clicked");
                mSignInClicked = true;
                mGoogleApiClient.connect();
                break;
            case R.id.button_sign_out:
                // user wants to sign out
                // sign out.
                Log.d(TAG, "Sign-out button clicked");
                mSignInClicked = false;
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                switchToScreen(R.id.screen_sign_in);
                break;
            case R.id.button_invite_players:
                // show list of invitable players
                intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 1);
                switchToScreen(R.id.screen_wait);
                startActivityForResult(intent, RC_SELECT_PLAYERS);
                break;
            case R.id.button_see_invitations:
                // show list of pending invitations
                intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
                switchToScreen(R.id.screen_wait);
                startActivityForResult(intent, RC_INVITATION_INBOX);
                break;
            case R.id.button_accept_popup_invitation:
                // user wants to accept the invitation shown on the invitation popup
                // (the one we got through the OnInvitationReceivedListener).
                acceptInviteToRoom(mIncomingInvitationId);
                mIncomingInvitationId = null;
                break;
            case R.id.button_quick_game:
                // user wants to play against a random opponent right now
                startQuickGame();
                break;

        }
    }

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                handleInvitationInboxResult(responseCode, intent);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (responseCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    try {
                        startGame();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom();
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    showActivityResultError(this, requestCode, responseCode, R.string.signin_other_error);
                }
                break;
        }
        super.onActivityResult(requestCode, responseCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        Log.d(TAG, "**** got onStop");

        // if we're in a room, leave it.
        leaveRoom();

        // stop trying to keep the screen on
        stopKeepingScreenOn();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switchToMainScreen();
        } else {
            switchToScreen(R.id.screen_sign_in);
        }
        super.onStop();
    }

    // Activity just got to the foreground. We switch to the wait screen because we will now
    // go through the sign-in flow (remember that, yes, every time the Activity comes back to the
    // foreground we go through the sign-in flow -- but if the user is already authenticated,
    // this flow simply succeeds and is imperceptible).
    @Override
    public void onStart() {
        if (mGoogleApiClient == null) {
            switchToScreen(R.id.screen_sign_in);
        } else if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Connecting client.");
            switchToScreen(R.id.screen_wait);
            mGoogleApiClient.connect();
        } else {
            Log.w(TAG,
                    "GameHelper: client was already connected on onStart()");
        }
        super.onStart();
    }

    // Handle back key to make sure we cleanly leave a game if we are in the middle of one
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == R.id.screen_game) {
            leaveRoom();
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            mRoomId = null;
            switchToScreen(R.id.screen_wait);
        } else {
            switchToMainScreen();
        }
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        mIncomingInvitationId = invitation.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        switchToScreen(mCurScreen); // This will show the invitation popup
    }

    @Override
    public void onInvitationRemoved(String invitationId) {

        if (mIncomingInvitationId.equals(invitationId) && mIncomingInvitationId != null) {
            mIncomingInvitationId = null;
            switchToScreen(mCurScreen); // This will hide the invitation popup
        }

    }

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG, "onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        switchToScreen(R.id.screen_sign_in);
    }

    // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");

        //get participants and my ID:
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

        // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
        if (mRoomId == null)
            mRoomId = room.getRoomId();

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        switchToMainScreen();
    }

    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
        mRoomId = null;
        showGameError();
    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }

        // save room ID so we can leave cleanly before the game starts.
        mRoomId = room.getRoomId();

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        updateRoom(room);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // We treat most of the room update callbacks in the same way: we update our list of
    // participants and update the display. In a real game we would also have to check if that
    // change requires some action like removing the corresponding player avatar from the screen,
    // etc.
    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {

        }
    }

    /*
     * GAME LOGIC SECTION. Methods that implement the game's rules.
     */

    // Current state of the game:
    int mSecondsLeft = -1; // how long until the game ends (seconds)
    final static int GAME_DURATION = 20; // game duration, seconds.
    int mScore = 0; // user's current score

    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        mSecondsLeft = GAME_DURATION;
        mScore = 0;
        p2HealthMap.clear();
        mFinishedParticipants.clear();
    }

    // Start the gameplay phase of the game.
    void startGame() throws IOException {
        initGLView();
        broadcast();
        switchToScreen(R.id.screen_game);


        // run the gameTick() method every second to update the game.
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0)
                    return;
                try {
                    broadcast();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                h.postDelayed(this, 10);
            }
        }, 10);
        final Handler h2 = new Handler();
        h2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0)
                    return;
                try {
                    broadcastScore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                h2.postDelayed(this, 1000);
            }
        }, 1000);
    }




    /*
     * COMMUNICATIONS SECTION. Methods that implement the game's network
     * protocol.
     */

    // Score of other participants. We update this as we receive their scores
    // from the network.
    Map<String, Integer> p2HealthMap = new HashMap<String, Integer>();

    // Participants who sent us their final score.
    Set<String> mFinishedParticipants = new HashSet<String>();


    private ArrayList<byte[]> bulletData = new ArrayList<>();

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        //Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
        Log.v("dataRec", buf.length + "");
        if (player2 != null) {
            Log.v("dataRec2", buf.length + "");
            /*switch (buf[0]) {

                case 'H': {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                    int p2Health = 1000;
                    try {
                        p2Health = dataInputStream.readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (player2.getHealth() > p2Health) {
                        player2.setHealth(p2Health);
                    }
                    break;
                }
                case 'S': {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

                    try {
                        player2.setScore(dataInputStream.readInt());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 'P':
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);


                    try {
                        player2.setxLoc(dataInputStream.readFloat());
                        player2.setyLoc(dataInputStream.readFloat());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 'B':
                    bulletData.add(buf);
                    long id = (long) buf[3];
                    boolean exists = false;
                    int index = 0;
                    for (Bullet bullet:new ArrayList<>(player2.getBullets())) {
                        if (bullet.getId() == id) {
                            exists = true;
                            break;
                        }
                        index ++;
                    }
                    if (!exists) {
                        player2.addBullet(buf[4] == 'L' ? Player.LEFT_FACING : Player.RIGHT_FACING, (float) buf[0], (float)buf[1]);
                        bulletData.add(buf);
                    } else {
                        Bullet b = player2.getBullets().get(index);
                        Log.v("bulletRecieved",buf[1]+","+buf[2]);
                        b.setxLoc((int)(buf[1]/100.0));
                        b.setyLoc((int)(buf[2]/100.0));
                    }
                    break;

            }*/
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            try {
                switch (dataInputStream.readChar()) {

                    case 'D': {


                        int p2Health = dataInputStream.readInt();
                        if (player2.getHealth() > p2Health) {
                            player2.setHealth(p2Health);
                        }

                        long messageTime = dataInputStream.readLong();
                        if(messageTime>lastRecievedTime) {
                            lastRecievedTime = messageTime;
                            float xVal = dataInputStream.readFloat();
                            Log.v("xLocp2", xVal + "");
                            player2.setxLoc(xVal);
                            player2.setyLoc(dataInputStream.readFloat());
                        }

                        break;
                    }
                    case 'S': {

                        int score = dataInputStream.readInt();
                        if(player2.getScore()<score)
                        player2.setScore(score);

                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void broadcastScore() throws IOException{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);


        dataOutputStream.writeChar('S');
        dataOutputStream.writeInt(player1.getScore());
        byte[] scoreData = byteArrayOutputStream.toByteArray();

        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;


            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, scoreData,
                    mRoomId, p.getParticipantId());
        }

    }
    void broadcast() throws IOException {
        if (isPaused) {
            return;
        }



        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream2 = new DataOutputStream(byteArrayOutputStream2);


        dataOutputStream2.writeChar('D');
        dataOutputStream2.writeInt(player1.getHealth());
        dataOutputStream2.writeLong(System.currentTimeMillis());
        dataOutputStream2.writeFloat(-1 * player1.getxLoc());
        dataOutputStream2.writeFloat(player1.getyLoc());
        byte[] otherData = byteArrayOutputStream2.toByteArray();
        Log.v("dataSend", String.valueOf(otherData.length));





        /*



        byte[] health = new byte[2];
        health[0] = (byte) ('H');

        // Second byte is the score.
        health[1] = (byte) player1.getHealth();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);


            dataOutputStream.writeChar('H');
            dataOutputStream.writeInt(player1.getHealth());
        health = byteArrayOutputStream.toByteArray();


        byteArrayOutputStream.flush();
        dataOutputStream.flush();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeChar('S');
        dataOutputStream.writeInt(player1.getScore());
        byte[] score = byteArrayOutputStream.toByteArray();


         byteArrayOutputStream.flush();
        dataOutputStream.flush();
         dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeChar('P');
        dataOutputStream.writeFloat(-1*player1.getxLoc());
        dataOutputStream.writeFloat(player1.getyLoc());
        byte[] playerPos = byteArrayOutputStream.toByteArray();
        Log.v("playerPosSize",playerPos.length+"");*/
        /*playerPos[0] = (byte) ('P');
        Log.v("player1Loc",player1.getxLoc()+","+player1.getyLoc());
        String xString = (-1*player1.getxLoc())+"";
        int dotPos = xString.indexOf(".");
        while(xString.length()<6){
            xString+="0";
        }
        Log.v("xString",xString);
        playerPos[1] = (byte)(Integer.parseInt(xString.substring(0,dotPos)));
        playerPos[2] = (byte)(Integer.parseInt(xString.substring(dotPos+1,dotPos+2)));
        playerPos[3] = (byte)(Integer.parseInt(xString.substring(dotPos+2,dotPos+3)));
        playerPos[4] = (byte)(Integer.parseInt(xString.substring(dotPos+3,dotPos+4)));

        xString = player1.getyLoc()+"";
        while(xString.length()<6){
            xString+="0";
        }
        dotPos = xString.indexOf(".");
        Log.v("yString",xString);
        playerPos[5] = (byte)(Integer.parseInt(xString.substring(0,dotPos)));
        playerPos[6] = (byte)(Integer.parseInt(xString.substring(dotPos+1,dotPos+2)));
        playerPos[7] = (byte)(Integer.parseInt(xString.substring(dotPos+2,dotPos+3)));
        playerPos[8] = (byte)(Integer.parseInt(xString.substring(dotPos+3,dotPos+4)));

        Log.v("Player1Send",(float) playerPos[1]+","+(float) playerPos[2]);
        */


       /*ArrayList<byte[]> bulletsPos = new ArrayList();
        for(Bullet b: new ArrayList<>(player1.getBullets())){
            byte[] bulletPos = new byte[5];
            bulletPos[0]=(byte)('B');
            bulletPos[1]=(byte)b.getxLoc();
            bulletPos[2]=(byte)b.getyLoc();
            bulletPos[3]=(byte)b.getId();
            bulletPos[4]=(byte)(b.getDirection().equals(Player.LEFT_FACING) ? 'L' : 'R');
            bulletsPos.add(bulletPos);
        }*/
        // Send to every other participant.


        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;

            Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, otherData, mRoomId,
                    p.getParticipantId());
            /*for(byte[] bytes:bulletsPos){
                Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, bytes, mRoomId,
                        p.getParticipantId());
            }*/
        }


    }

    /*
     * UI SECTION. Methods that implement the game's UI.
     */

    // This array lists everything that's clickable, so we can install click
    // event handlers.
    final static int[] CLICKABLES = {
            R.id.button_accept_popup_invitation, R.id.button_invite_players,
            R.id.button_quick_game, R.id.button_see_invitations, R.id.button_sign_in,
            R.id.button_sign_out
    };

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_game, R.id.screen_main, R.id.screen_sign_in,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == R.id.screen_main);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    void switchToMainScreen() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switchToScreen(R.id.screen_main);
        } else {
            switchToScreen(R.id.screen_sign_in);
        }
    }



    /*
     * MISC SECTION. Miscellaneous methods.
     */


    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    private static MyGLSurfaceView mGLView;

    public static volatile double mPreviousX, mPreviousY, mPreviousX2, mPreviousY2;
    public static volatile double mCurrentX, mCurrentY, mCurrentX2, mCurrentY2;
    private static Player player1, player2;
    private static double widthPixels, heightPixels;
    public static boolean p1Touch, p2Touch;
    public ImageButton pauseButton;
    private RelativeLayout parentMenu, subMenu;
    private Button resumeButton, menuButton;
    public static boolean isPaused;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (player != null && sharedPref.getBoolean("musicToggle", true)) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (player != null && sharedPref.getBoolean("musicToggle", true)) {
            player.start();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }

    public static void updateScore1() {
        score1.setText("P1 Score: " + player1.getScore());
    }

    public static void updateScore2() {
        score2.setText("P2 Score: " + player2.getScore());
    }

    public static void updateHealth() {
        if (health1 != null && health2 != null) {
            health1.setText("P1 Health: " + player1.getHealth());
            health2.setText("P2 Health: " + player2.getHealth());
        }
    }


    public static TextView score1, score2, health1, health2;
    private static RelativeLayout leftLayout, rightLayout;
    private FrameLayout frame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.invitelayout);
        // Create the Google Api Client with access to Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        // set up a click listener for everything we care about
        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }
        frame = (FrameLayout) findViewById(R.id.screen_game);

        score1 = (TextView) frame.findViewById(R.id.player1Score);
        score2 = (TextView) frame.findViewById(R.id.player2Score);
        health1 = (TextView) frame.findViewById(R.id.player1Health);
        health2 = (TextView) frame.findViewById(R.id.player2Health);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }
        leftLayout = (RelativeLayout) findViewById(R.id.leftRelativeLayout);
        rightLayout = (RelativeLayout) findViewById(R.id.rightRelativeLayout);
        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isPaused) {
                    return true;
                }
                float x = event.getX();
                float y = event.getY();
                // Log.v("action",MotionEvent.actionToString(event.getAction()));
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        // Log.v("action","move");
                        player1.setMoveValues(Math.atan2(y - mPreviousY, x - mPreviousX), Math.sqrt((x - mPreviousX) * (x - mPreviousX) + (y - mPreviousY) * (y - mPreviousY)));
                        //player1.move();
                        mCurrentX = x;
                        mCurrentY = y;
                        mGLView.requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        // Log.v("action","down");
                        mPreviousX = x;
                        mPreviousY = y;
                        p1Touch = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        //  Log.v("action","up");
                        p1Touch = false;
                        break;

                }
                return true;
            }
        });

        pauseButton = (ImageButton) findViewById(R.id.pauseButton);


        if (0.2126 * colorBG[0] + 0.7152 * colorBG[1] + 0.0722 * colorBG[2] > 0.179) {
            score1.setTextColor(Color.BLACK);
            health1.setTextColor(Color.BLACK);
            score2.setTextColor(Color.BLACK);
            health2.setTextColor(Color.BLACK);
            pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            score1.setTextColor(Color.WHITE);
            health1.setTextColor(Color.WHITE);
            score2.setTextColor(Color.WHITE);
            health2.setTextColor(Color.WHITE);
            pauseButton.setImageResource(R.drawable.ic_action_pause);
        }
        isPaused = false;
        parentMenu = (RelativeLayout) findViewById(R.id.menu_layout);
        subMenu = (RelativeLayout) findViewById(R.id.subMenu);
        resumeButton = (Button) subMenu.findViewById(R.id.resumeButton);
        menuButton = (Button) subMenu.findViewById(R.id.backMenuButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.v("pause", "clicked");
                subMenu.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);

                parentMenu.setBackgroundColor(Color.parseColor("#80000000"));
                isPaused = true;
                mGLView.onPause();
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subMenu.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                parentMenu.setBackgroundColor(Color.parseColor("#00000000"));
                isPaused = false;
                mGLView.onResume();
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MultiplayerActivity.this, MainMenu.class));
            }
        });


    }


    public void initGLView() {
        if (player1 != null && player2 != null) {
            player1.reset();
            player2.reset();
        }
        mGLView = new MyGLSurfaceView(getApplicationContext(), mGoogleApiClient);


        frame.addView(mGLView, 0);

        DisplayMetrics display = getResources().getDisplayMetrics();
        widthPixels = display.widthPixels;
        heightPixels = display.heightPixels;
        mGLView.getHolder().setFixedSize((int) widthPixels, (int) heightPixels);
        health1.setText("P1 Health: " + player1.getHealth());
        health2.setText("P2 Health: " + player2.getHealth());
    }

    class MyGLSurfaceView extends GLSurfaceView {

        public final MultiplayerRenderer mRenderer;

        public float getScreenHeight() {
            return mRenderer.getScreenHeight();
        }

        public float getScreenWidth() {
            return mRenderer.getScreenWidth();
        }

        public MultiplayerRenderer getRenderer() {
            return mRenderer;
        }

        public MyGLSurfaceView(Context context, GoogleApiClient client) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MultiplayerRenderer(context, score1, score2, health1, health2, mParticipants.get(0), mRoomId, mGoogleApiClient, bulletData);

            // Set the Renderer for drawing on the GLSurfaceView

            setRenderer(mRenderer);
            //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            player1 = mRenderer.getPlayer1();
            player2 = mRenderer.getPlayer2();

        }


    }


    public static float getmPreviousXFloat() {
        double distance = mPreviousX / widthPixels * widthPixels / heightPixels * 2;
        //Log.v("joystick", mPreviousX + "");
        return (float) (-distance + mGLView.getScreenWidth());
    }

    public static float getmPreviousYFloat() {
        if (mPreviousY > heightPixels / 2) {
            double distance = mPreviousY - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mPreviousY;
            return (float) (distance / (heightPixels / 2));
        }
    }

    public static float getmPreviousX2Float() {

        double distance = (mPreviousX2) / (widthPixels / 2) * -widthPixels / heightPixels;
        double x2 = -mPreviousX2 / widthPixels * widthPixels / heightPixels * 2;
        double distance2 = mPreviousX2 / rightLayout.getWidth() * widthPixels / heightPixels;
        //Log.v("joystick", mPreviousX2 + ";" + widthPixels / heightPixels);
        // Log.v("joystick",mPreviousX2/widthPixels*mGLView.getScreenWidth()*2+";");
        return (float) -distance2;
    }

    public static float getmPreviousY2Float() {
        if (mPreviousY2 > heightPixels / 2) {
            double distance = mPreviousY2 - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mPreviousY2;
            return (float) (distance / (heightPixels / 2));
        }
    }


    public static float getmCurrentXFloat() {
        double distance = mCurrentX / widthPixels * widthPixels / heightPixels * 2;
        //Log.v("joystick", mPreviousX + "");
        return (float) (-distance + mGLView.getScreenWidth());
    }

    public static float getmCurrentYFloat() {
        if (mCurrentY > heightPixels / 2) {
            double distance = mCurrentY - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mCurrentY;
            return (float) (distance / (heightPixels / 2));
        }
    }

    public static float getmCurrentX2Float() {


        double distance2 = mCurrentX2 / rightLayout.getWidth() * widthPixels / heightPixels;
        //Log.v("joystick", mPreviousX2 + ";" + widthPixels / heightPixels);
        // Log.v("joystick",mPreviousX2/widthPixels*mGLView.getScreenWidth()*2+";");
        return (float) -distance2;
    }

    public static float getmCurrentY2Float() {
        if (mCurrentY2 > heightPixels / 2) {
            double distance = mCurrentY2 - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mCurrentY2;
            return (float) (distance / (heightPixels / 2));
        }
    }
}