package com.cinesbus;

import android.os.Bundle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.*;
import com.google.android.gms.common.GooglePlayServicesClient.*;
import com.google.android.gms.plus.PlusClient;

import com.cinesbus.util.Util;

public class MainActivity extends Activity implements View.OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	private static final String TAG = "ExampleActivity";
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	private String link;
	private Util u;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		u = new Util(this);

		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				"http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity").build();

		findViewById(R.id.sign_in_button).setOnClickListener(this);

		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Iniciando...");
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "disconnected");
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	String name = "";

	@Override
	public void onConnected(Bundle connectionHint) {
		// String accountName = mPlusClient.getAccountName();
		// Toast.makeText(this, accountName + " is connected.",
		// Toast.LENGTH_LONG).show();

		mConnectionProgressDialog.dismiss();

		name = mPlusClient.getCurrentPerson().getDisplayName();

		Toast.makeText(this, name + "YA ESTAS CONECTADO", Toast.LENGTH_LONG)
				.show();

		link += "&name="
				+ u.convertirURL(mPlusClient.getCurrentPerson()
						.getDisplayName());
		link += "&nick="
				+ u.convertirURL(mPlusClient.getCurrentPerson().getNickname());
		link += "&mail=" + u.convertirURL(mPlusClient.getAccountName());
		link += "&version="
				+ u.convertirURL(getText(R.string.versionName).toString());
		link += "&nacimiento=" + u.convertirURL(fecha_nac);
		link += "&genero="
				+ u.convertirURL(mPlusClient.getCurrentPerson().getGender()
						+ "");
		link += "&lenguaje="
				+ u.convertirURL(mPlusClient.getCurrentPerson().getLanguage());
		link += "&url="
				+ u.convertirURL(mPlusClient.getCurrentPerson().getUrl());
		link += "&tipo=" + tipo;

		context = getApplicationContext();

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					// Try connecting again.
					mConnectionResult = null;
					mPlusClient.connect();
				}
			}
		}
	}

}