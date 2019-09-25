/*
 * Copyright 2014 ShangDao.Ltd  All rights reserved.
 * SiChuan ShangDao.Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @BaseApplication.java  2014-2-28 上午10:37:51 - Carson
 * @author YanXu
 * @email:981385016@qq.com
 * @version 1.0
 */

package com.zebra.main.interfac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.zebra.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class BaseApplication extends Application {

    private static BaseApplication mAppInstance;
    public static int mWidth;
    public static int mHeight;
    public Handler mHomeHandler;
    public static final boolean IS_DONATE_VERSION = false;
    public static final String HOME_DIR = "/MifareClassicTool";
    public static final String KEYS_DIR = "key-files";
    public static final String DUMPS_DIR = "dump-files";
    public static final String TMP_DIR = "tmp";
    public static final String STD_KEYS = "std.keys";
    public static final String STD_KEYS_EXTENDED = "extended-std.keys";
    private static final String LOG_TAG = BaseApplication.class.getSimpleName();
    private static Tag mTag = null;
    private static byte[] mUID = null;
    private static SparseArray<byte[][]> mKeyMap = null;
    private static int mKeyMapFrom = -1;
    private static int mKeyMapTo = -1;
    private static String mVersionCode;
    private static NfcAdapter mNfcAdapter;
    private static Context mAppContext;

    public enum Operations {
        Read, Write, Increment, DecTransRest, ReadKeyA, ReadKeyB, ReadAC, WriteKeyA, WriteKeyB, WriteAC
    }

    public static BaseApplication getAppContext() {
        return mAppInstance;
    }

    public static String getCMD() {
        return mAppInstance.getPackageName();
        // + mAppInstance.getString(R.string.app_id);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
        mAppContext = getApplicationContext();
        try {
            mVersionCode = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;
        } catch (NameNotFoundException e) {
            Log.d(LOG_TAG, "Version not found.");
        }
        initDeviceType();
//		LogcatHelper.getInstance(this).start();
    }

    private void initDeviceType() {
        DisplayMetrics dis = getResources().getDisplayMetrics();
        mWidth = dis.widthPixels;
        mHeight = dis.heightPixels;
        double x = Math.pow(mWidth / dis.xdpi, 2);
        double y = Math.pow(mHeight / dis.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        // if (screenInches > 7.0) {
        // ShareCookie.setPhoneType("1");
        // } else {
        // ShareCookie.setPhoneType("0");
        // }
    }

    public static boolean isExternalStorageWritableErrorToast(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        }
        Toast.makeText(context, R.string.info_no_external_storage,
                Toast.LENGTH_LONG).show();
        return false;
    }

    public static String[] readFileLineByLine(File file, boolean readComments,
                                              Context context) {
        BufferedReader br = null;
        String[] ret = null;
        if (file != null && file.exists()) {
            try {
                br = new BufferedReader(new FileReader(file));

                String line;
                ArrayList<String> linesArray = new ArrayList<String>();
                while ((line = br.readLine()) != null) {
                    // Ignore empty lines.
                    // Ignore comments if readComments == false.
                    if (!line.equals("")
                            && (readComments || !line.startsWith("#"))) {
                        try {
                            linesArray.add(line);
                        } catch (OutOfMemoryError e) {
                            // Error. File is too big
                            // (too many lines, out of memory).
                            Toast.makeText(context, R.string.info_file_to_big,
                                    Toast.LENGTH_LONG).show();
                            return null;
                        }
                    }
                }
                if (linesArray.size() > 0) {
                    ret = linesArray.toArray(new String[linesArray.size()]);
                } else {
                    ret = new String[]{""};
                }
            } catch (Exception e) {
                Log.e(LOG_TAG,
                        "Error while reading from file " + file.getPath() + ".",
                        e);
                ret = null;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error while closing file.", e);
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }

    public static boolean saveFile(File file, String[] lines, boolean append) {
        boolean noError = true;
        if (file != null && lines != null) {
            if (append) {
                // Append to a existing file.
                String[] newLines = new String[lines.length + 4];
                System.arraycopy(lines, 0, newLines, 4, lines.length);
                newLines[0] = "";
                newLines[1] = "";
                newLines[2] = "# Append #######################";
                newLines[3] = "";
                lines = newLines;
            }

            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file, append));
                int i;
                for (i = 0; i < lines.length - 1; i++) {
                    bw.write(lines[i]);
                    bw.newLine();
                }
                bw.write(lines[i]);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while writing to '" + file.getName()
                        + "' file.", e);
                noError = false;

            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error while closing file.", e);
                        noError = false;
                    }
                }
            }
        } else {
            noError = false;
        }
        return noError;
    }

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public static void enableNfcForegroundDispatch(Activity targetActivity) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {

            Intent intent = new Intent(targetActivity,
                    targetActivity.getClass())
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    targetActivity, 0, intent, 0);
            mNfcAdapter.enableForegroundDispatch(targetActivity, pendingIntent,
                    null,
                    new String[][]{new String[]{NfcA.class.getName()}});
        }
    }

    public static void disableNfcForegroundDispatch(Activity targetActivity) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.disableForegroundDispatch(targetActivity);
        }
    }

    public static int treatAsNewTag(Intent intent, Context context) {
        // Check if Intent has a NFC Tag.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            setTag(tag);

            // Show Toast message with UID.
            String id = context.getResources().getString(
                    R.string.info_new_tag_found)
                    + " (UID: ";
            id += byte2HexString(tag.getId());
            id += ")";
            Toast.makeText(context, id, Toast.LENGTH_LONG).show();
            return checkMifareClassicSupport(tag, context);
        }
        return -4;
    }

    public static int checkMifareClassicSupport(Tag tag, Context context) {
        if (tag == null || context == null) {
            // Error.
            return -3;
        }

        if (Arrays.asList(tag.getTechList()).contains(
                MifareClassic.class.getName())) {
            // Device and tag support Mifare Classic.
            return 0;
        } else if (context.getPackageManager().hasSystemFeature(
                "com.nxp.mifare")) {
            // Tag does not support Mifare Classic.
            return -2;
        } else {
            NfcA nfca = NfcA.get(tag);
            byte[] atqa = nfca.getAtqa();
            if (atqa[1] == 0
                    && (atqa[0] == 4 || atqa[0] == (byte) 0x44 || atqa[0] == 2 || atqa[0] == (byte) 0x42)) {
                // ATQA says it is most likely a Mifare Classic tag.
                byte sak = (byte) nfca.getSak();
                if (sak == 8 || sak == 9 || sak == (byte) 0x18) {
                    // SAK says it is most likely a Mifare Classic tag.
                    // --> Device does not support Mifare Classic.
                    return -1;
                }
            }
            // Nope, it's not the device (most likely).
            // The tag does not support Mifare Classic.
            return -2;
        }
    }

    public static int getOperationInfoForBlock(byte c1, byte c2, byte c3,
                                               Operations op, boolean isSectorTrailer, boolean isKeyBReadable) {
        // Is Sector Trailer?
        if (isSectorTrailer) {
            // Sector Trailer.
            if (op != Operations.ReadKeyA && op != Operations.ReadKeyB
                    && op != Operations.ReadAC && op != Operations.WriteKeyA
                    && op != Operations.WriteKeyB && op != Operations.WriteAC) {
                // Error. Sector Trailer but no Sector Trailer permissions.
                return 4;
            }
            if (c1 == 0 && c2 == 0 && c3 == 0) {
                if (op == Operations.WriteKeyA || op == Operations.WriteKeyB
                        || op == Operations.ReadKeyB || op == Operations.ReadAC) {
                    return 1;
                }
                return 0;
            } else if (c1 == 0 && c2 == 1 && c3 == 0) {
                if (op == Operations.ReadKeyB || op == Operations.ReadAC) {
                    return 1;
                }
                return 0;
            } else if (c1 == 1 && c2 == 0 && c3 == 0) {
                if (op == Operations.WriteKeyA || op == Operations.WriteKeyB) {
                    return 2;
                }
                if (op == Operations.ReadAC) {
                    return 3;
                }
                return 0;
            } else if (c1 == 1 && c2 == 1 && c3 == 0) {
                if (op == Operations.ReadAC) {
                    return 3;
                }
                return 0;
            } else if (c1 == 0 && c2 == 0 && c3 == 1) {
                if (op == Operations.ReadKeyA) {
                    return 0;
                }
                return 1;
            } else if (c1 == 0 && c2 == 1 && c3 == 1) {
                if (op == Operations.ReadAC) {
                    return 3;
                }
                if (op == Operations.ReadKeyA || op == Operations.ReadKeyB) {
                    return 0;
                }
                return 2;
            } else if (c1 == 1 && c2 == 0 && c3 == 1) {
                if (op == Operations.ReadAC) {
                    return 3;
                }
                if (op == Operations.WriteAC) {
                    return 2;
                }
                return 0;
            } else if (c1 == 1 && c2 == 1 && c3 == 1) {
                if (op == Operations.ReadAC) {
                    return 3;
                }
                return 0;
            } else {
                return -1;
            }
        } else {
            // Data Block.
            if (op != Operations.Read && op != Operations.Write
                    && op != Operations.Increment
                    && op != Operations.DecTransRest) {
                // Error. Data block but no data block permissions.
                return -1;
            }
            if (c1 == 0 && c2 == 0 && c3 == 0) {
                return (isKeyBReadable) ? 1 : 3;
            } else if (c1 == 0 && c2 == 1 && c3 == 0) {
                if (op == Operations.Read) {
                    return (isKeyBReadable) ? 1 : 3;
                }
                return 0;
            } else if (c1 == 1 && c2 == 0 && c3 == 0) {
                if (op == Operations.Read) {
                    return (isKeyBReadable) ? 1 : 3;
                }
                if (op == Operations.Write) {
                    return 2;
                }
                return 0;
            } else if (c1 == 1 && c2 == 1 && c3 == 0) {
                if (op == Operations.Read || op == Operations.DecTransRest) {
                    return (isKeyBReadable) ? 1 : 3;
                }
                return 2;
            } else if (c1 == 0 && c2 == 0 && c3 == 1) {
                if (op == Operations.Read || op == Operations.DecTransRest) {
                    return (isKeyBReadable) ? 1 : 3;
                }
                return 0;
            } else if (c1 == 0 && c2 == 1 && c3 == 1) {
                if (op == Operations.Read || op == Operations.Write) {
                    return 2;
                }
                return 0;
            } else if (c1 == 1 && c2 == 0 && c3 == 1) {
                if (op == Operations.Read) {
                    return 2;
                }
                return 0;
            } else if (c1 == 1 && c2 == 1 && c3 == 1) {
                return 0;
            } else {
                // Error.
                return -1;
            }
        }
    }

    public static boolean isKeyBReadable(byte c1, byte c2, byte c3) {
        return c1 == 0 && (c2 == 0 && c3 == 0) || (c2 == 1 && c3 == 0)
                || (c2 == 0 && c3 == 1);
    }

    public static byte[][] acBytesToACMatrix(byte acBytes[]) {
        // ACs correct?
        // C1 (Byte 7, 4-7) == ~C1 (Byte 6, 0-3) and
        // C2 (Byte 8, 0-3) == ~C2 (Byte 6, 4-7) and
        // C3 (Byte 8, 4-7) == ~C3 (Byte 7, 0-3)
        byte[][] acMatrix = new byte[3][4];
        if (acBytes.length > 2
                && (byte) ((acBytes[1] >>> 4) & 0x0F) == (byte) ((acBytes[0] ^ 0xFF) & 0x0F)
                && (byte) (acBytes[2] & 0x0F) == (byte) (((acBytes[0] ^ 0xFF) >>> 4) & 0x0F)
                && (byte) ((acBytes[2] >>> 4) & 0x0F) == (byte) ((acBytes[1] ^ 0xFF) & 0x0F)) {
            // C1, Block 0-3
            for (int i = 0; i < 4; i++) {
                acMatrix[0][i] = (byte) ((acBytes[1] >>> 4 + i) & 0x01);
            }
            // C2, Block 0-3
            for (int i = 0; i < 4; i++) {
                acMatrix[1][i] = (byte) ((acBytes[2] >>> i) & 0x01);
            }
            // C3, Block 0-3
            for (int i = 0; i < 4; i++) {
                acMatrix[2][i] = (byte) ((acBytes[2] >>> 4 + i) & 0x01);
            }
            return acMatrix;
        }
        return null;
    }

    public static byte[] acMatrixToACBytes(byte acMatrix[][]) {
        if (acMatrix != null && acMatrix.length == 3) {
            for (int i = 0; i < 3; i++) {
                if (acMatrix[i].length != 4)
                    // Error.
                    return null;
            }
        } else {
            // Error.
            return null;
        }
        byte[] acBytes = new byte[3];
        // Byte 6, Bit 0-3.
        acBytes[0] = (byte) ((acMatrix[0][0] ^ 0xFF) & 0x01);
        acBytes[0] |= (byte) (((acMatrix[0][1] ^ 0xFF) << 1) & 0x02);
        acBytes[0] |= (byte) (((acMatrix[0][2] ^ 0xFF) << 2) & 0x04);
        acBytes[0] |= (byte) (((acMatrix[0][3] ^ 0xFF) << 3) & 0x08);
        // Byte 6, Bit 4-7.
        acBytes[0] |= (byte) (((acMatrix[1][0] ^ 0xFF) << 4) & 0x10);
        acBytes[0] |= (byte) (((acMatrix[1][1] ^ 0xFF) << 5) & 0x20);
        acBytes[0] |= (byte) (((acMatrix[1][2] ^ 0xFF) << 6) & 0x40);
        acBytes[0] |= (byte) (((acMatrix[1][3] ^ 0xFF) << 7) & 0x80);
        // Byte 7, Bit 0-3.
        acBytes[1] = (byte) ((acMatrix[2][0] ^ 0xFF) & 0x01);
        acBytes[1] |= (byte) (((acMatrix[2][1] ^ 0xFF) << 1) & 0x02);
        acBytes[1] |= (byte) (((acMatrix[2][2] ^ 0xFF) << 2) & 0x04);
        acBytes[1] |= (byte) (((acMatrix[2][3] ^ 0xFF) << 3) & 0x08);
        // Byte 7, Bit 4-7.
        acBytes[1] |= (byte) ((acMatrix[0][0] << 4) & 0x10);
        acBytes[1] |= (byte) ((acMatrix[0][1] << 5) & 0x20);
        acBytes[1] |= (byte) ((acMatrix[0][2] << 6) & 0x40);
        acBytes[1] |= (byte) ((acMatrix[0][3] << 7) & 0x80);
        // Byte 8, Bit 0-3.
        acBytes[2] = (byte) (acMatrix[1][0] & 0x01);
        acBytes[2] |= (byte) ((acMatrix[1][1] << 1) & 0x02);
        acBytes[2] |= (byte) ((acMatrix[1][2] << 2) & 0x04);
        acBytes[2] |= (byte) ((acMatrix[1][3] << 3) & 0x08);
        // Byte 8, Bit 4-7.
        acBytes[2] |= (byte) ((acMatrix[2][0] << 4) & 0x10);
        acBytes[2] |= (byte) ((acMatrix[2][1] << 5) & 0x20);
        acBytes[2] |= (byte) ((acMatrix[2][2] << 6) & 0x40);
        acBytes[2] |= (byte) ((acMatrix[2][3] << 7) & 0x80);

        return acBytes;
    }

    public static boolean isHexAnd16Byte(String hexString, Context context) {
        if (hexString.matches("[0-9A-Fa-f]+") == false) {
            // Error, not hex.
            Toast.makeText(context, R.string.info_not_hex_data,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (hexString.length() != 32) {
            // Error, not 16 byte (32 chars).
            Toast.makeText(context, R.string.info_not_16_byte,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean isValueBlock(String hexString) {
        byte[] b = BaseApplication.hexStringToByteArray(hexString);
        if (b.length == 16) {
            // Google some NXP info PDFs about Mifare Classic to see how
            // Value Blocks are formated.
            // For better reading (~ = invert operator):
            // if (b0=b8 and b0=~b4) and (b1=b9 and b9=~b5) ...
            // ... and (b12=b14 and b13=b15 and b12=~b13) then
            return (b[0] == b[8] && (byte) (b[0] ^ 0xFF) == b[4])
                    && (b[1] == b[9] && (byte) (b[1] ^ 0xFF) == b[5])
                    && (b[2] == b[10] && (byte) (b[2] ^ 0xFF) == b[6])
                    && (b[3] == b[11] && (byte) (b[3] ^ 0xFF) == b[7])
                    && (b[12] == b[14] && b[13] == b[15] && (byte) (b[12] ^ 0xFF) == b[13]);
        }
        return false;
    }

    public static int isValidDump(String[] lines, boolean ignoreAsterisk) {
        ArrayList<Integer> knownSectors = new ArrayList<Integer>();
        int blocksSinceLastSectorHeader = 4;
        boolean is16BlockSector = false;
        if (lines == null || lines.length == 0) {
            // There are no lines.
            return 6;
        }
        for (int i = 0; i < lines.length; i++) {
            if ((is16BlockSector == false && blocksSinceLastSectorHeader == 4)
                    || (is16BlockSector && blocksSinceLastSectorHeader == 16)) {
                // A sector header is expected.
                if (lines[i].matches("^\\+Sector: [0-9]{1,2}$") == false) {
                    // Not a valid sector length or not a valid sector header.
                    return 1;
                }
                int sector = -1;
                try {
                    sector = Integer.parseInt(lines[i].split(": ")[1]);
                } catch (Exception ex) {
                    // Not a valid sector header.
                    // Should not occur due to the previous check (regex).
                    return 1;
                }
                if (sector < 0 || sector > 39) {
                    // Sector out of range.
                    return 4;
                }
                if (knownSectors.contains(sector)) {
                    // Two times the same sector number (index).
                    // Maybe this is a file containing multiple dumps
                    // (the dump editor->save->append function was used).
                    return 5;
                }
                knownSectors.add(sector);
                is16BlockSector = sector >= 32;
                blocksSinceLastSectorHeader = 0;
                continue;
            }
            if (lines[i].startsWith("*") && ignoreAsterisk) {
                // Ignore line and move to the next sector.
                // (The line was a "No keys found or dead sector" message.)
                is16BlockSector = false;
                blocksSinceLastSectorHeader = 4;
                continue;
            }
            if (lines[i].matches("[0-9A-Fa-f-]+") == false) {
                // Not pure hex (or NO_DATA).
                return 2;
            }
            if (lines[i].length() != 32) {
                // Not 32 chars per line.
                return 3;
            }
            blocksSinceLastSectorHeader++;
        }
        return 0;
    }

    public static void isValidDumpErrorToast(int errorCode, Context context) {
        switch (errorCode) {
            case 1:
                Toast.makeText(context, R.string.info_valid_dump_not_4_or_16_lines,
                        Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(context, R.string.info_valid_dump_not_hex,
                        Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(context, R.string.info_valid_dump_not_16_bytes,
                        Toast.LENGTH_LONG).show();
                break;
            case 4:
                Toast.makeText(context, R.string.info_valid_dump_sector_range,
                        Toast.LENGTH_LONG).show();
                break;
            case 5:
                Toast.makeText(context, R.string.info_valid_dump_double_sector,
                        Toast.LENGTH_LONG).show();
                break;
            case 6:
                Toast.makeText(context, R.string.info_valid_dump_empty_dump,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    public static void reverseByteArrayInPlace(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    public static String byte2HexString(byte[] bytes) {
        String ret = "";
        if (bytes != null) {
            for (Byte b : bytes) {
                ret += String.format("%02X", b.intValue() & 0xFF);
            }
        }
        return ret;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                        .digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Argument(s) for hexStringToByteArray(String s)"
                    + "was not a hex string");
        }
        return data;
    }

    public static SpannableString colorString(String data, int color) {
        SpannableString ret = new SpannableString(data);
        ret.setSpan(new ForegroundColorSpan(color), 0, data.length(), 0);
        return ret;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void copyToClipboard(String text, Context context) {
        if (text.equals("") == false) {
            if (Build.VERSION.SDK_INT >= 11) {
                // Android API level 11+.
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("mifare classic tool data", text);
                clipboard.setPrimaryClip(clip);
            } else {
                // Android API level 10.
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            }
            Toast.makeText(context, R.string.info_copied_to_clipboard,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static String getFromClipboard(Context context) {
        if (Build.VERSION.SDK_INT >= 11) {
            // Android API level 11+.
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.getPrimaryClip() != null
                    && clipboard.getPrimaryClip().getItemCount() > 0
                    && clipboard
                    .getPrimaryClipDescription()
                    .hasMimeType(
                            android.content.ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                return clipboard.getPrimaryClip().getItemAt(0).getText()
                        .toString();
            }
        } else {
            // Android API level 10.
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasText()) {
                return clipboard.getText().toString();
            }
        }

        // Error.
        return null;
    }

    public static void copyFile(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static Tag getTag() {
        return mTag;
    }

    public static void setTag(Tag tag) {
        mTag = tag;
        mUID = tag.getId();
    }

    public static NfcAdapter getNfcAdapter() {
        return mNfcAdapter;
    }

    public static void setNfcAdapter(NfcAdapter nfcAdapter) {
        mNfcAdapter = nfcAdapter;
    }

    public static SparseArray<byte[][]> getKeyMap() {
        return mKeyMap;
    }

    public static void setKeyMapRange(int from, int to) {
        mKeyMapFrom = from;
        mKeyMapTo = to;
    }

    public static int getKeyMapRangeFrom() {
        return mKeyMapFrom;
    }

    public static int getKeyMapRangeTo() {
        return mKeyMapTo;
    }

    public static void setKeyMap(SparseArray<byte[][]> value) {
        mKeyMap = value;
    }

    public static byte[] getUID() {
        return mUID;
    }

    public static String getVersionCode() {
        return mVersionCode;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    public static int VersionDetails() {
        int Value = 0;
        try {
            PackageInfo pInfo = mAppContext.getPackageManager().getPackageInfo(mAppContext.getPackageName(), 0);
            String version = pInfo.versionName;
            Value = pInfo.versionCode;
            //Value = Integer.parseInt(mAppContext.getPackageManager().getPackageInfo(packageName, 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Value;
    }
}
