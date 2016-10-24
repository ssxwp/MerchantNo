package com.qingyuan.tianya.merchant.activity;

import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.qrcode.camera.CameraManager;
import com.qingyuan.tianya.merchant.qrcode.decoding.CaptureActivityHandler;
import com.qingyuan.tianya.merchant.qrcode.decoding.InactivityTimer;
import com.qingyuan.tianya.merchant.qrcode.view.ViewfinderView;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback{

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private HeaderView header;
	private LinearLayout ll_left_layout;
	private AlertDialog dialog2;
	private TextView tv_shishou,tv_youhui;
	private String mem_id;
	private String price;
	private String m_id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		addActivity(this);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		initView();
		initData();
		initClick();
	}

	@Override
	public void initView() {
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		header = ((HeaderView) findViewById(R.id.ac_qr_head));
		ll_left_layout=(LinearLayout) header.findViewById(R.id.ll_left_layout);
	}

	@Override
	public void initData() {
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	public void initClick() {
		ll_left_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				skipActivityForClose(MipcaActivityCapture.this,HomeActivity.class,null);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * 处理扫描结果
	 */
	public void handleDecode(final Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		dialog2 = new AlertDialog.Builder(MipcaActivityCapture.this).create();
		dialog2.setView(new EditText(MipcaActivityCapture.this));
		dialog2.show();
		dialog2.getWindow().setContentView(R.layout.dialog_money);
		final EditText ed = (EditText)dialog2.getWindow().findViewById(R.id.ed);
		tv_shishou = (TextView)dialog2.getWindow().findViewById(R.id.tv_shishou);
		tv_youhui=(TextView)dialog2.getWindow().findViewById(R.id.tv_youhui);
		ed.setFocusable(true);
		dialog2.getWindow().findViewById(R.id.out_diss).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (StringUtil.isNotEmpty(ed.getText().toString().trim())) {
					getMoney(result, ed.getText().toString().trim());
				}else {
					toast("请输入金额");
				}
			}
		});
		dialog2.getWindow().findViewById(R.id.out_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtil.isNotEmpty(tv_shishou.getText().toString().trim())) {
					//payPurse(ed.getText().toString().trim());
					//dialog2.dismiss();
					getSure();
				} else {
					toast("需先计算价钱");
				}
			}
		});
		/*String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			bundle.putParcelable("bitmap", barcode);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);
		}
		MipcaActivityCapture.this.finish();*/
		/*AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		if (barcode == null)
		{
			dialog.setIcon(null);
		}
		else
		{

			Drawable drawable = new BitmapDrawable(barcode);
			dialog.setIcon(drawable);
		}
		dialog.setTitle("扫描结果");
		dialog.setMessage(result.getText());
		dialog.setNegativeButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//用默认浏览器打开扫描得到的地址
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(result.getText());
				intent.setData(content_url);
				startActivity(intent);
				finish();
			}
		});
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		dialog.create().show();*/
	}

	private void getSure() {
		initProgressDialog();
		RequestParams params = new RequestParams();
		params.put("mem_id",mem_id);
		params.put("price",price);
		params.put("shop_id",m_id);
		HttpUtil.gets("http://114.215.78.102/index.php/Home/index/obtainCash", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String s) {
				try {
					JSONObject jObj = new JSONObject(s);
					if (jObj.getString("flag").equals("success")) {
						//toast(jObj.getString("message"));
						tv_shishou.setText(jObj.getString("message"));
					} else {
						toast(jObj.getString("message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					close();
				}
				close();
			}

			public void onFailure(Throwable arg0) { // 失败，调用
				close();
			}

			public void onFinish() { // 完成后调用，失败，成功，都要调
				close();
			}
		});
	}

	private void getMoney(Result result, String trim) {
		initProgressDialog();
		m_id = createSharedPreference(this, "user_custId").getValue("custId");
		RequestParams params = new RequestParams();
		params.put("shop_id",m_id);
		params.put("price",trim);
		params.put("string",result.toString());
		HttpUtil.gets("http://114.215.78.102/index.php/Home/index/deciphering", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String s) {
				try {
					JSONObject jObj = new JSONObject(s);
					if (jObj.getString("flag").equals("success")) {
						JSONObject object = jObj.getJSONObject("message");
						mem_id = object.getString("mem_id");
						price = object.getString("price");
						tv_shishou.setText("实收金额：￥"+price);
					} else {
						toast(jObj.getString("message"));
					}
                   /* try {
                        toast(jObj.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        close();
                    }*/
				} catch (JSONException e) {
					e.printStackTrace();
					close();
				}
				close();
			}

			public void onFailure(Throwable arg0) { // 失败，调用
				close();
			}

			public void onFinish() { // 完成后调用，失败，成功，都要调
				close();
			}
		});

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException | RuntimeException ioe) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}