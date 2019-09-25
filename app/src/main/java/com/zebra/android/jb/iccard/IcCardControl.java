package com.zebra.android.jb.iccard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import com.zebra.android.jb.utils.AddressUtils;
import android.util.Log;
import android_serialport_api.SerialPort;

public class IcCardControl {

	public final static int IdCardComReadId = 1;

	private File power = new File("/proc/jbcommon/gpio_control/id_card_en");
	private String serialPort_Path = "/dev/ttyMT1"; // 串口地址
	public final static String TAG = "RS232Controller";

	private Context mContext;
	private static IcCardControl idcardCon;
	private SerialPort sP;
	private OutputStream out;
	private InputStream in;
	private ReadThread readThread;

	private CmdProcessThread cmdProcessThread;
	//private Callback l;
	private static Object lock = new Object();

	private final byte[] CMD_READ_ID = { 0x69, (byte) 0x96, 0x00, 0x01, 0x53, 0x53 };

	private final String idcard_error = "69960002530155";
	public final static String IdCardErrorToUer = "ffffff";

	private boolean cardInsert = false;
	/**
	 * 串口数据回调接口
	 * 
	 * @author Ivan.Wang 2015-4-18
	 */
	/*private interface Callback {
		void IdCard_Read(String data);
	}*/

	private IcCardControl() {
	}

	public static IcCardControl getInstance() {
		if (idcardCon == null) {
			synchronized (lock) {
				if (idcardCon == null) {
					idcardCon = new IcCardControl();
				}
			}
		}
		return idcardCon;
	}

