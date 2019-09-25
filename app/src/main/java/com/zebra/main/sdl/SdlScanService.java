package com.zebra.main.sdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import com.zebra.android.jb.Preference;
import com.zebra.android.jb.SymbologiesPrefer;
import com.zebra.android.jb.barcode.BarcodeManager;
import com.zebra.android.jb.barcode.BarcodeUntil;
import com.zebra.android.jb.utils.Tools;
import com.zebra.android.jb.utils.WakeLockUtil;

import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.adc.decoder.BarCodeReader.DecodeCallback;
import com.zebra.adc.decoder.BarCodeReader.ErrorCallback;
import com.zebra.adc.decoder.BarCodeReader.VideoCallback;

public class SdlScanService extends Service implements DecodeCallback, ErrorCallback, VideoCallback {
    private long scan_time_limit = 200; // 扫描间隔控制
    private static final String TAG = "SDLgui";
    private boolean isActivityUp = false;
    private ComponentName cn;
    //private ScanListener mScanListener;
    private KeyguardManager km;
    private BarcodeManager scanManager = null;
    private boolean isUhfShortcutSupport = false;
    private boolean isScanShortcutSupport = true;
    public static boolean keyF2DownOrNot = false;
    public static boolean isServiceOn = false;
    public static boolean isScanActivityUp = false; // 是否有Acitivty与服务绑定
    private WakeLockUtil mWakeLockUtil = null;
    public static String uhfName = "com.jiebao.ht380a.uhf.UHFMainActivity";
    public static String chaobiaoName = "com.jiebao.ht380a.chaobiao.ChaoBiaoActivity";
    public static String rs232Name = "com.jiebao.ht380.rs232.RS232Activity";
    public static String psamName = "com.jiebao.ht380a.psam.PSamActivity";
    public static String uhfName2 = "com.jiebao.ht380a.uhf.UHFActivityMode2";
    public static String uhfName3 = "com.jiebao.ht380a.uhf.UHFActivityMode3";
    public static String scanName = "com.jiebao.ht380a.scan.ScanActivity";
    private FileOutputStream fileOutputStream;
    private File scanFile;
    private boolean sdCard;
    public BeepManagerSdl mBeepManager;
    private Intent webAddressintent;
    private Intent scanDataIntent;
    private Intent EditTextintent;
    private BarcodeUntil scanUtil;
    private BarCodeReader bcr = null;
    private int state = STATE_IDLE;

