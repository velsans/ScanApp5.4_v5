package com.zebra.main.sdl;

import android.content.Context;
import android.content.SharedPreferences;
import android.jb.barcode.ResourceUtil;

public class SymbologiesPrefer {

	private static SharedPreferences getSP(Context context) {
		return context.getSharedPreferences(
		// context.getString(R.string.app_name), Context.MODE_PRIVATE);
				context.getString(ResourceUtil.getStringResIDByName(context,
						"app_name")), Context.MODE_PRIVATE);
	}

	public static  int getCodeAztec(Context context, int defaultValues){
		return getSP(context).getInt("Aztec",defaultValues);	
	}

	public static  void setCodeAztec(Context context, int defaultValues){
		 getSP(context).edit().putInt("Aztec", defaultValues).commit();
	}

	public static  int getCodabar(Context context, int defaultValues){
		return getSP(context).getInt("Codabar",defaultValues);	
	}

	public static  void setCodabar(Context context, int defaultValues){
		 getSP(context).edit().putInt("Codabar", defaultValues).commit();
	}
	
	public static  int getCode32(Context context, int defaultValues){
		return getSP(context).getInt("Code32",defaultValues);	
	}

	public static  void setCode32(Context context, int defaultValues){
		 getSP(context).edit().putInt("Code32", defaultValues).commit();
	}
	public static  int getCode39(Context context, int defaultValues){
		return getSP(context).getInt("Code39",defaultValues);	
	}

	public static  void setCode39(Context context, int defaultValues){
		 getSP(context).edit().putInt("Code39", defaultValues).commit();
	}

	public static  int getCode93(Context context, int defaultValues){
		return getSP(context).getInt("Code93",defaultValues);	
	}

	public static  void setCode93(Context context, int defaultValues){
		 getSP(context).edit().putInt("Code93", defaultValues).commit();
	}
	
	public static  int getCodeEan13(Context context, int defaultValues){
		return getSP(context).getInt("Ean13",defaultValues);	
	}

	public static  void setCodeEan13(Context context, int defaultValues){
		 getSP(context).edit().putInt("Ean13", defaultValues).commit();
	}
	
	public static  int getCodeGs1_databar(Context context, int defaultValues){
		return getSP(context).getInt("Gs1_databar",defaultValues);	
	}

	public static  void setCodeGs1_databar(Context context, int defaultValues){
		 getSP(context).edit().putInt("Gs1_databar", defaultValues).commit();
	}
	
	public static  int getCodeInterleaved_2_of_5(Context context, int defaultValues){
		return getSP(context).getInt("Interleaved_2_of_5",defaultValues);	
	}

	public static  void setCodeInterleaved_2_of_5(Context context, int defaultValues){
		 getSP(context).edit().putInt("Interleaved_2_of_5", defaultValues).commit();
	}
	
	public static  int getCodeMatrix_2_of_5(Context context, int defaultValues){
		return getSP(context).getInt("Matrix_2_of_5",defaultValues);	
	}

	public static  void setCodeMatrix_2_of_5(Context context, int defaultValues){
		 getSP(context).edit().putInt("Matrix_2_of_5", defaultValues).commit();
	}
	
	
}
