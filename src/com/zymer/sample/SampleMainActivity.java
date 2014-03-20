package com.zymer.sample;

import com.zymer.sdk.ZCryptoSDK;
import com.zymer.sdk.ZKeyServerSDK;
import com.zymer.sdk.ZPinSDK;
import com.zymer.sdk.data.ZResult;
import com.zymer.sdk.data.ZUserInfo;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SampleMainActivity extends Activity implements OnClickListener {
	private int result = 0;
	
	private String uid = "test@zymerinc.com";
	private String name = "testuser";
	private String email = "test@zymerinc.com";
	private String description = "This user is test user for zymer";
	private String data = "hello zymer!";
	private String encData = "";
	private String decData = "";
	private String serial = "1234";
	private String server = "http://192.168.1.10:3000";
	
	private Button btnZPinSDK = null;
	private Button btnZCryptoSDK = null;
	private Button btnZKeyServerSDK = null;
	
	private EditText etOutput = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_main);
		
		// set mode
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
		StrictMode.setThreadPolicy(policy);
		
		btnZPinSDK = (Button)findViewById(R.id.btnZPinSDKTest);
		btnZPinSDK.setOnClickListener(this);
		btnZCryptoSDK = (Button)findViewById(R.id.btnZCryptoSDKTest);
		btnZCryptoSDK.setOnClickListener(this);
		btnZKeyServerSDK = (Button)findViewById(R.id.btnZKeyServerSDKTest);
		btnZKeyServerSDK.setOnClickListener(this);
		
		etOutput = (EditText)findViewById(R.id.etOutput);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sample_main, menu);
		return true;
	}
	
	/*
	 * Secure Pin SDK
	 */
	public void testZPinSDK() 
	{
		output("Start ZPinSDK Testing...");
		
		ZPinSDK pinSDK = new ZPinSDK();
		result = pinSDK.initialize(this);
		output(result, "Initialize");
		
		result = pinSDK.removePin();
		output(result, "removePin");

		result = pinSDK.setPin("", "1234");
		output(result, "setPin");

		result = pinSDK.verifyPin("1234");
		output(result, "verifyPin");

		result = pinSDK.changePin("1234", "abcd");
		output(result, "changePin");

		result = pinSDK.verifyPin("abcd");
		output(result, "verifyPin");
		
		output("ZPinSDK Testing done.");
		
	}
	
	/*
	 * Crypto SDK
	 */
	public void testZCryptoSDK()
	{
		output("Start ZCryptoSDK Testing...");
		
		ZCryptoSDK cryptoSDK = new ZCryptoSDK();
		result = cryptoSDK.initialize(this);
		output(result, "Initialize");
		
		result = cryptoSDK.reset();
		output(result, "reset");
		
		result = cryptoSDK.createUser(uid, name, email, description);
		output(result, "createUser");
		
		encData = cryptoSDK.encrypt(uid, data);
		if (!encData.isEmpty())
			output(ZResult.RESULT_OK, "encData");
		else
			output(ZResult.ERROR_UNEXPECTED, "encData");
		
		decData = cryptoSDK.decrypt(encData);
		if (!decData.isEmpty())
			output(ZResult.RESULT_OK, "decrypt");
		else
			output(ZResult.ERROR_UNEXPECTED, "decrypt");
		
		if (decData.compareTo(data) == 0) {
			output("Match it!");
		} else {
			output("Not Match");
		}
		
		output("ZCryptoSDK Testing done.");
	}

	/*
	 * KeyServer SDK
	 */
	public void testZKeyServerSDK() 
	{
		output("Start ZKeyServerSDK Testing...");
		
		ZKeyServerSDK keySDK = new ZKeyServerSDK();
		
		result = keySDK.initialize(this, server, serial);
		output(result, "initialize");
		
		result = keySDK.addUserInfo(uid, name, email, description);
		output(result, "addUserInfo");

		keySDK.synchronize();
		output(result, "synchronize");
		
		String uinfo = "user info=";
		int count = keySDK.getUserCount();
		for (int i = 0; i < count; i++) {
			ZUserInfo zUserInfo = keySDK.getUserInfo(i);
			uinfo += zUserInfo.getUid() + ",";
			uinfo += zUserInfo.getName() + ",";
			uinfo += zUserInfo.getEmail() + ",";
			uinfo += zUserInfo.getDesc() + ",";
			
			output(result, uinfo);
		}

		result = keySDK.addKey(uid, "this is key");
		output(result, "addKey");

		result = keySDK.deleteKey(uid);
		output(result, "deleteKey");
		
		result = keySDK.resetKeyServer();
		output(result, "resetKeyServer");
		
		output("ZKeyServerSDK Testing done.");
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnZPinSDKTest:
			testZPinSDK();
			break;
		case R.id.btnZCryptoSDKTest:
			testZCryptoSDK();
			break;
		case R.id.btnZKeyServerSDKTest:
			testZKeyServerSDK();
			break;
		}
	}
	
	private void output(String log) {
		log = log + "\n";
		etOutput.setText(log + etOutput.getText().toString());
	}
	
	private void output(int result, String log) {
		String Tag = "[PASSED]";
		if (result == ZResult.RESULT_OK) {
			Tag = "[PASSED] ";
		} else {
			Tag = "[FAILED] ";
		}
		
		log = Tag + log + "\n";
		etOutput.setText(log + etOutput.getText().toString());
	}
}