    //states
    static final int STATE_IDLE = 0;
    static final int STATE_DECODE = 1;
    static final int STATE_HANDSFREE = 2;
    static final int STATE_PREVIEW = 3;    //snapshot preview mode
    static final int STATE_SNAPSHOT = 4;
    static final int STATE_VIDEO = 5;
    private static int decCount = 0;
    private int trigMode = BarCodeReader.ParamVal.LEVEL;
    private int decodes = 0;
    public boolean beepMode = false;        // decode beep enable
    static private boolean sigcapImage = true; // true = display signature capture
    private String decodeDataString;
    private String decodeStatString;
    private SdlScanListener mScanListener;
    public BeepManagerSdl mBeepManagersdl;
    private boolean snapPreview = false;    // snapshot preview mode enabled - true - calls viewfinder which gets handled by
    private int motionEvents = 0;
    private int modechgEvents = 0;
    private byte[] bs;
    private static File ss = new File("/proc/jbcommon/gpio_control/se4710");
    private static FileInputStream inputStream = null;
    private static File ss_isCameraOpen = new File("/proc/jbcommon/gpio_control/isCameraOpen");
    private ScanThread scanThread;
    private String openCamera = "31";
    private readFileThread readFilethread = null;
    private ActivityManager am;
    private boolean islockScreen = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 3001:
                    Toast.makeText(
                            SdlScanService.this,
                            getResources().getString(
                                    R.string.scan_init_failure_info), Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    mScanListener.ScannedResult((String) msg.obj);
                    break;
                case 2:
                    mScanListener.ScannedStatus((String) msg.obj);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    static {
        System.loadLibrary("IAL");
        System.loadLibrary("SDL");

        if (android.os.Build.VERSION.SDK_INT >= 19)
            System.loadLibrary("barcodereader44"); // Android 4.4
        else if (android.os.Build.VERSION.SDK_INT >= 18)
            System.loadLibrary("barcodereader43"); // Android 4.3
        else
            System.loadLibrary("barcodereader");   // Android 2.3 - Android 4.2
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceOn = true;
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        Log.v(TAG, "SDLService onCreate");
        mWakeLockUtil = new WakeLockUtil(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.jb.action.F4key");
        intentFilter.addAction("com.jb.action.Key");
        intentFilter.addAction("com.jb.action.SCAN_SWITCH");
        intentFilter.addAction("com.jb.action.SOFT_SCAN_SWITCH");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(f4Receiver, intentFilter);

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                sdCard = true;
                File file = new File("mnt/sdcard/Scan Record");
                if (!file.exists()) {
                    file.mkdirs();
                }

                scanFile = new File("mnt/sdcard/" + "Scan Record" + "/"
                        + "Scan Record.txt");
                if (!scanFile.exists())
                    scanFile.createNewFile();
                fileOutputStream = new FileOutputStream(scanFile, true);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        isScanShortcutSupport = Preference.getScanShortcutSupport(this, true);
        isUhfShortcutSupport = Preference.getUhfShortcutSupport(this, true);

        scanDataIntent = new Intent("com.jb.action.GET_SCANDATA");
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);
        Log.v(TAG, "SDLService onStart isActivityUp:" + isActivityUp);
        //state = STATE_IDLE;
        if (isActivityUp) {//避免开机自启与camera冲突
            openBcr();
        }
        if (readFilethread != null) {
            readFilethread.interrupt();
            readFilethread.run = false;
        }
        readFilethread = new readFileThread();
        readFilethread.run = true;
        readFilethread.start();

        if (mBeepManagersdl == null)
            mBeepManagersdl = new BeepManagerSdl(this,
                    beepMode, beepMode);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }

    public synchronized void openBcr() {
        try {
            if (bcr == null && getNumberOfCameras() > 1) {
                if (android.os.Build.VERSION.SDK_INT >= 18) {
                    bcr = BarCodeReader.open(1, this); // Android 4.3 and above
                } else {
                    bcr = BarCodeReader.open(1); // Android 2.3
                }
            } else if (bcr == null && getNumberOfCameras() == 1) {
                if (android.os.Build.VERSION.SDK_INT >= 18) {
                    bcr = BarCodeReader.open(0, this); // Android 4.3 and above
                } else {
                    bcr = BarCodeReader.open(0); // Android 2.3
                }
            }

            if (bcr == null) {
                if (mScanListener != null)
                    mScanListener.ScannedStatus("ERROR open failed");
                return;
            }

            bcr.setDecodeCallback(this);
            bcr.setErrorCallback(this);

            // Set parameter - Uncomment for QC/MTK platforms
            // bcr.setParameter(765, 0); // For QC/MTK platforms

            // Sample of how to setup OCR Related String Parameters
            // OCR Parameters
            // Enable OCR-B
            //bcr.setParameter(681, 1);

            // Set OCR templates
            //String OCRSubSetString = "01234567890"; // Only numeric characters
            //String OCRSubSetString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!%"; // Only numeric characters
            // Parameter # 686 - OCR Subset
            //bcr.setParameter(686, OCRSubSetString);

            //String OCRTemplate = "54R"; // The D ignores all characters after the template
            // Parameter # 547 - OCR Template
            //bcr.setParameter(547, OCRTemplate);
            // Parameter # 689 - OCR Minimum characters
            //bcr.setParameter(689, 13);
            // Parameter # 690 - OCR Maximum characters
            //bcr.setParameter(690, 13);

            // Set Orientation
            //bcr.setParameter(-2, 1);
            bcr.setParameter(7, 1);  //ENAble CODABAR
            bcr.setParameter(24, 5);
            bcr.setParameter(25, 55);
            bcr.setParameter(22, 0);
            bcr.setParameter(23, 0);
            bcr.setParameter(11, 1);   //ENAbleMSI

            //Preference.getScanPower(this, 10);
            //bcr.setParameter(764, 2);//reduce power
//			bcr.setParameter(764, Preference.getScanPower(this, 10));
//			Log.e("jiebao", "getScanPower" + Preference.getScanPower(this, 10));

            bcr.setParameter(765, 0);
            bcr.setParameter(687, 4); // 4 - omnidirectional
            bcr.setParameter(137, 0);//scan the same code
            bcr.setParameter(586, 2);//Inverse 1D
            bcr.setParameter(716, 1);//support phone mode
            bcr.setParameter(55, 1);
            bcr.setParameter(402, 2);
            // Sets OCR lines to decide
            //bcr.setParameter(691, 2); // 2 - OCR 2 lines
            // End of OCR Parameter Sample

            //init enable code type
            int value = 1;
            value = SymbologiesPrefer.getCodeAztec(this, 1);
            bcr.setParameter(7, value);
            value = SymbologiesPrefer.getCodabar(this, 1);
            bcr.setParameter(7, value);

            value = SymbologiesPrefer.getCode32(this, 1);
            if (value == 1) {
                bcr.setParameter(0, 1);
                bcr.setParameter(86, 1);
            } else {
                bcr.setParameter(0, 0);
            }

            value = SymbologiesPrefer.getCode39(this, 1);
            bcr.setParameter(0, value);

            value = SymbologiesPrefer.getCode93(this, 1);
            bcr.setParameter(9, value);

            value = SymbologiesPrefer.getCodeEan13(this, 1);
            bcr.setParameter(3, value);

            value = SymbologiesPrefer.getCodeGs1_databar(this, 1);
            bcr.setParameter(338, value);

            value = SymbologiesPrefer.getCodeInterleaved_2_of_5(this, 1);
            bcr.setParameter(6, value);

            value = SymbologiesPrefer.getCodeMatrix_2_of_5(this, 1);
            bcr.setParameter(618, value);

            bcr.setParameter(17, 1);
        } catch (Exception e) {
            if (mScanListener != null) {
                mScanListener.ScannedStatus("open excp:" + e);
            }
        }
    }

    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public BarCodeReader getBcr() {
        return bcr;
    }

    private synchronized static void writeFile(File file, String value) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(value.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "SDLService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void setOnScanListener(SdlScanListener scanListener) {
        this.mScanListener = scanListener;
//		if (bcr == null)
//		{
//			mScanListener.ScannedStatus("ERROR open failed");
//			Log.v(TAG,"sdl open failed"+" mScanListener: "+mScanListener);
//		}
    }

    public void setIsScanShortcutSupport(boolean isScanShortcutSupport) {
        this.isScanShortcutSupport = isScanShortcutSupport;
    }

    public void setIsUhfShortcutSupport(boolean isUhfShortcutSupport) {
        this.isUhfShortcutSupport = isUhfShortcutSupport;
    }

    public void setActivityUp(boolean isActivityUp) {
        this.isActivityUp = isActivityUp;
    }

    public Binder myBinder = new MyBinder();

    public class MyBinder extends Binder {

        public SdlScanService getService() {

            return SdlScanService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    long nowTime = 0;
    long lastTime = 0;
    /**
     * 捕获扫描物理按键广播
     */
    private BroadcastReceiver f4Receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Bundle bundle = intent.getExtras();
            if (intent.hasExtra("F4key")) {

                if (getScanShortcutSupport() && intent.getStringExtra("F4key").equals("down")) {
                    Log.v(TAG, "F4key readFile: " + BarCodeReader.readFile(ss_isCameraOpen) + "isActivityUp: " + isActivityUp + "bcr: " + bcr);
                    if (bcr == null && (BarCodeReader.readFile(ss_isCameraOpen).equals("30")) && !islockScreen) {
                        //Toast.makeText(SdlScanService.this, "Can't open scan in Camera", Toast.LENGTH_SHORT).show();
                        if (!isActivityUp) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }
                        openBcr();
                        doDecode();
                    } else if (bcr != null) {
                        doDecode();
                    }
                }
            } else if (intent.getAction().equals("com.jb.action.SOFT_SCAN_SWITCH")) {
                if (intent.getStringExtra("state").equals("0")) {
                    Log.v(TAG, "stopSelf");
                    release();
                } else if (intent.getStringExtra("state").equals("1")) {
                    Log.v(TAG, "openBcr");
                    openBcr();
                }
                //onDestroy();
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                // 开屏
                System.out.println("ACTION_SCREEN_ON");
                islockScreen = false;
                openBcr();
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                // 锁屏
                System.out.println("ACTION_SCREEN_OFF");
                islockScreen = true;
                setIdle();
                release();
            }
        }
    };

