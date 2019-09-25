package com.zebra.android.jb.identitycard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import com.zebra.android.jb.utils.AddressUtils;
import com.zebra.android.jb.utils.Tools;
import android.util.Log;
import android_serialport_api.SerialPort;


public class IdentityCardController {

	

	public final static int IdCardComReadId = 1;
	
	
	private File power = new File("/sys/devices/platform/uhf/rs232");
	private String serialPort_Path = "/dev/ttyMT1"; // 串口地址
	public final static String TAG = "RS232Controller";

	private Context mContext;
	private static IdentityCardController idcardCon;
	private SerialPort sP;
	private OutputStream out;
	private InputStream in;
	private volatile boolean run;
	private Callback l;
	private static Object lock = new Object();

	private final byte[] CMD_READ_ID = {0x69, (byte)0x96, 0x00, 0x01, 0x53, 0x53};
	
	private final String idcard_error = "69960002530155";
	public final static String IdCardErrorToUer = "ffffff";
	/**
	 * 串口数据回调接口
	 * 
	 * @author Ivan.Wang 2015-4-18
	 */
	public interface Callback {
		void IdCard_Read(String data);
	}

	private IdentityCardController() {
	}

	public static IdentityCardController getInstance() {
		if (idcardCon == null) {
			synchronized (lock) {
				if (idcardCon == null) {
					idcardCon = new IdentityCardController();
				}
			}
		}
		return idcardCon;
	}

	/**
	 * Convert bytes to string,actually display only
	 * 
	 * @param bytes
	 * @return String
	 */
	private String bytesToHexString(byte[] src, int start, int size) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = start; i < size; i++) {
			int v = src[i] & 0xFF;

			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

    public static void powerOn(){
    	
        IO_OE("1");
        IO_CS0("0");
        IO_CS1("1");
        power("1");
        BACK_OE("1");
        BACK_CS0("1");
        BACK_CS1("1");
        ufsEnable("1");
        
    }


    public static void powerDown(){
        IO_OE("0");
        IO_CS0("0");
        IO_CS1("0");
        power("0");
        BACK_OE("0");
        BACK_CS0("0");
        BACK_CS1("0");
        ufsEnable("0");
    }


    private static void writeFile(File file, String value) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(value);
            writer.flush();
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void IO_OE(String status) {

        writeFile(new File(AddressUtils.SCAN_IR_GPS_RS232_SWITCH_OE), status);
    }

    private static void power(String p) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        writeFile(new File(AddressUtils.IC_CARD_EN), p);
    }

    private static void IO_CS1(String status) {

        System.out.println("power:" + AddressUtils.SCAN_IR_GPS_RS232_CS1 + " " + status);
        writeFile(new File(AddressUtils.SCAN_IR_GPS_RS232_CS1), status);
    }

    private static void IO_CS0(String status) {

        System.out.println("power:" + AddressUtils.SCAN_IR_GPS_RS232_CS0 + " " + status);
        writeFile(new File(AddressUtils.SCAN_IR_GPS_RS232_CS0), status);
    }

    private static void BACK_CS0(String status) {

        System.out.println("BACK_CS0:" + AddressUtils.BACK_UART_CS0 + " " + status);
        writeFile(new File(AddressUtils.BACK_UART_CS0), status);
    }
    private static void BACK_CS1(String status) {

        System.out.println("BACK_CS1:" + AddressUtils.BACK_UART_CS1 + " " + status);
        writeFile(new File(AddressUtils.BACK_UART_CS1), status);
    }

    private static void BACK_OE(String status) {

        System.out.println("BACK_OE:" + AddressUtils.BACK_UART_OE + " " + status);
        writeFile(new File(AddressUtils.BACK_UART_OE), status);
    }

    private static void ufsEnable(String status) {

        System.out.println("ufsEnable:" + AddressUtils.RFID_ENABLE + " " + status);
        writeFile(new File(AddressUtils.RFID_ENABLE), status);
    }
}