	private synchronized void writeFile(File file, String value) {

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

	/**
	 * 写数据
	 * 
	 * @param data
	 */
	private void IcCard_Write(byte[] data) {
		if (out != null) {
			try {
				out.write(data);
				out.flush();
				System.out.println("write:" + bytesToHexString(data, 0, data.length));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.e("jiebao", "write filed:" + e.getMessage());
			}

		}
	}

	private void IcCardSendCMD(int command) {
		switch (command) {
		case IdCardComReadId:
			IcCard_Write(CMD_READ_ID);
			break;
		default:
			break;
		}
	}

	/**
	 * Convert bytes to string,actually display only
	 *
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
			stringBuilder.append(hv + " ");
		}
		return stringBuilder.toString();
	}

	/**
	 * idCard模块上电
	 */
	private void power_up() {
		writeFile(power, "1");
	}

	/**
	 * idCard模块下电
	 */
	private void power_down() {
		writeFile(power, "0");
	}

	/**
	 * 初始化串口
	 * 
	 * @param baud     波特率
	 * @param bits     数据位
	 * @param event    校验位
	 * @param stopBits 停止位
	 */
	private void IcCardOpen(int baud, int bits, char event, int stopBits, Context context) {
//		//发送广播 关闭扫描服务
//		context.sendBroadcast(new Intent("ReleaseCom"));

		mContext = context;
		power = new File(AddressUtils.IC_CARD_EN);// 无线psam_en,有线rs232_pwr_en
		serialPort_Path = "/dev/ttyMT1"; // 串口地址

		//this.l = l;

		try {
			IcCard_Close();
			power_up();
			IO_OE("0");
			Thread.sleep(100);
			sP = new SerialPort(new File(serialPort_Path), baud, bits, event, stopBits, 0);
			in = sP.getInputStream();
			out = sP.getOutputStream();
			readThread = new ReadThread();
			readThread.start();


			CmdProcessThread cmdProcessThread = new CmdProcessThread();
			cmdProcessThread.start();
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException("open port failed:" + e.getMessage());
		}

	}

	public void IcCard_Open( Context context) {
		IcCardOpen(115200, 8, 'N', 1,  context);
	}

	/**
	 * 关闭串口
	 */
	public void IcCard_Close() {

		power_down();

		if (readThread != null) {
			readThread.isThreadRun = false;
			readThread = null;
		}

		
		if (cmdProcessThread != null) {
			cmdProcessThread.isThreadRun = false;
			cmdProcessThread = null;
		}

		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (sP != null)
				sP.close();

			in = null;
			out = null;
			sP = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/*private void idCardDealData(byte[] data) {

		String dealData = Tools.bytesToHexString(data);
		Log.e(TAG, "raw data = " + dealData);
		if (dealData.equals(idcard_error)) {
			if (l != null) {
				l.IdCard_Read(IdCardErrorToUer);
			}
		} else {
			int dataNum = (data[3] - 0x02) * 2;
			if ((data[data.length - 2] & 0xff) == 0x0f) {
				dataNum--;
			}
			String endData = dealData.substring(12, 12 + dataNum);
			if (l != null) {
				l.IdCard_Read(endData);
			}
		}
	}
*/
	private class ReadThread extends Thread {
		private boolean isThreadRun = true;

		@Override
		public void run() {

			while (isThreadRun) {
				try {
					SerialAnalyze();
					Thread.sleep(50);

					if (in == null) {
						return;
					}
					int size;
					int cout = in.available();
					byte[] buffer1;
					// byte[] buffer1 = new byte[1024];
					if (cout > 0) {

						cout = in.available();
						buffer1 = new byte[cout];
						size = in.read(buffer1);

						if (size > 0) {
							readData(buffer1);
							// idCardDealData(buffer1);
						}
					} else {
						continue;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * private void notifyReader() { if (readThread != null && readThread.isAlive())
	 * { readThread.interrupt(); } }
	 */

	private void IO_OE(String status) {
		System.out.println("IO_OE:" + AddressUtils.SCAN_IR_GPS_RS232_SWITCH_OE + " " + status);
		writeFile(new File(AddressUtils.SCAN_IR_GPS_RS232_SWITCH_OE), status);
		Log.e("kaka","IO_OE  00");
	}

	private final int UART_MAX_BUFFER_LENGTH = 8000;
	private int rx_w, rx_r;
	private byte[] RxBuffer = new byte[UART_MAX_BUFFER_LENGTH];
	private SerialReceiveData_Frame m_Msg = new SerialReceiveData_Frame();
	private SerialSendData_Frame waiting_SerialSendData = new SerialSendData_Frame();;
	private final int waitingTime = 10;// 10 *100 ms
	
	private class SerialReceiveData_Frame {
		int BufferCount = 0;
		int DataCount = 0;
		byte CheckSum = 0;
		boolean bFrameReady = false;
		byte[] Buffer = new byte[4096];
	};

	private class SerialSendData_Frame {
		int cmd = 0;
		int ack = 0;
		byte[] Buffer = new byte[4096];
	};

	private void readData(byte[] byteArray) {
//		byte[] byteArray = mBluetoothLeService.hex2byte(data1.getBytes());

		// Log.v("kaka", "readData: " + data1);
		if (byteArray != null && byteArray.length > 0) {

			for (int i = 0; i < byteArray.length; i++) {
				if (rx_w < rx_r) {
					if ((rx_w + 1) < (rx_r)) {
						RxBuffer[rx_w++] = byteArray[i];
					}
				} else {
					if (rx_w >= UART_MAX_BUFFER_LENGTH)
						rx_w = 0;
					RxBuffer[rx_w++] = byteArray[i];
				}

			}
		}

	}

	private void SerialAnalyze() {
		int r_cnt;
		int i, k, j;
		int w_len;

		byte crc = 0;

		if (m_Msg.bFrameReady == true) {
			return;
		}

		for (i = 0; i < 10; i++) {

			if (rx_r < rx_w) {
				w_len = rx_w - rx_r;
			} else if (rx_r > rx_w) {
				w_len = UART_MAX_BUFFER_LENGTH - rx_r + rx_w;
			} else {
				break;
			}

			if (m_Msg.BufferCount == 0) {
				if (w_len < 6) {
					return;
				}
				r_cnt = rx_r;
				rx_r++;

				if (rx_r == UART_MAX_BUFFER_LENGTH)
					rx_r = 0;

				if (RxBuffer[r_cnt] == 0x7e) {
					m_Msg.Buffer[0] = RxBuffer[r_cnt++];
				} else {
					// RxBuffer[r_cnt] = 0;
					continue;
				}

				if (r_cnt == UART_MAX_BUFFER_LENGTH)
					r_cnt = 0;

				for (j = 0; j < 4; j++) {
					m_Msg.Buffer[1 + j] = RxBuffer[r_cnt++];

					if (r_cnt == UART_MAX_BUFFER_LENGTH)
						r_cnt = 0;
				}

				m_Msg.DataCount = m_Msg.Buffer[1] * 256 + m_Msg.Buffer[2];

				if ((m_Msg.DataCount > 2048) || (m_Msg.DataCount < 2)) {
					m_Msg.BufferCount = 0;
					m_Msg.DataCount = 0;
					continue;
				}

				m_Msg.BufferCount = 5;

			}

			if (m_Msg.BufferCount == 5) {

				r_cnt = rx_r;

				for (j = 0; j < (m_Msg.BufferCount - 1); j++) {
					r_cnt += 1;
					if (r_cnt == UART_MAX_BUFFER_LENGTH)
						r_cnt = 0;
				}

				if (r_cnt <= rx_w) {
					w_len = rx_w - r_cnt;
				} else if (r_cnt > rx_w) {
					w_len = UART_MAX_BUFFER_LENGTH - r_cnt + rx_w;
				}

				if (w_len < (m_Msg.DataCount))
					continue;
				crc = 0;
				for (j = 1; j < 5; j++) {
					crc ^= m_Msg.Buffer[j];
				}
				k = m_Msg.BufferCount;

				for (j = 0; j < (m_Msg.DataCount - 2); j++) {

					crc ^= RxBuffer[r_cnt];
					m_Msg.Buffer[k++] = RxBuffer[r_cnt++];

					if (r_cnt == UART_MAX_BUFFER_LENGTH)
						r_cnt = 0;
				}

				for (j = 0; j < 2; j++) {
					m_Msg.Buffer[k++] = RxBuffer[r_cnt++];
					if (r_cnt == UART_MAX_BUFFER_LENGTH)
						r_cnt = 0;
				}

				// -----------校验和---------------
				if (crc != m_Msg.Buffer[k - 2]) {
					m_Msg.BufferCount = 0;
					continue;
				}

				if (m_Msg.Buffer[k - 1] == 0x0A) {
					m_Msg.bFrameReady = true;
					m_Msg.BufferCount = k;
					RxBuffer[rx_r - 1] = 0;
					for (k = 1; k < m_Msg.BufferCount; k++) {
						RxBuffer[rx_r] = 0;
						rx_r++;
						if (rx_r == UART_MAX_BUFFER_LENGTH)
							rx_r = 0;
					}
					break;
				} else
					m_Msg.BufferCount = 0;

			} else
				m_Msg.BufferCount = 0;

		}

	}

	private int SendFrame(int order, int suborder, byte[] pMessag, int MsgSize, int ack) {
		int i;
		byte crc = 0;
		byte[] buffer = new byte[MsgSize + 7];

		buffer[0] = 0x7e;
		buffer[1] = (byte) ((MsgSize + 2) >> 8);
		buffer[2] = (byte) ((MsgSize + 2) & 0x00ff);

		buffer[3] = (byte) order;
		buffer[4] = (byte) suborder;

		for (i = 0; i < MsgSize; i++)
			buffer[5 + i] = pMessag[i];

		MsgSize += 4;

		for (i = 0; i < MsgSize; i++)
			crc ^= (buffer[i + 1]) & 0xff;
		buffer[MsgSize + 1] = crc;
		buffer[MsgSize + 2] = 0x0A;
		
		if (ack == 1) {
			waiting_SerialSendData.cmd = order  * 256 + suborder;
		} else
			waiting_SerialSendData.cmd = -1;
		sendDatas(buffer);
		
		
		return 0;
	}

	private class CmdProcessThread extends Thread {
		private boolean isThreadRun = true;

		@Override
		public void run() {

			while (isThreadRun) {
				try {

					Thread.sleep(50);
					if (m_Msg.bFrameReady == true) {

						int command = (m_Msg.Buffer[3] & 0xff) * 256 + (m_Msg.Buffer[4] & 0xff);
						
						switch (command) {
						case 0x0000:
							if(m_Msg.Buffer[5] == 1)
								cardInsert = true;
							else
								cardInsert = false;
							break;
						case 0x0002:
						case 0x0101:
						case 0x0102:
						case 0x0200:
						case 0x0201:
						case 0x0202:
						case 0x0301:	
							Log.e("kaka", "in-:" + bytesToHexString(m_Msg.Buffer, 0, m_Msg.BufferCount));
								if ((waiting_SerialSendData.cmd != -1) && (waiting_SerialSendData.cmd == command)
									&&(waiting_SerialSendData.ack != 1)) {
								waiting_SerialSendData.Buffer = new byte[m_Msg.BufferCount];
								System.arraycopy(m_Msg.Buffer, 0, waiting_SerialSendData.Buffer, 0, m_Msg.BufferCount);
								waiting_SerialSendData.ack = 1;
								//Log.e("kaka", "in2:" + bytesToHexString(waiting_SerialSendData.Buffer, 0, waiting_SerialSendData.Buffer.length));
								
							}
							break;

						}

						m_Msg.BufferCount = 0;
						m_Msg.DataCount = 0;
						m_Msg.bFrameReady = false;
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	

	private synchronized void sendDatas(byte[] datas)
	{

		try {
			if (out != null) {
				out.write(datas);
				out.flush();
				Log.e("kaka", "out:" + bytesToHexString(datas, 0, datas.length));
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		

	}

	private byte[] waitingResponse(int cmd) {
		byte[] buffer = null;
		int cnt = waitingTime;
		while (cnt > 0) {
			cnt--;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			if ((waiting_SerialSendData.cmd == cmd) && (waiting_SerialSendData.ack == 1)) {
				buffer = waiting_SerialSendData.Buffer;
				waiting_SerialSendData.ack = 0;
				break;
			}

		}

		return buffer;
	}

	
	
	public boolean isCardInserted() {
		return cardInsert;
	}
	
	public String GetMcuVer() {
		String ver = null;
		byte[] cmdbuffer = new byte[1];
		cmdbuffer[0] = 0;
		SendFrame(0x00, 0x02, cmdbuffer, 1, 1);
		byte[] revBuffer = waitingResponse(0x0002);
		
		if(revBuffer != null) {
			if ( revBuffer[5] > 0  && (revBuffer.length==(revBuffer[5]+8))) {
	
				byte buffer[] = new byte[revBuffer[5] + 2];
			
				System.arraycopy(revBuffer, 6, buffer, 0, revBuffer[5]);
				//Log.e("kaka","len:"+m_Msg.Buffer[5]+"  "+bytesToHexString(datas, 0, datas.length));
				try {
					ver = new String(buffer, 0, revBuffer[5], "GB2312");
				} catch (UnsupportedEncodingException e) {
					Log.e("kaka", e.toString());
					e.printStackTrace();
				}
				Log.e("kaka", "ver:" + ver);
			}else {
					Log.e("jiebao","GetMcuVer revBuffer length error! len:"+revBuffer[5]+" / "+revBuffer.length);
			}
		}
		
		return ver;
	}

	/**
	 * AT24C_SetCardAddressLenght 功能：设置AT24C 卡片地址长度，默认为一个字节地址长度对应AT24C01-AT24C16，二个字节地址长度对应AT24C32-AT24C512
	 * @param lenght  卡片地址长度 1~2
	 * @return 0：设置成功， 1设置 失败。
	 */
	public int AT24C_SetCardAddressLenght(int lenght) {
		int res = -1;
		byte[] cmdbuffer = new byte[4];
		if (lenght != 2)
			lenght = 1;
		cmdbuffer[0] = (byte) lenght;

		res = SendFrame(0x01, 0x00, cmdbuffer, 1, 1);

		return res;
	}

	/**
	 * AT24C_Read 功能：读取AT24C 卡片数据
	 * 
	 * @param address 数据读取地址
	 * @param lenght  读取长度
	 * @return byte[] 为null 读取失败。
	 */
	public byte[] AT24C_Read(int address, int lenght) {
		byte[] buffer = null;
		byte[] cmdbuffer = new byte[4];
		cmdbuffer[0] = (byte) ((address >> 8) & 0x00ff);
		cmdbuffer[1] = (byte) (address & 0x00ff);
		cmdbuffer[2] = (byte) ((lenght >> 8) & 0x00ff);
		cmdbuffer[3] = (byte) (lenght & 0x00ff);
		SendFrame(0x01, 0x01, cmdbuffer, 4, 1);
		byte[] revBuffer = waitingResponse(0x0101);

	
		if (revBuffer != null) {
			int len = (revBuffer[5] & 0xff) * 256 + revBuffer[6];
			if (len > 0 && (revBuffer.length>=(len+7)) ) {
				buffer = new byte[len];			
				System.arraycopy(revBuffer, 7, buffer, 0, len);
			}else {
				Log.e("jiebao","AT24C_Read revBuffer length error! len:"+len+" / "+revBuffer.length);
			}
		}
		return buffer;

	}

	/**
	 * AT24C_Write 功能：写入AT24C 卡片数据
	 * 
	 * @param address 数据写入地址
	 * @param datas   数据源
	 * @param lenght  写入长度
	 * @return 0：写入成功， 1写入失败 ，2 datas数据源错误
	 */
	public int AT24C_Write(int address, byte[] datas, int lenght) {

		if (datas == null)
			return 2;
		if ((datas.length == 0) || (datas.length < lenght))
			return 2;

		byte[] cmdbuffer = new byte[4 + lenght];
		cmdbuffer[0] = (byte) ((address >> 8) & 0x00ff);
		cmdbuffer[1] = (byte) (address & 0x00ff);
		cmdbuffer[2] = (byte) ((lenght >> 8) & 0x00ff);
		cmdbuffer[3] = (byte) (lenght & 0x00ff);
		System.arraycopy(datas, 0, cmdbuffer, 4, lenght);
		SendFrame(0x01, 0x02, cmdbuffer, lenght + 4, 1);

		byte[] revBuffer = waitingResponse(0x0102);

		if (revBuffer != null && revBuffer[5] == 0) {
			return 0;
		}
		return 1;

	}

	/**
	 * Sle4442_ReadPwdCnt 功能：读取 Sle4442 卡片密码错误次数
	 * 
	 * @return 卡片密码错误次数 0~3 ，0xff为读取失败。
	 */
	public int Sle4442_ReadPwdCnt() {
		int res = 0xff;
		byte[] cmdbuffer = new byte[1];
		cmdbuffer[0] = (byte) 0;

		SendFrame(0x02, 0x00, cmdbuffer, 1, 1);
		byte[] revBuffer = waitingResponse(0x0200);

		if (revBuffer != null) {
			res = revBuffer[5]&0xff;

		}
		return res;

	}

	/**
	 * AT24C_Read 功能：读取Sle4442 卡片数据
	 * 
	 * @param address 数据读取地址
	 * @param lenght  读取长度
	 * @return byte[] 为null 读取失败。
	 */
	public byte[] Sle4442_Read(int address, int lenght) {
		byte[] buffer = null;
		byte[] cmdbuffer = new byte[4];
		cmdbuffer[0] = (byte) ((address >> 8) & 0x00ff);
		cmdbuffer[1] = (byte) (address & 0x00ff);
		cmdbuffer[2] = (byte) ((lenght >> 8) & 0x00ff);
		cmdbuffer[3] = (byte) (lenght & 0x00ff);
		SendFrame(0x02, 0x01, cmdbuffer, 4, 1);
		byte[] revBuffer = waitingResponse(0x0201);

			
		if (revBuffer != null) {
			int len = (revBuffer[5] & 0xff) * 256 + revBuffer[6];
			if (len > 0 && (revBuffer.length>=(len+7)) ) {
				buffer = new byte[len];			
				System.arraycopy(revBuffer, 7, buffer, 0, len);
			}else {
				Log.e("jiebao","Sle4442_Read revBuffer length error! len:"+len+" / "+revBuffer.length);
			}
		}
		return buffer;

	}

	/**
	 * Sle4442_Write 功能：写入Sle4442 卡片数据
	 * 
	 * @param address 数据写入地址
	 * @param datas   数据源
	 * @param lenght  写入长度
	 * @param psw     卡片密码 三个字节
	 * @return 0：写入成功， 1写入失败 ，2 datas数据源错误 ,3 密码错误
	 */
	public int Sle4442_Write(int address, byte[] datas, int lenght, byte[] psw) {
		if (datas == null)
			return 2;
		if ((datas.length == 0) || (datas.length < lenght))
			return 2;

		if ((psw == null) || (psw.length < 3))
			return 3;

		byte[] cmdbuffer = new byte[7 + lenght];
		int i;
		for (i = 0; i < 3; i++)
			cmdbuffer[i] = psw[i];

		cmdbuffer[i++] = (byte) ((address >> 8) & 0x00ff);
		cmdbuffer[i++] = (byte) (address & 0x00ff);
		cmdbuffer[i++] = (byte) ((lenght >> 8) & 0x00ff);
		cmdbuffer[i++] = (byte) (lenght & 0x00ff);

		System.arraycopy(datas, 0, cmdbuffer, i, lenght);
		SendFrame(0x02, 0x02, cmdbuffer, lenght + i, 1);

		byte[] revBuffer = waitingResponse(0x0202);

		if (revBuffer != null) {
			if (revBuffer[5] == 0)
				return 0;
			else if (revBuffer[5] == 2)
				return 3;
		}
		return 1;

	}
	
	/**
	 * AT24C_Read 功能：读取 EMV 卡片Id号	 
	 * @return byte[] 为null 读取失败。
	 */
	public byte[] EMV_Read_Id() {
		byte[] buffer = null;
		byte[] cmdbuffer = new byte[4];
		int address = 0;
		int lenght = 1;
		cmdbuffer[0] = (byte) ((address >> 8) & 0x00ff);
		cmdbuffer[1] = (byte) (address & 0x00ff);
		cmdbuffer[2] = (byte) ((lenght >> 8) & 0x00ff);
		cmdbuffer[3] = (byte) (lenght & 0x00ff);
		SendFrame(0x03, 0x01, cmdbuffer, 4, 1);
		byte[] revBuffer = waitingResponse(0x0301);

		if (revBuffer != null) {
			int len = (revBuffer[5] & 0xff) * 256 + revBuffer[6];
			if (len > 0 && (revBuffer.length>=(len+7)) ) {
				buffer = new byte[len];			
				System.arraycopy(revBuffer, 7, buffer, 0, len);
			}else {
				Log.e("jiebao","EMV_Read revBuffer length error! len:"+len+" / "+revBuffer.length);
			}
		}
		return buffer;

	}
}
