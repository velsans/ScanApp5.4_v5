package com.zebra.main.interfac;

public interface ScanListener {
//	public void result(byte[] data);
void result(String content);

//	public void minDeResult(String codeType, String content);
//
//	public void NewLandResult_3095(byte codeType, String content);
//	
//	public void NewLandResult_3070(String codeType, String content);
//	
//	public void HonyWellResult_4313(String codeType, String content);
//
//	public void xunbaoResult(byte codeType, String context);

//	public void henResult(byte[] data);
//	public void henResult(byte codeType, String context);
void dspStat(String content);
	void dspData(String content);
	void dspStat(int content);
	void henResult(String codeType, String context);
}