    private class readFileThread extends Thread {
        public boolean run;

        @Override
        public void run() {

            while (run) {
                openCamera = BarCodeReader.readFile(ss);
//				if(openCamera.equals("31")&& bcr==null){
//					Log.v(TAG,"readFileThread395 bcr: "+bcr);
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e) {
//
//						e.printStackTrace();
//					}
//					Log.v(TAG,"readFileThread402 bcr: "+bcr);
//					openBcr();
//				} else
                if (openCamera.equals("30") && bcr != null) {
                    release();
                }
            }
        }
    }

    // start a decode session
    public void doDecode() {
        if (setIdle() != STATE_IDLE)
            return;

        state = STATE_DECODE;
        decCount = 0;
        decodeDataString = new String("");
        decodeStatString = new String("");
        if (mScanListener != null) {
            //mScanListener.ScannedResult("");
            //mScanListener.ScannedStatus(R.string.decoding);
        }
        try {
            if (bcr == null)
                openBcr();
            bcr.startDecode(); // start decode (callback gets results)
        } catch (Exception e) {
            Log.v(TAG, "open excp:" + e);
        }

    }

    // ----------------------------------------
    // start video session
    public void doVideo() {
//			if (setIdle() != STATE_IDLE)
//				return;
//
//			resetTrigger();
//			mScanListener.ScannedResult("");
//			mScanListener.ScannedStatus("video started");
//			state = STATE_VIDEO;
//			videoCapDisplayStarted = false;
//			bcr.startVideoCapture(this);
//			//bcr.startPreview();
    }

    // ----------------------------------------
    // reset Level trigger mode
    void resetTrigger() {
        doSetParam(BarCodeReader.ParamNum.PRIM_TRIG_MODE, BarCodeReader.ParamVal.LEVEL);
        trigMode = BarCodeReader.ParamVal.LEVEL;
    }

    //------------------------------------------
    public int setIdle() {
        int prevState = state;
        int ret = prevState;        //for states taking time to chg/end

        state = STATE_IDLE;
        switch (prevState) {
            case STATE_HANDSFREE:
                resetTrigger();
                //fall thru
            case STATE_DECODE:
                if (mScanListener != null) {
                    try {
                        mScanListener.ScannedStatus("decode stopped");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (bcr != null)
                    bcr.stopDecode();
                break;

            case STATE_VIDEO:
                bcr.stopPreview();
                break;

            case STATE_SNAPSHOT:
                ret = STATE_IDLE;
                break;

            default:
                ret = STATE_IDLE;
        }
        return ret;
    }

    public String getStrProperty(int paramNum) {
        return bcr.getStrParameter(paramNum);
    }

    public int getNumProperty(int paramNum) {
        return bcr.getNumProperty(paramNum);
    }

    //---------------------------------------
    // set Default params
    public void doDefaultParams() {
        setIdle();
        bcr.setDefaultParameters();
        mScanListener.ScannedStatus("Parameters Defaulted");

        // reset modes
        snapPreview = false;
        int val = bcr.getNumParameter(BarCodeReader.ParamNum.PRIM_TRIG_MODE);
        if (val != BarCodeReader.BCR_ERROR)
            trigMode = val;
    }

    // ----------------------------------------
    // get properties
    public void doGetProp() {
        setIdle();
        String sMod = bcr.getStrProperty(BarCodeReader.PropertyNum.MODEL_NUMBER).trim();
        String sSer = bcr.getStrProperty(BarCodeReader.PropertyNum.SERIAL_NUM).trim();
        String sImg = bcr.getStrProperty(BarCodeReader.PropertyNum.IMGKIT_VER).trim();
        String sEng = bcr.getStrProperty(BarCodeReader.PropertyNum.ENGINE_VER).trim();
        String sBTLD = bcr.getStrProperty(BarCodeReader.PropertyNum.BTLD_FW_VER).trim();

        int buf = bcr.getNumProperty(BarCodeReader.PropertyNum.MAX_FRAME_BUFFER_SIZE);
        int hRes = bcr.getNumProperty(BarCodeReader.PropertyNum.HORIZONTAL_RES);
        int vRes = bcr.getNumProperty(BarCodeReader.PropertyNum.VERTICAL_RES);

        String s = "Model:\t\t" + sMod + "\n";
        s += "Serial:\t\t" + sSer + "\n";
        s += "Bytes:\t\t" + buf + "\n";
        s += "V-Res:\t\t" + vRes + "\n";
        s += "H-Res:\t\t" + hRes + "\n";
        s += "ImgKit:\t\t" + sImg + "\n";
        s += "Engine:\t" + sEng + "\n";
        s += "FW BTLD:\t" + sBTLD + "\n";

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        if (dlg != null) {
            dlg.setTitle("SDL Properties");
            dlg.setMessage(s);
            dlg.setPositiveButton("ok", null);
            dlg.show();
        }
    }

    private BroadcastReceiver ScreenBroadcastReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                // 开屏
                System.out.println("ACTION_SCREEN_ON");
                if (!checkIsInFirst()) {
//					if (scanManager == null) {
//						scanManager = BarcodeManager.getInstance();
//					} else {
//						// if (scanManager.isSerialPort_isOpen()) {
//						scanManager.Barcode_Close();
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//
//							e.printStackTrace();
//						}
//						// }
//					}
//					// Log.e(TAG, "ACTION_SCREEN_ON()");
//					scanManager.Barcode_Open(SdlScanService.this, dataReceived);
//					// scanManager.SetReadListener(dataReceived);
//					System.out.println("scanManager.init()");
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // 锁屏
                System.out.println("ACTION_SCREEN_OFF");
                // 以防别使用该的串口的被下电s
                if (!checkIsInFirst()) {
                    if (scanManager != null) {
                        scanManager.Barcode_Close();
                        scanManager.Barcode_Stop();
                        // scanManager = null;
                    }
                }
                // scanManager.power("0");
            } else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                System.out.println("ACTION_USER_PRESENT");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
        readFilethread.run = false;
        unregisterReceiver(f4Receiver);
        Log.i(TAG, "sdlService onDestroy");
        mWakeLockUtil.unLock();
        sendBroadcast(new Intent("SdlScanServiceDestroy"));
        isServiceOn = false;
        if (null != readFilethread) {
            notifyReader();
            readFilethread = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
            inputStream = null;
        }
    }

    private void notifyReader() {
        if (readFilethread != null && readFilethread.isAlive()) {
            readFilethread.interrupt();
        }
    }

    public synchronized void release() {
        //writeFile(ss,"0");
        Log.e(TAG, "destory sdl decode bcr: " + bcr);
        if (bcr != null) {
            setIdle();
            bcr.stopDecode();
            Log.e(TAG, "destory sdl decode635");
            bcr.release();
            Log.e(TAG, "destory sdl decode637");
            bcr = null;
            Log.e(TAG, "destory sdl decode");
        }
    }

    private Runnable stopKeyDown = new Runnable() {

        @Override
        public void run() {
            // System.out.println("keyF2DownOrNot:" + keyF2DownOrNot);
            keyF2DownOrNot = false;
        }
    };

    /**
     * 检查最上层Activity 是否为使用相同串口Activity 防冲突
     *
     * @return
     */
    private boolean checkIsInFirst() {
        cn = am.getRunningTasks(1).get(0).topActivity;
        Log.d("", "pkg:" + cn.getPackageName());
        Log.d("", "cls:" + cn.getClassName());
        if (cn.getClassName().equals(rs232Name)
                || cn.getClassName().equals(psamName)
                || cn.getClassName().equals(chaobiaoName)
                || cn.getClassName().equals(uhfName)
                || cn.getClassName().equals(uhfName2)
                || cn.getClassName().equals(uhfName3))
            return true;
        return false;
    }

    public void sendScanBroadcast(String codeid, String dataStr) {
        //scanDataIntent = new Intent("com.jb.action.GET_SCANDATA");
        scanDataIntent.putExtra("data", dataStr);
        scanDataIntent.putExtra("codetype", codeid);
        this.sendBroadcast(scanDataIntent);
    }

    /**
     * 网页支持
     *
     * @param dataStr
     */
    public void webAddressHandler(String dataStr) {
        try {
            Log.v(TAG, "SdlScanService webAddressHandler dataStr: " + dataStr);
            webAddressintent = new Intent();
            webAddressintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            webAddressintent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("http://www.baidu.com/s?ie=utf8&word=" + dataStr);
            webAddressintent.setData(content_url);
            this.startActivity(webAddressintent);
        } catch (Exception e) {
        }
    }

    public void saveInTxt(String data) {
        Log.v("SdlScanService", "saveInTxt data: " + data);
        byte[] bytes = data.getBytes();
        try {
            fileOutputStream.write(bytes);
            fileOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getScanShortcutSupport() {
        return Preference.getScanShortcutSupport(this, true);
    }

    public boolean getScanNetSupport() {
        return Preference.getNetPageSupport(this, false);
    }

    public int getScanOutMode() {
        return Preference.getScanOutMode(this);
    }
    // ==== SDL methods =====================

    // ----------------------------------------
    private boolean isHandsFree() {
        return (trigMode == BarCodeReader.ParamVal.HANDSFREE);
    }

    // ----------------------------------------
    private boolean isAutoAim() {
        return (trigMode == BarCodeReader.ParamVal.AUTO_AIM);
    }

    @Override
    public void onDecodeComplete(int symbology, int length, byte[] data, BarCodeReader reader) {
        Log.e("jiebao", "symbology " + symbology + " length " + length);

        if (state == STATE_DECODE) {
            state = STATE_IDLE;
        }

        // Get the decode count
        if (length == BarCodeReader.DECODE_STATUS_MULTI_DEC_COUNT) {
            decCount = symbology;
        }

        if (length > 0) {

            if (mBeepManagersdl.isPlayBeep() || mBeepManagersdl.isVibrate()) {
                beep();
            }

            bs = new byte[length];
            if (isHandsFree() == false && isAutoAim() == false) {
                bcr.stopDecode();
            }

            ++decodes;
            if (symbology == 0x69) {
                if (sigcapImage) {
                    Bitmap bmSig = null;
                    int scHdr = 6;
                    if (length > scHdr) {
                        bmSig = BitmapFactory.decodeByteArray(data, scHdr, length - scHdr);
                    }

                    if (bmSig != null) {
                        //snapScreen(bmSig);
                    } else {
                        //Log.v(TAG,"OnDecodeComplete: SigCap no bitmap");
                    }

                }

//				System.arraycopy(data, 0, bs, 0, length);
//				decodeStatString += new String("[" + decodes + "] type: " + symbology + " len: " + length);
//				decodeDataString += new String(bs);
//				//Log.v(TAG,"onDecodeComplete decodeDataString: "+decodeDataString+" length:"+length+" bs: "+bs.length);
//
//				houtai_result("",decodeDataString);
//				if(mScanListener !=null) {
//					//Log.v(TAG,"decodeDataString1569 " + decodeDataString);
//					mScanListener.ScannedStatus(decodeStatString);
//					mScanListener.ScannedResult(decodeDataString);
//				}
//				if (sdCard && getScanSaveTxt()) {
//					scanUtil = new BarcodeUntil();
//					scanUtil.setScanDate(Tools.getNowTimeString());
//					scanUtil.setScanResult(decodeDataString);
//					saveInTxt(scanUtil.getScanResult() + "\n"
//								+ "Scan time" + ": " + scanUtil.getScanDate() + "\n");
//				}

                decodeStatString += new String("[" + decodes + "] type: " + symbology + " len: " + length);
                decodeDataString += new String(data);

            } else {
                if (symbology == 0x99) {
                    symbology = data[0];
                    int n = data[1];
                    int s = 2;
                    int d = 0;
                    int len = 0;
                    byte d99[] = new byte[data.length];
                    for (int i = 0; i < n; ++i) {
                        s += 2;
                        len = data[s++];
                        System.arraycopy(data, s, d99, d, len);
                        s += len;
                        d += len;
                    }
                    d99[d] = 0;
                    data = d99;
                }

                System.arraycopy(data, 0, bs, 0, length);
                String codeType = Tools.returnType(bs);

                String val = null;
                if (codeType.equals("default")) {
                    val = new String(bs);
                } else {
                    try {
                        val = new String(bs, codeType);
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                }

                decodeStatString += new String("[" + decodes + "] type: " + symbology + " len: " + length);
                decodeDataString += val;

                String prefix = Preference.getCustomPrefix(SdlScanService.this);
                String suffix = Preference.getCustomSuffix(SdlScanService.this);

                if (!TextUtils.isEmpty(prefix)) {
                    decodeDataString = prefix + decodeDataString;
                }

                if (!TextUtils.isEmpty(suffix)) {
                    decodeDataString = decodeDataString + suffix;
                }

                Log.d("jiebao", "onDecodeComplete decodeDataString: " + decodeDataString + " length:" + length + " bs: " + bs.length);
                if (!isActivityUp) {
                    //houtai_result("",decodeDataString);
                }

                if (mScanListener != null) {
                    mScanListener.ScannedStatus(decodeStatString);
                    mScanListener.ScannedResult(decodeDataString);
//			    Message msg1 = mHandler.obtainMessage();
//			    msg1.what = 1;
//			    msg1.obj    = decodeDataString;
//			    mHandler.sendMessage(msg1);
//			    Message msg2 = mHandler.obtainMessage();
//			    msg2.what = 2;
//			    msg2.obj    = decodeStatString;
//			    mHandler.sendMessage(msg2);
                }

               /* if (sdCard && getScanSaveTxt()) {
                    scanUtil = new BarcodeUntil();
                    scanUtil.setScanDate(Tools.getNowTimeString());
                    scanUtil.setScanResult(decodeDataString);
                    saveInTxt(scanUtil.getScanResult() + "\n" + "Scan time" + ": " + scanUtil.getScanDate() + "\n");
                }
*/
                if (decCount > 1) // Add the next line only if multiple decode
                {
                    decodeStatString += new String(" ; ");
                    decodeDataString += new String(" ; ");
                } else {
                    decodeDataString = new String("");
                    decodeStatString = new String("");
                }

            }

            notifyScanReader();
        } else    // no-decode
        {
            if (mScanListener != null) {
                try {
                    mScanListener.ScannedResult("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            switch (length) {
                case BarCodeReader.DECODE_STATUS_TIMEOUT:
                    Log.v(TAG, "decode timed out");
                    break;

                case BarCodeReader.DECODE_STATUS_CANCELED:
                    Log.v(TAG, "decode cancelled");
                    break;

                case BarCodeReader.DECODE_STATUS_ERROR:
                default:
                    Log.v(TAG, "decode failed");
                    break;
            }
        }

    }

    public void beep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBeepManagersdl.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ----------------------------------------
    // start HandFree decode session
    public void doHandsFree() {
        if (setIdle() != STATE_IDLE)
            return;
        if (bcr == null)
            openBcr();
        int ret = bcr.startHandsFreeDecode(BarCodeReader.ParamVal.HANDSFREE);
        if (ret != BarCodeReader.BCR_SUCCESS) {
            if (mScanListener != null)
                mScanListener.ScannedStatus("startHandFree FAILED");
        } else {
            trigMode = BarCodeReader.ParamVal.HANDSFREE;
            state = STATE_HANDSFREE;

            decodeDataString = new String("");
            decodeStatString = new String("");
            if (mScanListener != null) {
                mScanListener.ScannedResult("");
                mScanListener.ScannedStatus("HandsFree decoding");
            }
        }
    }

    private class ScanThread extends Thread {
        public boolean run;

        @Override
        public void run() {

            while (run) {
                try {
                    if (bcr != null) {
                        doDecode();
                        // System.out.println("ScanActivity Barcode_Start");
                        sleep(3000);
                    }
                } catch (InterruptedException e) {
                    try {
                        sleep(scan_time_limit);
                    } catch (InterruptedException e1) {

                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                setIdle();
            }
        }
    }

    /**
     * 出光开始扫描
     */
    public synchronized void Barcode_Continue_Start(long time) {
        // Log.d(TAG, "Barcode_Continue_Start()");
        if (time > 0) {
            scan_time_limit = time;
            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.run = false;
            }
            scanThread = new ScanThread();
            scanThread.run = true;
            scanThread.start();
        }
    }

    /**
     * 闭光停止扫描
     */
    public synchronized void Barcode_Continue_Stop() {
        // Log.d(TAG, "Barcode_Continue_Stop()");
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.run = false;
        }
        scanThread = null;
    }

    private void notifyScanReader() {
        if (scanThread != null && scanThread.isAlive()) {
            scanThread.interrupt();
        }
    }

    // start a snap/preview session
    public void doSnap() {
//			if (setIdle() != STATE_IDLE)
//				return;
//
//			resetTrigger();
//			mScanListener.ScannedResult("");
//			if (snapPreview)		//snapshot-preview mode?
//			{
//				state = STATE_PREVIEW;
//				videoCapDisplayStarted = false;
//				mScanListener.ScannedStatus("Snapshot Preview");
//				bcr.startViewFinder(this);
//			}
//			else
//			{
//				state = STATE_SNAPSHOT;
//				snapScreen(null);
//				bcr.takePicture(app);
//			}
    }

    // ----------------------------------------
    // take snapshot
    public void doSnap1() {
//			if (state == STATE_PREVIEW)
//			{
//				bcr.stopPreview();
//				state = STATE_SNAPSHOT;
//			}
//			if (state == STATE_SNAPSHOT)
//			{
//				snapScreen(null);
//				bcr.takePicture(app);
//			}
//			else //unexpected state - reset mode
//			{
//				setIdle();
//				mainScreen();
//			}
    }

    // ----------------------------------------
    // get param
    public int doGetParam(int num) {
        setIdle();
        int val = bcr.getNumParameter(num);
        return val;
    }

    // ----------------------------------------
    // set param
    public int doSetParam(int num, int val) {
        setIdle();
        String s = "";
        int ret = bcr.setParameter(num, val);
        if (ret != BarCodeReader.BCR_ERROR) {
            if (num == BarCodeReader.ParamNum.PRIM_TRIG_MODE) {
                trigMode = val;
                if (val == BarCodeReader.ParamVal.HANDSFREE) {
                    s = "HandsFree";
                } else if (val == BarCodeReader.ParamVal.AUTO_AIM) {
                    s = "AutoAim";
                    ret = bcr.startHandsFreeDecode(BarCodeReader.ParamVal.AUTO_AIM);
                    if (ret != BarCodeReader.BCR_SUCCESS) {
                        mScanListener.ScannedStatus("AUtoAIm start FAILED");
                    }
                } else if (val == BarCodeReader.ParamVal.LEVEL) {
                    s = "Level";
                }
            } else if (num == BarCodeReader.ParamNum.IMG_VIDEOVF) {
                if (snapPreview = (val == 1))
                    s = "SnapPreview";
            }
        } else
            s = " FAILED (" + ret + ")";

        mScanListener.ScannedStatus("Set #" + num + " to " + val + " " + s);
        return ret;
    }

    public void disableAllCodeTypes() {
        bcr.disableAllCodeTypes();
    }

    public void enableAllCodeTypes() {
        bcr.enableAllCodeTypes();
    }

    public void setPlayBeep(boolean beep) {
        Preference.setScanSound(this, beep);
        if (mBeepManagersdl != null)
            mBeepManagersdl.setPlayBeep(beep);

    }

    public void startViewFinder() {
        if (state == STATE_PREVIEW) {
            //bcr.setPreviewDisplay(holder);
            bcr.startViewFinder(this);            //snapshot with preview mode
        } else //must be video
        {
            //bcr.setPreviewDisplay(holder);
            //bcr.startVideoCapture(this);
            bcr.startPreview();
        }
    }

    @Override
    // ----------------------------------------
    public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
        switch (event) {
            case BarCodeReader.BCRDR_EVENT_SCAN_MODE_CHANGED:
                ++modechgEvents;
                if (mScanListener != null) {
                    try {
                        mScanListener.ScannedStatus("Scan Mode Changed Event (#" + modechgEvents + ")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case BarCodeReader.BCRDR_EVENT_MOTION_DETECTED:
                ++motionEvents;
                if (mScanListener != null) {
                    try {
                        mScanListener.ScannedStatus("Motion Detect Event (#" + motionEvents + ")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case BarCodeReader.BCRDR_EVENT_SCANNER_RESET:
                if (mScanListener != null) {
                    try {
                        mScanListener.ScannedStatus("Reset Event");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                // process any other events here
                break;
        }
    }

    @Override
    public void onError(int error, BarCodeReader reader) {


    }

    @Override
    public void onVideoFrame(int format, int width, int height, byte[] data,
                             BarCodeReader reader) {


    }
}
