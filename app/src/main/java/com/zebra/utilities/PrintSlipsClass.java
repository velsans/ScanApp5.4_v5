package com.zebra.utilities;

import android.content.Context;
import android.util.Log;

import com.zebra.R;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;

import java.util.ArrayList;
import java.util.Calendar;

public class PrintSlipsClass {
    private static Context context_ths;
    AlertDialogManager alert = new AlertDialogManager();
    private InternalDataBaseHelperClass mDBInternalHelper;

    public PrintSlipsClass(Context context) {
        context_ths = context;
        mDBInternalHelper = new InternalDataBaseHelperClass(context);
    }

    public String TransferHeader() {
        String GWWHeaderDetails = "";
        try {
            StringBuilder transferDeatils = new StringBuilder();
            transferDeatils.append(Common.TransferUniqueID + "--");
            transferDeatils.append(Common.VBB_Number + "--");
            transferDeatils.append(Common.EndDate + "--");
            transferDeatils.append(Common.FromLocationID + "--");
            transferDeatils.append(Common.ToLocaTransID + "--");
            transferDeatils.append(Common.TransferAgencyID + "--");
            transferDeatils.append(Common.DriverID + "--");
            transferDeatils.append(Common.TransportId + "--");
            transferDeatils.append(Common.TransportTypeId + "--");
            transferDeatils.append(Common.FellingSectionId + "--");
            transferDeatils.append(Common.Count + "--");
            transferDeatils.append(Common.decimalFormat.format(Common.VolumeSum) + "--");
            transferDeatils.append(Common.LoadedTypeID + "--");
            transferDeatils.append(Common.FromLocationname + "--");
            transferDeatils.append(Common.ToLocationName + "--");
            transferDeatils.append(Common.AgencyName + "--");
            transferDeatils.append(Common.DriverName + "--");
            transferDeatils.append(Common.TrucklicensePlateNo + "--");
            transferDeatils.append(Common.TransportMode + "--");
            transferDeatils.append(Common.LoadedName + "--");
            transferDeatils.append(Common.TransferID + "--");
            GWWHeaderDetails = "SIZE 69.9 mm, 60 mm\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "QRCODE 41,15,L,9,A,0,M2,S7," + "\"" + transferDeatils.toString() + "\"" + "\n" +
                    "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return GWWHeaderDetails;
    }

    public String TransferDetails() {
        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        String TransferHead = "INVENTORY TRNASFER";// + CommonMessage(R.string.app_name);
        String GwwHeader = "";
        try {
            GwwHeader = "SIZE 69.9 mm, 75.7 mm\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 137,28,\"0\",0,10,10," + "\"" + TransferHead + "\"" + "\n" +
                    "BAR 137,53, 284, 1\n" +
                    "TEXT 250,316,\"0\",0,9,9," + "\"" + Common.DriverName + "\"" + "\n" +
                    "TEXT 250,400,\"0\",0,9,9," + "\"" + Common.TransportMode + "\"" + "\n" +
                    "TEXT 250,154,\"0\",0,9,9,\"Date:\"\n" +
                    "TEXT 250,195,\"0\",0,9,9," + "\"" + Common.FromLocationname + "\"" + "\n" +
                    "TEXT 250,235,\"0\",0,9,9," + "\"" + Common.ToLocationName + "\"" + "\n" +
                    "TEXT 250,277,\"0\",0,9,9," + "\"" + Common.AgencyName + "\"" + "\n" +
                    "TEXT 250,356,\"0\",0,9,9," + "\"" + Common.TrucklicensePlateNo + "\"" + "\n" +
                    "TEXT 12,195,\"0\",0,10,9,\"From Location:\"\n" +
                    "BAR 12,216, 180, 1\n" +
                    "TEXT 12,235,\"0\",0,9,9,\"To  Location:\"\n" +
                    "BAR 12,256, 134, 1\n" +
                    "TEXT 12,276,\"0\",0,9,9,\"Transport Agency:\"\n" +
                    "BAR 12,297, 192, 1\n" +
                    "TEXT 12,316,\"0\",0,9,9,\"Driver Details:\"\n" +
                    "BAR 12,337, 148, 1\n" +
                    "TEXT 12,356,\"0\",0,9,9,\"TransportPlateNo:\"\n" +
                    "BAR 12,377, 189, 1\n" +
                    "TEXT 12,154,\"0\",0,9,9,\"VBB #:\"\n" +
                    "BAR 12,175, 72, 1\n" +
                    "TEXT 12,397,\"0\",0,9,9,\"Transport Mode:\"\n" +
                    "BAR 12,418, 170, 1\n" +
                    "TEXT 100,154,\"0\",0,9,9," + "\"" + Common.VBB_Number + "\"" + "\n" +
                    "TEXT 319,154,\"0\",0,9,9," + "\"" + Common.EndDate + "\"" + "\n" +
                    "TEXT 250,443,\"0\",0,9,9," + "\"" + Common.Count + "\"" + "\n" +
                    "TEXT 12,440,\"0\",0,9,9,\"Count:\"\n" +
                    "BAR 12,461, 69, 1\n" +
                    "TEXT 250,483,\"0\",0,9,9," + "\"" + Common.decimalFormat.format(Common.VolumeSum) + "\"" + "\n" +
                    "CODEPAGE 1253\n" +
                    "TEXT 12,480,\"0\",0,9,9,\"Volume Ó:\"\n" +
                    "BAR 12,501, 105, 1\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 120,107,\"0\",0,9,9,\"TransUniqueID:\"\n" +
                    "BAR 120,128, 161, 1\n" +
                    "TEXT 282,107,\"0\",0,9,9," + "\"" + Common.TransferUniqueID + "\"" + "\n" +
                    "TEXT 163,68,\"0\",0,9,9,\"Dev Name:\"\n" +
                    "TEXT 282,68,\"0\",0,9,9," + "\"" + Common.DeviceName + "\"" + "\n" +
                    "TEXT 12,522,\"0\",0,9,9,\"LoadedBy:\"\n" +
                    "BAR 12,543, 111, 1\n" +
                    "TEXT 250,522,\"0\",0,9,9," + "\"" + Common.LoadedName + "\"" + "\n" +
                    "TEXT 12,564,\"ROMAN.TTF\",0,1,9,\"User:\"\n" +
                    "BAR 12,585, 116, 1\n" +
                    "TEXT 250,564,\"0\",0,9,9," + "\"" + Common.Username + "\"" + "\n" +
                    "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return GwwHeader;
    }

    public String TransferDetails19_09_2019() {
        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        String FromLocaOrFellingSec = "", FromLocaOrFellingSecValue;
        String GwwHeader = "";
        if (Common.FromLocationID == 2 && Common.ToLocaTransID == 6) {
            FromLocaOrFellingSecValue = "Felling Section:";
            FromLocaOrFellingSec = mDBInternalHelper.GetFellingSecNumbersFromINTransfer(Common.TransferID);
        } else {
            FromLocaOrFellingSec = Common.FromLocationname;
            FromLocaOrFellingSecValue = "From Location:";
        }
        try {
            GwwHeader = "SIZE 69.9 mm, 63.6 mm\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 143,11,\"0\",0,10,10,\"GREEN WOOD WORLD\"\n" +
                    "BAR 143,36, 273, 1\n" +
                    "TEXT 146,54,\"0\",0,10,10,\"TRANSPORT RECEIPT\"\n" +
                    "BAR 146,79, 266, 1\n" +
                    "TEXT 281,324,\"0\",0,9,9," + "\"" + Common.DriverName + "\"" + "\n" +
                    "TEXT 282,399,\"0\",0,9,9," + "\"" + Common.TransportMode + "\"" + "\n" +
                    "TEXT 11,172,\"0\",0,9,9,\"Date:\"\n" +
                    "BAR 11,193, 54, 1\n" +
                    "TEXT 281,211,\"0\",0,9,9," + "\"" + FromLocaOrFellingSec + "\"" + "\n" +
                    "TEXT 281,250,\"0\",0,9,9," + "\"" + Common.ToLocationName + "\"" + "\n" +
                    "TEXT 281,287,\"0\",0,9,9," + "\"" + Common.AgencyName + "\"" + "\n" +
                    "TEXT 282,362,\"0\",0,9,9," + "\"" + Common.TrucklicensePlateNo + "\"" + "\n" +
                    "TEXT 11,250,\"0\",0,9,9,\"To  Location:\"\n" +
                    "BAR 11,271, 134, 1\n" +
                    "TEXT 11,287,\"0\",0,9,9,\"Transport Agency:\"\n" +
                    "BAR 11,308, 192, 1\n" +
                    "TEXT 11,324,\"0\",0,9,9,\"Driver Details:\"\n" +
                    "BAR 11,345, 161, 1\n" +
                    "TEXT 11,362,\"0\",0,9,9,\"TransportPlateNo:\"\n" +
                    "BAR 11,383, 189, 1\n" +
                    "TEXT 11,399,\"0\",0,9,9,\"Transport Mode:\"\n" +
                    "BAR 11,420, 170, 1\n" +
                    "TEXT 281,172,\"0\",0,9,9," + "\"" + Common.EndDate + "\"" + "\n" +
                    "TEXT 132,437,\"0\",0,9,9," + "\"" + Common.Count + "\"" + "\n" +
                    "TEXT 11,437,\"0\",0,9,9,\"Count:\"\n" +
                    "BAR 11,458, 69, 1\n" +
                    "TEXT 403,437,\"0\",0,9,9," + "\"" + Common.decimalFormat.format(Common.VolumeSum) + "\"" + "\n" +
                    "CODEPAGE 1253\n" +
                    "TEXT 282,437,\"0\",0,8,9,\"Volume Ó:\"\n" +
                    "BAR 282,458, 96, 1\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 11,133,\"0\",0,9,9,\"TransportID:\"\n" +
                    "BAR 11,154, 130, 1\n" +
                    "TEXT 281,132,\"0\",0,9,9," + "\"" + Common.TransferID + "\"" + "\n" +
                    "TEXT 11,94,\"0\",0,9,9,\"Device Name:\"\n" +
                    "BAR 11,115, 143, 1\n" +
                    "TEXT 281,94,\"0\",0,9,9," + "\"" + Common.DeviceName + "\"" + "\n" +
                    "TEXT 11,475,\"0\",0,9,9,\"LoadedBy:\"\n" +
                    "BAR 11,496, 111, 1\n" +
                    "TEXT 132,475,\"0\",0,9,9," + "\"" + Common.LoadedName + "\"" + "\n" +
                    "TEXT 282,475,\"0\",0,9,9,\"User:\"\n" +
                    "BAR 282,496, 56, 1\n" +
                    "TEXT 403,475,\"0\",0,9,9," + "\"" + Common.Username + "\"" + "\n" +
                    "TEXT 11,211,\"0\",0,9,9," + "\"" + FromLocaOrFellingSecValue + "\"" + "\n" +
                    "BAR 11,232, 157, 1\n" +
                    "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return GwwHeader;
    }

    public String TransferReceivedBottom19_09_2019(String UniqueID) {
        String appName = CommonMessage(R.string.app_name);
        String GwwBottom = "";
        try {
            GwwBottom = "SIZE 69.9 mm, 5 mm\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 4,7,\"0\",0,9,9," + "\"" + LeftADDSpace(UniqueID, 19) + "\"" + "\n" +
                    "TEXT 431,7,\"0\",0,9,9," + "\"" + RightADDSpace(appName, 19) + "\"" + "\n" +
                    "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("TransferBottom-Receipt", e.toString(), false);
        }
        return GwwBottom;
    }

    public String TransferBarCodeDetails() {
        String BarcodeDetails = "";
        try {
            double ArraySizeAddingValue = 7, Gap = 11.8, HeadValueDiff = 92,
                    MainStatingBox = 44, MainEndingBox = 138,
                    NumberStatingBox = 76, NumberEndingBox = 107,
                    NumberStaringBoxValue = 83, BarcodeStatingValue = 56,
                    sbblabelStaringBoxValue = 64, WSpiceStatingValue = 98;

            StringBuilder BarStgBildr = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.InventorytransferScannedResultList.size(); TransferIndex++) {
                String ScannedResultStr =
                        "BOX 12," + MainStatingBox + ",545," + MainEndingBox + ",3\n" +
                                "BOX 21," + NumberStatingBox + ",51," + NumberEndingBox + ",3\n" +
                                "TEXT 26," + NumberStaringBoxValue + ",\"0\",0,7,8," + "\"" + ADDValue(String.valueOf(TransferIndex + 1)) + "\"" + "\n" +
                                "BARCODE 63," + BarcodeStatingValue + ",\"39\",72,0,0,2,5," + "\"" + Common.InventorytransferScannedResultList.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 420," + sbblabelStaringBoxValue + ",\"0\",0,8,10," + "\"" + ADDSpace(Common.InventorytransferScannedResultList.get(TransferIndex).getSbbLabel(), 13) + "\"" + "\n" +
                                "TEXT 426," + WSpiceStatingValue + ",\"0\",0,8,10," + "\"" + ADDSpace(Common.InventorytransferScannedResultList.get(TransferIndex).getWoodSpieceCode(), 10) + "\"" + "\n";
                BarStgBildr.append(ScannedResultStr);

                MainStatingBox = MainStatingBox + HeadValueDiff;
                MainEndingBox = MainEndingBox + HeadValueDiff;

                NumberStatingBox = NumberStatingBox + HeadValueDiff;
                NumberEndingBox = NumberEndingBox + HeadValueDiff;

                NumberStaringBoxValue = NumberStaringBoxValue + HeadValueDiff;

                BarcodeStatingValue = BarcodeStatingValue + HeadValueDiff;

                sbblabelStaringBoxValue = sbblabelStaringBoxValue + HeadValueDiff;

                WSpiceStatingValue = WSpiceStatingValue + HeadValueDiff;

                ArraySizeAddingValue = ArraySizeAddingValue + Gap;
            }
            BarcodeDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeAddingValue) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 12,3,545,46,3\n" +
                    "BAR 54,4, 3, 41\n" +
                    "TEXT 26,15,\"0\",0,8,10,\"No\"\n" +
                    "TEXT 440,13,\"0\",0,9,10,\"Details\"\n" +
                    "BAR 415,4, 3, 41\n" +
                    "TEXT 186,12,\"0\",0,10,10,\"Barcode\"\n" +
                    BarStgBildr +
                    "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return BarcodeDetails;
    }

    public String TransferScannedItemsDetails() {
        String ScannedItemDetails = "", SerialNumber = "000", SbbLabel = "", WoodSpecieCode = "", FsNumValue = "";
        StringBuilder TransfertgBildr = new StringBuilder();
        int LoopSize = Common.InventorytransferScannedResultList.size() / 3;//printing slip columns
        int Remaining = Common.InventorytransferScannedResultList.size() % 3;//printing slip columns
        try {
            double ArraySizeVale = 5.2, Gap = 10.2,
                    BigBoxStart = 46, BigBoxEnd = 122, BigBoxDiff = 74,
                    NumberBoxStart = 52, NumberBoxEnd = 82, NumBoxDiff = 178,
                    BoxNumber = 58, FsNumber = 92, SbbValue = 54, WooSpiCode = 89,
                    BarEndStart = 48, BarDiff = 74;
            int ColumnIndex = 0;
            if (Remaining == 0) {
            } else if (Remaining == 1 || Remaining == 2) {
                LoopSize = LoopSize + 1;
            }
            for (int TransferIndex = 0; TransferIndex < LoopSize; TransferIndex++) {

                SerialNumber = ADDValue(String.valueOf(ColumnIndex + 1));
                Log.e("ColumnSize1", ">>>>>>" + ColumnIndex);
                SbbLabel = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex).getBarCode(), 13);
                WoodSpecieCode = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex).getWoodSpieceCode(), 10);
                FsNumValue = "FNo: " + String.valueOf(Common.InventorytransferScannedResultList.get(ColumnIndex).getFellingSectionId());
                String Column1Str =
                        "BOX 13," + BigBoxStart + ",545," + BigBoxEnd + ",3\n" +
                                "BOX 21, " + NumberBoxStart + ",59," + NumberBoxEnd + ",3\n" +
                                "TEXT 25, " + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 20, " + FsNumber + ",\"0\",0,8,8," + "\"" + FsNumValue + "\"" + "\n" +
                                 //"TEXT 87," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 69," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 122," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n" +
                                "BAR 191," + BarEndStart + ",3, 75\n";
                TransfertgBildr.append(Column1Str);

                ColumnIndex = ColumnIndex + 1;
                if (ColumnIndex < Common.InventorytransferScannedResultList.size()) {
                    Log.e("ColumnSize2", ">>>>>>" + ColumnIndex);
                    SerialNumber = ADDValue(String.valueOf(ColumnIndex + 1));
                    SbbLabel = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex).getBarCode(), 13);
                    WoodSpecieCode = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex).getWoodSpieceCode(), 10);
                    FsNumValue = "FNo: " + String.valueOf(Common.InventorytransferScannedResultList.get(ColumnIndex).getFellingSectionId());
                } else {
                    SerialNumber = "";
                    SbbLabel = "";
                    WoodSpecieCode = "";
                    FsNumValue = "FNo: ";
                }
                String Column2Str =
                        "BOX 199," + NumberBoxStart + ",237," + NumberBoxEnd + ",3\n" +
                                "TEXT 203," + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 198, " + FsNumber + ",\"0\",0,8,8," + "\"" + FsNumValue + "\"" + "\n" +
                                //"TEXT 265," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 247," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 300," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n" +
                                "BAR 368," + BarEndStart + ",3, 75\n";
                TransfertgBildr.append(Column2Str);

                ColumnIndex = ColumnIndex + 1;
                if (ColumnIndex < Common.InventorytransferScannedResultList.size()) {
                    SerialNumber = ADDValue(String.valueOf(ColumnIndex + 1));
                    SbbLabel = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex).getBarCode(), 13);
                    WoodSpecieCode = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex).getWoodSpieceCode(), 10);
                    FsNumValue = "FNo: " + String.valueOf(Common.InventorytransferScannedResultList.get(ColumnIndex).getFellingSectionId());
                } else {
                    SerialNumber = "";
                    SbbLabel = "";
                    WoodSpecieCode = "";
                    FsNumValue = "FNo: ";
                }
                String Column3Str =
                        "BOX 377," + NumberBoxStart + ",414," + NumberBoxEnd + ",3\n" +
                                "TEXT 381," + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 376, " + FsNumber + ",\"0\",0,8,8," + "\"" + FsNumValue + "\"" + "\n" +
                                //"TEXT 442," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 425," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 474," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n";

                TransfertgBildr.append(Column3Str);

                ColumnIndex = ColumnIndex + 1;

                BigBoxStart = BigBoxStart + BigBoxDiff;
                BigBoxEnd = BigBoxEnd + BigBoxDiff;

                NumberBoxStart = NumberBoxStart + BarDiff;
                NumberBoxEnd = NumberBoxEnd + BarDiff;

                BoxNumber = BoxNumber + BarDiff;
                SbbValue = SbbValue + BarDiff;
                WooSpiCode = WooSpiCode + BarDiff;

                BarEndStart = BarEndStart + BarDiff;
                FsNumber = FsNumber + BarDiff;

                ArraySizeVale = ArraySizeVale + Gap;
            }

            ScannedItemDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeVale) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,5,545,48,3\n" +
                    "TEXT 192,14,\"0\",0,10,10,\"Scanned Items\"\n" +
                    TransfertgBildr +
                    "PRINT 1,1\n";

        } catch (Exception ex) {
            AlertDialogBox("TransferDetails-Receipt", ex.toString(), false);
        }
        return ScannedItemDetails;
    }

    public String TransferScannedItemsDetailsNew() {
        String ScannedItemDetails = "", SerialNumber = "000", SbbLabel = "", WoodSpecieCode = "";
        StringBuilder TransfertgBildr = new StringBuilder();
        int LoopSize = Common.InventorytransferScannedResultList.size() / 3;//printing slip columns
        int Remaining = Common.InventorytransferScannedResultList.size() % 3;//printing slip columns
        try {
            double ArraySizeVale = 5.2, Gap = 10.2,
                    BigBoxStart = 46, BigBoxEnd = 122, BigBoxDiff = 74,
                    NumberBoxStart = 66, NumberBoxEnd = 97, NumBoxDiff = 178,
                    BoxNumber = 73, SbbValue = 54, WooSpiCode = 89,
                    BarEndStart = 48, BarDiff = 74;
            int ColumnIndex = 1;
            if (Remaining == 0) {
            } else if (Remaining == 1 || Remaining == 2) {
                LoopSize = LoopSize + 1;
            }
            for (int TransferIndex = 0; TransferIndex < LoopSize; TransferIndex++) {
                SerialNumber = ADDValue(String.valueOf(ColumnIndex));
                SbbLabel = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex - 1).getSbbLabel(), 13);
                WoodSpecieCode = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex - 1).getWoodSpieceCode(), 10);

                String Column1Str =
                        "BOX 13," + BigBoxStart + ",545," + BigBoxEnd + ",3\n" +
                                "BOX 21, " + NumberBoxStart + ",59," + NumberBoxEnd + ",3\n" +
                                "TEXT 25, " + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 87," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 104," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n" +
                                "BAR 191," + BarEndStart + ",3, 75\n";

                TransfertgBildr.append(Column1Str);

                ColumnIndex = ColumnIndex + 1;
                SerialNumber = ADDValue(String.valueOf(ColumnIndex));
                //if (Common.InventorytransferScannedResultList.size() <= ColumnIndex) {
                SbbLabel = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex - 1).getSbbLabel(), 13);
                WoodSpecieCode = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex - 1).getWoodSpieceCode(), 10);
                // }
                if (Remaining > 0) {
                    if (TransferIndex == (LoopSize - 1)) {
                        Log.e("TransferIndex", ">>>>>>>>" + TransferIndex + ">>>>>" + (LoopSize - 1));
                        if (Remaining == 1) {
                            SerialNumber = "-------";
                            SbbLabel = "--------------";
                            WoodSpecieCode = "------------";
                        }
                    }
                }

                String Column2Str =
                        "BOX 199," + NumberBoxStart + ",237," + NumberBoxEnd + ",3\n" +
                                "TEXT 203," + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 265," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 282," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n" +
                                "BAR 368," + BarEndStart + ",3, 75\n";
                TransfertgBildr.append(Column2Str);

                ColumnIndex = ColumnIndex + 1;
                SerialNumber = ADDValue(String.valueOf(ColumnIndex));
                //if (Common.InventorytransferScannedResultList.size() <= ColumnIndex) {

                SbbLabel = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex - 1).getSbbLabel(), 13);
                WoodSpecieCode = ADDSpace(Common.InventorytransferScannedResultList.get(ColumnIndex - 1).getWoodSpieceCode(), 10);
                //}
                if (Remaining > 0) {
                    if (TransferIndex == (LoopSize - 1)) {
                        Log.e("TransferIndex", ">>>>>>>>" + TransferIndex + ">>>>>" + (LoopSize - 1));
                        if (Remaining == 1 || Remaining == 2) {
                            SerialNumber = "-------";
                            SbbLabel = "--------------";
                            WoodSpecieCode = "------------";
                        }
                    }
                }
                String Column3Str =
                        "BOX 377," + NumberBoxStart + ",414," + NumberBoxEnd + ",3\n" +
                                "TEXT 381," + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 442," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 463," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n";

                TransfertgBildr.append(Column3Str);
                ColumnIndex = ColumnIndex + 1;

                BigBoxStart = BigBoxStart + BigBoxDiff;
                BigBoxEnd = BigBoxEnd + BigBoxDiff;

                NumberBoxStart = NumberBoxStart + BarDiff;
                NumberBoxEnd = NumberBoxEnd + BarDiff;

                BoxNumber = BoxNumber + BarDiff;
                SbbValue = SbbValue + BarDiff;
                WooSpiCode = WooSpiCode + BarDiff;

                BarEndStart = BarEndStart + BarDiff;

                ArraySizeVale = ArraySizeVale + Gap;
            }

            ScannedItemDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeVale) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,5,545,48,3\n" +
                    "TEXT 192,14,\"0\",0,10,10,\"Scanned Items\"\n" +
                    TransfertgBildr +
                    "PRINT 1,1\n";

        } catch (Exception ex) {
            AlertDialogBox("TransferDetails-Receipt", ex.toString(), false);
        }
        return ScannedItemDetails;
    }

    public String TransferFooter() {
        String GwwBarcodeQRitems = "";
        try {
            StringBuilder transferBarcodeDeatils = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.InventorytransferScannedResultList.size(); TransferIndex++) {
                transferBarcodeDeatils.append(Common.InventorytransferScannedResultList.get(TransferIndex).getQualitiy()
                        + "-" + Common.InventorytransferScannedResultList.get(TransferIndex).getBarCode()
                        + "-" + Common.InventorytransferScannedResultList.get(TransferIndex).getFellingSectionId()
                        + "-" + Common.InventorytransferScannedResultList.get(TransferIndex).getTreeNumber() + "--");
            }
            String FinalVAlue = Common.TransferUniqueID + "--" + transferBarcodeDeatils.toString();
            String printSlipValue = GwwException.TransferSlipValues(Common.InventorytransferScannedResultList.size());
            String[] BarcodeSplite = printSlipValue.split("-");
            GwwBarcodeQRitems = "SIZE 69.9 mm," + BarcodeSplite[2] + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "QRCODE " + BarcodeSplite[0] + "," + BarcodeSplite[1] + ",L,9,A,0,M2,S7," + "\"" + FinalVAlue + "\"" + "\n" +
                    "CODEPAGE 1252\n" +
                    "PRINT 1,1";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return GwwBarcodeQRitems;
    }

    public String ReceivedHeader() {
        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        String ReceivedHead = "INVENTORY RECEIVED";// + CommonMessage(R.string.app_name);
        String GwwHeader = "";
        try {
            GwwHeader =
                    "SIZE 69.9 mm, 80.0 mm\n" +
                            "DIRECTION 0,0\n" +
                            "REFERENCE 0,0\n" +
                            "OFFSET 0 mm\n" +
                            "SET PEEL OFF\n" +
                            "SET CUTTER OFF\n" +
                            "SET PARTIAL_CUTTER OFF\n" +
                            "SET TEAR ON\n" +
                            "CLS\n" +
                            "CODEPAGE 1252\n" +
                            "TEXT 137,28,\"ROMAN.TTF\",0,1,10," + "\"" + ReceivedHead + "\"" + "\n" +
                            "BAR 137,53, 284, 1\n" +
                            "TEXT 250,366,\"ROMAN.TTF\",0,1,9," + "\"" + Common.DriverName + "\"" + "\n" +
                            "TEXT 250,450,\"ROMAN.TTF\",0,1,9," + "\"" + Common.TransportMode + "\"" + "\n" +
                            "TEXT 250,204,\"ROMAN.TTF\",0,1,9,\"Date:\"\n" +
                            "TEXT 250,245,\"ROMAN.TTF\",0,1,9," + "\"" + Common.RecFromLocationname + "\"" + "\n" +
                            "TEXT 250,285,\"ROMAN.TTF\",0,1,9," + "\"" + Common.ToLocationName + "\"" + "\n" +
                            "TEXT 250,327,\"ROMAN.TTF\",0,1,9," + "\"" + Common.AgencyName + "\"" + "\n" +
                            "TEXT 250,406,\"ROMAN.TTF\",0,1,9," + "\"" + Common.TrucklicensePlateNo + "\"" + "\n" +
                            "TEXT 12,245,\"0\",0,10,9,\"From Location:\"\n" +
                            "BAR 12,266, 180, 1\n" +
                            "TEXT 12,285,\"ROMAN.TTF\",0,1,9,\"To  Location:\"\n" +
                            "BAR 12,306, 134, 1\n" +
                            "TEXT 12,326,\"ROMAN.TTF\",0,1,9,\"Transport Agency:\"\n" +
                            "BAR 12,347, 192, 1\n" +
                            "TEXT 12,366,\"ROMAN.TTF\",0,1,9,\"Driver Details:\"\n" +
                            "BAR 12,387, 148, 1\n" +
                            "TEXT 12,406,\"ROMAN.TTF\",0,1,9,\"TransportPlateNo:\"\n" +
                            "BAR 12,427, 189, 1\n" +
                            "TEXT 12,204,\"ROMAN.TTF\",0,1,9,\"VBB #:\"\n" +
                            "BAR 12,225, 72, 1\n" +
                            "TEXT 12,447,\"ROMAN.TTF\",0,1,9,\"Transport Mode:\"\n" +
                            "BAR 12,468, 170, 1\n" +
                            "TEXT 100,204,\"ROMAN.TTF\",0,1,9," + "\"" + Common.VBB_Number + "\"" + "\n" +
                            "TEXT 319,204,\"ROMAN.TTF\",0,1,9," + "\"" + Common.EndDate + "\"" + "\n" +
                            "TEXT 250,493,\"ROMAN.TTF\",0,1,9," + "\"" + Common.Count + "\"" + "\n" +
                            "TEXT 12,490,\"ROMAN.TTF\",0,1,9,\"Count:\"\n" +
                            "BAR 12,511, 69, 1\n" +
                            "TEXT 250,533,\"ROMAN.TTF\",0,1,9," + "\"" + Common.decimalFormat.format(Common.VolumeSum) + "\"" + "\n" +
                            "CODEPAGE 1253\n" +
                            "TEXT 12,533,\"ROMAN.TTF\",0,1,9,\"Volume Ó:\"\n" +
                            "BAR 12,554, 105, 1\n" +
                            "CODEPAGE 1252\n" +
                            "TEXT 120,107,\"ROMAN.TTF\",0,1,9,\"ReceivedId:\"\n" +
                            "BAR 120,128, 121, 1\n" +
                            "TEXT 260,107,\"ROMAN.TTF\",0,1,9," + "\"" + Common.ReceivedID + "\"" + "\n" +
                            "TEXT 163,68,\"ROMAN.TTF\",0,1,9,\"Dev Name:\"\n" +
                            "TEXT 282,68,\"ROMAN.TTF\",0,1,9," + "\"" + Common.DeviceName + "\"" + "\n" +
                            "TEXT 12,572,\"ROMAN.TTF\",0,1,9,\"LoadedBy:\"\n" +
                            "BAR 12,593, 111, 1\n" +
                            "TEXT 250,572,\"ROMAN.TTF\",0,1,9," + "\"" + Common.ReceivedLoadedTypeName + "\"" + "\n" +
                            "TEXT 80,149,\"ROMAN.TTF\",0,1,9,\"TransUniqueID:\"\n" +
                            "BAR 80,170, 161, 1\n" +
                            "TEXT 260,149,\"ROMAN.TTF\",0,1,9," + "\"" + Common.TransferReciveUniqueID + "\"" + "\n" +
                            "TEXT 12,610,\"0\",0,9,9,\"CheckedBy:\"\n" +
                            "BAR 12,631, 125, 1\n" +
                            "TEXT 250,610,\"0\",0,9,9," + "\"" + Common.Username + "\"" + "\n" +
                            "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return GwwHeader;
    }

    public String ReceivedHeader19_09_2019() {
        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        String GwwHeader = "";
        String FromLocaOrFellingSec = "", FromLocaOrFellingSecValue;

        if (Common.RecFromLocationID == 2) {
            FromLocaOrFellingSecValue = "Felling Section:";
            FromLocaOrFellingSec = mDBInternalHelper.GetFellingSecNumbersFromINReceived(Common.ReceivedID);
        } else {
            FromLocaOrFellingSec = Common.RecFromLocationname;
            FromLocaOrFellingSecValue = "From Location:";
        }
        try {
            GwwHeader = "SIZE 69.9 mm, 63.6 mm\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 157,10,\"0\",0,9,9,\"GREEN WOOD WORLD\"\n" +
                    "BAR 157,31, 245, 1\n" +
                    "TEXT 100,49,\"0\",0,9,9,\"TRANSPORT RECEIVED RECEIPT\"\n" +
                    "BAR 100,70, 359, 1\n" +
                    "TEXT 292,321,\"0\",0,9,9," + "\"" + Common.DriverName + "\"" + "\n" +
                    "TEXT 292,396,\"0\",0,9,9," + "\"" + Common.TransportMode + "\"" + "\n" +
                    "TEXT 9,168,\"0\",0,9,9,\"Vbb#\"\n" +
                    "BAR 9,189, 59, 1\n" +
                    "TEXT 292,207,\"0\",0,9,9," + "\"" + FromLocaOrFellingSec + "\"" + "\n" +
                    "TEXT 291,246,\"0\",0,9,9," + "\"" + Common.ToLocationName + "\"" + "\n" +
                    "TEXT 291,284,\"0\",0,9,9," + "\"" + Common.AgencyName + "\"" + "\n" +
                    "TEXT 292,358,\"0\",0,9,9," + "\"" + Common.TrucklicensePlateNo + "\"" + "\n" +
                    "TEXT 9,246,\"0\",0,9,9,\"To Location:\"\n" +
                    "BAR 9,267, 128, 1\n" +
                    "TEXT 9,284,\"0\",0,9,9,\"Transport Agency:\"\n" +
                    "BAR 9,305, 192, 1\n" +
                    "TEXT 9,321,\"0\",0,9,9,\"Driver Details:\"\n" +
                    "BAR 9,342, 161, 1\n" +
                    "TEXT 9,358,\"0\",0,9,9,\"TransportPlateNo:\"\n" +
                    "BAR 9,379, 189, 1\n" +
                    "TEXT 9,396,\"0\",0,9,9,\"Transport Mode:\"\n" +
                    "BAR 9,417, 170, 1\n" +
                    "TEXT 78,168,\"0\",0,9,9," + "\"" + Common.VBB_Number + "\"" + "\n" +
                    "TEXT 130,433,\"0\",0,9,9," + "\"" + Common.Count + "\"" + "\n" +
                    "TEXT 9,433,\"0\",0,9,9,\"Count:\"\n" +
                    "BAR 9,454, 69, 1\n" +
                    "TEXT 391,433,\"0\",0,9,9," + "\"" + Common.VolumeSum + "\"" + "\n" +
                    "CODEPAGE 1253\n" +
                    "TEXT 292,433,\"0\",0,8,9,\"Volume Ó:\"\n" +
                    "BAR 292,454, 96, 1\n" +
                    "CODEPAGE 1252\n" +
                    "TEXT 9,129,\"0\",0,9,9,\"TransportID:\"\n" +
                    "BAR 9,150, 130, 1\n" +
                    "TEXT 292,129,\"0\",0,9,9," + "\"" + Common.ReceivedTransferID + "\"" + "\n" +
                    "TEXT 9,90,\"0\",0,9,9,\"Device Name:\"\n" +
                    "BAR 9,111, 143, 1\n" +
                    "TEXT 292,90,\"0\",0,9,9," + "\"" + Common.DeviceName + "\"" + "\n" +
                    "TEXT 9,471,\"0\",0,9,9,\"LoadedBy:\"\n" +
                    "BAR 9,492, 111, 1\n" +
                    "TEXT 130,471,\"0\",0,9,9," + "\"" + Common.ReceivedLoadedTypeName + "\"" + "\n" +
                    "TEXT 292,471,\"0\",0,9,9,\"CheckedBy:\"\n" +
                    "BAR 292,492, 125, 1\n" +
                    "TEXT 421,471,\"0\",0,9,9," + "\"" + Common.Username + "\"" + "\n" +
                    "TEXT 9,207,\"0\",0,9,9," + "\"" + FromLocaOrFellingSecValue + "\"" + "\n" +
                    "BAR 9,228, 157, 1\n" +
                    "TEXT 353,168,\"0\",0,9,9," + "\"" + Common.EndDate + "\"" + "\n" +
                    "TEXT 292,168,\"0\",0,9,9,\"Date:\"\n" +
                    "BAR 292,189, 54, 1\n" +
                    "PRINT 1,1\n";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return GwwHeader;
    }

    public String ReceivedBarCodeSlips() {
        String BarcodeDetails = "";
        try {
            double ArraySizeAddingValue = 7, Gap = 11.6, mainBoxDiff = 92, TextDiff = 89,
                    MainStatingBox = 44, MainEndingBox = 138,
                    NumberStartBox = 76, NumberEndBox = 107,
                    SerialNumberBox = 83, BarcodeStatingValue = 56,
                    CheckBoxStart = 63, CheckBoxEnd = 120,
                    diagonalStart1 = 92, diagonalEnd1 = 107,
                    diagonalStart2 = 107, diagonalEnd2 = 76, textStatring = 83;

            StringBuilder BarStgBildr = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.InventoryReceivedScannedResultList.size(); TransferIndex++) {
                String ScannedResultStr =
                        "BOX 13," + MainStatingBox + ",545," + MainEndingBox + ",3\n" +
                                "BOX 21," + NumberStartBox + ",51," + NumberEndBox + ",3\n" +
                                "TEXT 26," + SerialNumberBox + ",\"0\",0,7,8," + "\"" + ADDValue(String.valueOf(TransferIndex + 1)) + "\"" + "\n" +
                                "BARCODE 63," + BarcodeStatingValue + ",\"39\",72,0,0,2,5," + "\"" + Common.InventoryReceivedScannedResultList.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 349," + textStatring + ",\"0\",0,7,7," + "\"" + Common.InventoryReceivedScannedResultList.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "BOX 446," + CheckBoxStart + ",511," + CheckBoxEnd + ",3\n";

                String CheckboxTickStr = "DIAGONAL  467," + diagonalStart1 + ",476," + diagonalEnd1 + ",3\n" +
                        "DIAGONAL  476," + diagonalStart2 + ",494," + diagonalEnd2 + ",3\n";
                if (Common.InventoryReceivedScannedResultList.get(TransferIndex).getIsReceived().equals("YES")) {
                    ScannedResultStr = ScannedResultStr + CheckboxTickStr;
                }
                BarStgBildr.append(ScannedResultStr);

                MainStatingBox = MainStatingBox + mainBoxDiff;
                MainEndingBox = MainEndingBox + mainBoxDiff;

                NumberStartBox = NumberStartBox + mainBoxDiff;
                NumberEndBox = NumberEndBox + mainBoxDiff;

                SerialNumberBox = SerialNumberBox + mainBoxDiff;
                BarcodeStatingValue = BarcodeStatingValue + mainBoxDiff;

                CheckBoxStart = CheckBoxStart + mainBoxDiff;
                CheckBoxEnd = CheckBoxEnd + mainBoxDiff;

                diagonalStart1 = diagonalStart1 + mainBoxDiff;
                diagonalEnd1 = diagonalEnd1 + mainBoxDiff;

                diagonalStart2 = diagonalStart2 + mainBoxDiff;
                diagonalEnd2 = diagonalEnd2 + mainBoxDiff;

                textStatring = textStatring + mainBoxDiff;

                ArraySizeAddingValue = ArraySizeAddingValue + Gap;
            }
            BarcodeDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeAddingValue) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,3,545,46,3\n" +
                    "BAR 54,4, 3, 41\n" +
                    "TEXT 26,15,\"0\",0,8,8,\"No\"\n" +
                    "TEXT 452,14,\"0\",0,8,8,\"Check\"\n" +
                    "BAR 415,4, 3, 41\n" +
                    "TEXT 186,12,\"0\",0,10,10,\"BarCode\"\n" +
                    BarStgBildr +
                    "PRINT 1,1\n";
            Log.e("", "" + BarcodeDetails);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("Transfer-Receipt", e.toString(), false);
        }
        return BarcodeDetails;
    }

    public String ReceivedScannedItemsDetails() {
        String ScannedItemDetails = "", SerialNumber = "000", SbbLabel = "", WoodSpecieCode = "", ReceivedValues = "";
        StringBuilder TransfertgBildr = new StringBuilder();
        int LoopSize = Common.InventoryReceivedScannedResultList.size() / 3;//printing slip columns
        int Remaining = Common.InventoryReceivedScannedResultList.size() % 3;//printing slip columns
        try {
            double ArraySizeVale = 5.2, Gap = 10.2,
                    BigBoxStart = 46, BigBoxEnd = 122, BigBoxDiff = 74,
                    NumberBoxStart = 52, NumberBoxEnd = 82, NumBoxDiff = 178,
                    CheckBoxStart = 86, CheckBoxEnd = 116,
                    BoxNumber = 58, SbbValue = 54, WooSpiCode = 89,
                    ReceivedStr = 91, Diagnol1Start = 100, Diagnol1End = 108,
                    Diagnol2Start = 113, Diagnol2End = 94,
                    ReceivedStart = 95, ReceivedYESNO = 94,
                    BarEndStart = 48, BarDiff = 74;
            int ColumnIndex = 0;
            if (Remaining == 0) {
            } else if (Remaining == 1 || Remaining == 2) {
                LoopSize = LoopSize + 1;
            }
            for (int TransferIndex = 0; TransferIndex < LoopSize; TransferIndex++) {

                SerialNumber = ADDValue(String.valueOf(ColumnIndex + 1));
                Log.e("ColumnSize1", ">>>>>>" + ColumnIndex);
                SbbLabel = ADDSpace(Common.InventoryReceivedScannedResultList.get(ColumnIndex).getSbbLabel(), 13);
                WoodSpecieCode = Common.InventoryReceivedScannedResultList.get(ColumnIndex).getWoodSpieceCode();

                String Column1Str =
                        "BOX 13," + BigBoxStart + ",545," + BigBoxEnd + ",3\n" +
                                "BOX 21, " + NumberBoxStart + ",59," + NumberBoxEnd + ",3\n" +
                                "TEXT 25, " + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 19," + ReceivedStr + ",\"0\",0,6,8,\"Received:\"" + "\n" +
                                "BOX 100, " + CheckBoxStart + ",137," + CheckBoxEnd + ",3\n" +
                                "TEXT 87," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 141," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n" +
                                "BAR 191," + BarEndStart + ",3, 75\n";
                TransfertgBildr.append(Column1Str);

                ReceivedValues = (Common.InventoryReceivedScannedResultList.get(ColumnIndex).getIsReceived());
                if (ReceivedValues.equals("YES")) {
                    String Diagonal1 =
                            "DIAGONAL 107," + Diagnol1Start + ",113," + Diagnol1End + ",2\n" +
                                    "DIAGONAL 113," + Diagnol2Start + ",133," + Diagnol2End + ",2\n";
                    TransfertgBildr.append(Diagonal1);
                }

                ColumnIndex = ColumnIndex + 1;
                if (ColumnIndex < Common.InventoryReceivedScannedResultList.size()) {
                    Log.e("ColumnSize2", ">>>>>>" + ColumnIndex);
                    SerialNumber = ADDValue(String.valueOf(ColumnIndex + 1));
                    SbbLabel = ADDSpace(Common.InventoryReceivedScannedResultList.get(ColumnIndex).getSbbLabel(), 13);
                    WoodSpecieCode = Common.InventoryReceivedScannedResultList.get(ColumnIndex).getWoodSpieceCode();
                    ReceivedValues = (Common.InventoryReceivedScannedResultList.get(ColumnIndex).getIsReceived());
                    if (ReceivedValues.equals("YES")) {
                        String Diagonal2 =
                                "DIAGONAL 286," + Diagnol1Start + ",291," + Diagnol1End + ",2\n" +
                                        "DIAGONAL 291," + Diagnol2Start + ",312," + Diagnol2End + ",2\n";
                        TransfertgBildr.append(Diagonal2);
                    }
                } else {
                    SerialNumber = "";
                    SbbLabel = "";
                    WoodSpecieCode = "";
                }
                String Column2Str =
                        "BOX 199," + NumberBoxStart + ",237," + NumberBoxEnd + ",3\n" +
                                "TEXT 203," + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 265," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 199," + ReceivedStr + ",\"0\",0,6,8,\"Received:\"" + "\n" +
                                "BOX 279, " + CheckBoxStart + ",317," + CheckBoxEnd + ",3\n" +
                                "TEXT 322," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n" +
                                "BAR 368," + BarEndStart + ",3, 75\n";
                TransfertgBildr.append(Column2Str);

                ColumnIndex = ColumnIndex + 1;
                if (ColumnIndex < Common.InventoryReceivedScannedResultList.size()) {
                    SerialNumber = ADDValue(String.valueOf(ColumnIndex + 1));
                    SbbLabel = ADDSpace(Common.InventoryReceivedScannedResultList.get(ColumnIndex).getSbbLabel(), 13);
                    WoodSpecieCode = Common.InventoryReceivedScannedResultList.get(ColumnIndex).getWoodSpieceCode();

                    ReceivedValues = (Common.InventoryReceivedScannedResultList.get(ColumnIndex).getIsReceived());
                    if (ReceivedValues.equals("YES")) {
                        String Diagonal2 =
                                "DIAGONAL 463," + Diagnol1Start + ",469," + Diagnol1End + ",2\n" +
                                        "DIAGONAL 469," + Diagnol2Start + ",489," + Diagnol2End + ",2\n";
                        TransfertgBildr.append(Diagonal2);

                    }
                } else {
                    SerialNumber = "";
                    SbbLabel = "";
                    WoodSpecieCode = "";
                }
                String Column3Str =
                        "BOX 377," + NumberBoxStart + ",414," + NumberBoxEnd + ",3\n" +
                                "TEXT 381," + BoxNumber + ",\"0\",0,7,7," + "\"" + SerialNumber + "\"" + "\n" +
                                "TEXT 377," + ReceivedStr + ",\"0\",0,6,8,\"Received:\"" + "\n" +
                                "BOX 457," + CheckBoxStart + ",495," + CheckBoxEnd + ",3\n" +
                                "TEXT 442," + SbbValue + ",\"0\",0,8,10," + "\"" + SbbLabel + "\"" + "\n" +
                                "TEXT 502," + WooSpiCode + ",\"0\",0,8,10," + "\"" + WoodSpecieCode + "\"" + "\n";

                TransfertgBildr.append(Column3Str);

                ColumnIndex = ColumnIndex + 1;

                BigBoxStart = BigBoxStart + BigBoxDiff;
                BigBoxEnd = BigBoxEnd + BigBoxDiff;

                NumberBoxStart = NumberBoxStart + BarDiff;
                NumberBoxEnd = NumberBoxEnd + BarDiff;

                CheckBoxStart = CheckBoxStart + BarDiff;
                CheckBoxEnd = CheckBoxEnd + BarDiff;

                ReceivedStart = ReceivedStart + BarDiff;
                ReceivedStr = ReceivedStr + BarDiff;

                Diagnol1Start = Diagnol1Start + BarDiff;
                Diagnol1End = Diagnol1End + BarDiff;
                Diagnol2Start = Diagnol2Start + BarDiff;
                Diagnol2End = Diagnol2End + BarDiff;

                BoxNumber = BoxNumber + BarDiff;
                SbbValue = SbbValue + BarDiff;
                WooSpiCode = WooSpiCode + BarDiff;

                BarEndStart = BarEndStart + BarDiff;

                ArraySizeVale = ArraySizeVale + Gap;
            }

            ScannedItemDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeVale) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,5,545,48,3\n" +
                    "TEXT 192,14,\"0\",0,10,10,\"Scanned Items\"\n" +
                    TransfertgBildr +
                    "PRINT 1,1\n";

        } catch (Exception ex) {
            AlertDialogBox("TransferDetails-Receipt", ex.toString(), false);
        }
        return ScannedItemDetails;
    }

    public String FellingRegisterHeader() {
        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        Common.VolumeSum = Double.valueOf(Common.decimalFormat.format(Common.VolumeSum));
        String FRHead = "FELLING REGISTRATION";// + CommonMessage(R.string.app_name);
        String FellingRegHeader = "";
        try {
            FellingRegHeader =
                    "SIZE 69.9 mm, 43.3 mm\n" +
                            "DIRECTION 0,0\n" +
                            "REFERENCE 0,0\n" +
                            "OFFSET 0 mm\n" +
                            "SET PEEL OFF\n" +
                            "SET CUTTER OFF\n" +
                            "SET PARTIAL_CUTTER OFF\n" +
                            "SET TEAR ON\n" +
                            "CLS\n" +
                            "CODEPAGE 1252\n" +
                            "TEXT 137,12,\"0\",0,10,10," + "\"" + FRHead + "\"" + "\n" +
                            "BAR 137,37, 284, 1\n" +
                            "TEXT 250,103,\"0\",0,9,9,\"Date:\"\n" +
                            "BAR 250,124, 54, 1\n" +
                            "TEXT 250,144,\"0\",0,9,9," + "\"" + Common.FromLocationname + "\"" + "\n" +
                            "TEXT 250,184,\"0\",0,9,9," + "\"" + Common.FellingRegDate + "\"" + "\n" +
                            "TEXT 12,144,\"0\",0,10,9,\"Location:\"\n" +
                            "BAR 12,165, 111, 1\n" +
                            "TEXT 12,184,\"0\",0,9,9,\"RegistrationDate:\"\n" +
                            "BAR 12,205, 180, 1\n" +
                            "TEXT 12,103,\"0\",0,9,9,\"RegNo:\"\n" +
                            "BAR 12,124, 77, 1\n" +
                            "TEXT 100,103,\"0\",0,9,9," + "\"" + Common.FellingRegNo + "\"" + "\n" +
                            "TEXT 319,103,\"0\",0,9,9," + "\"" + Common.EndDate + "\"" + "\n" +
                            "TEXT 250,268,\"0\",0,9,9," + "\"" + Common.Count + "\"" + "\n" +
                            "TEXT 12,265,\"0\",0,9,9,\"Count:\"\n" +
                            "BAR 12,286, 69, 1\n" +
                            "TEXT 102,57,\"0\",0,9,9,\"RegisterUniqueID:\"\n" +
                            "BAR 102,78, 189, 1\n" +
                            "TEXT 300,57,\"0\",0,9,9," + "\"" + Common.FellingRegUniqueID + "\"" + "\n" +
                            "CODEPAGE 1253\n" +
                            "TEXT 12,307,\"0\",0,9,9,\"Volume ∑:\"\n" +
                            "BAR 12,328, 105, 1\n" +
                            "CODEPAGE 1252\n" +
                            "TEXT 250,310,\"0\",0,9,9," + "\"" + Common.VolumeSum + "\"" + "\n" +
                            "TEXT 12,225,\"0\",0,9,9,\"FSecID:\"\n" +
                            "BAR 12,246, 81, 1\n" +
                            "TEXT 100,225,\"0\",0,9,9," + "\"" + Common.FellingSectionNumber + "\"" + "\n" +
                            "TEXT 250,225,\"0\",0,9,9,\"Plot Nos:\"\n" +
                            "BAR 250,246, 83, 1\n" +
                            "TEXT 346,225,\"0\",0,9,9," + "\"" + Common.PlotNo + "\"" + "\n" +
                            "PRINT 1,1";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("FellingRegister-Receipt", e.toString(), false);
        }
        return FellingRegHeader;
    }

    public String FellingRegisterBarCodeDetails() {
        String BarcodeDetails = "";
        try {
            double ArraySizeAddingValue = 7, Gap = 11.8, HeadValueDiff = 92,
                    MainStatingBox = 44, MainEndingBox = 138,
                    NumberStatingBox = 76, NumberEndingBox = 107,
                    NumberStaringBoxValue = 83, BarcodeStatingValue = 54,
                    sbblabelStaringBoxValue = 79, WSpiceStatingValue = 54,
                    qualityStaringBoxValue = 82, treenoStaringBoxValue = 110;

            StringBuilder BarStgBildr = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.FellingRegisterLogsDetails.size(); TransferIndex++) {
                String ScannedResultStr =
                        "BOX 13," + MainStatingBox + ",545," + MainEndingBox + ",3\n" +
                                "BOX 21," + NumberStatingBox + ",51," + NumberEndingBox + ",3\n" +
                                "TEXT 26," + NumberStaringBoxValue + ",\"0\",0,7,8," + "\"" + ADDValue(String.valueOf(TransferIndex + 1)) + "\"" + "\n" +
                                "BARCODE 63," + BarcodeStatingValue + ",\"39\",66,0,0,2,5," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 334," + sbblabelStaringBoxValue + ",\"0\",0,9,8," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 456," + WSpiceStatingValue + ",\"0\",0,9,8," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getWoodSpieceCode() + "\"" + "\n" +
                                "TEXT 468," + qualityStaringBoxValue + ",\"0\",0,8,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getQuality() + "\"" + "\n" +
                                "TEXT 458," + treenoStaringBoxValue + ",\"0\",0,9,8," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTreeNumber() + "\"" + "\n";

                BarStgBildr.append(ScannedResultStr);

                MainStatingBox = MainStatingBox + HeadValueDiff;
                MainEndingBox = MainEndingBox + HeadValueDiff;

                NumberStatingBox = NumberStatingBox + HeadValueDiff;
                NumberEndingBox = NumberEndingBox + HeadValueDiff;

                NumberStaringBoxValue = NumberStaringBoxValue + HeadValueDiff;

                BarcodeStatingValue = BarcodeStatingValue + HeadValueDiff;

                sbblabelStaringBoxValue = sbblabelStaringBoxValue + HeadValueDiff;

                treenoStaringBoxValue = treenoStaringBoxValue + HeadValueDiff;

                qualityStaringBoxValue = qualityStaringBoxValue + HeadValueDiff;

                WSpiceStatingValue = WSpiceStatingValue + HeadValueDiff;

                ArraySizeAddingValue = ArraySizeAddingValue + Gap;
            }
            BarcodeDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeAddingValue) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,3,545,46,3\n" +
                    "TEXT 27,14,\"ROMAN.TTF\",0,1,8,\"No\"\n" +
                    "TEXT 437,14,\"0\",0,9,8,\"Details\"\n" +
                    "BAR 415,4, 3, 41\n" +
                    "TEXT 196,14,\"ROMAN.TTF\",0,1,8,\"Barcode\"\n" +
                    BarStgBildr +
                    "PRINT 1,1";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("FellingRegisterBarcode-Receipt", e.toString(), false);
        }
        return BarcodeDetails;
    }

    public String FellingRegisterBarCodeDetailsNew() {
        String BarcodeDetails = "";
        try {
            double ArraySizeAddingValue = 7, Gap = 11.8, HeadValueDiff = 92,
                    MainStatingBox = 44, MainEndingBox = 138,
                    NumberStatingBox = 76, NumberEndingBox = 107,
                    NumberStaringBoxValue = 83, BarcodeStatingValue = 54,
                    sbblabelStaringBoxValue = 57, WSpiceStatingValue = 105,
                    qualityStaringBoxValue = 81, treenoStaringBoxValue = 81, CheckBoxStartValue = 62, CheckBoxEndValue = 119;

            StringBuilder BarStgBildr = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.FellingRegisterLogsDetails.size(); TransferIndex++) {
                String ScannedResultStr =
                        "BOX 13," + MainStatingBox + ",545," + MainEndingBox + ",3\n" +
                                "BOX 21," + NumberStatingBox + ",51," + NumberEndingBox + ",3\n" +
                                "TEXT 26," + NumberStaringBoxValue + ",\"0\",0,7,8," + "\"" + FellingADDValue(String.valueOf(TransferIndex + 1)) + "\"" + "\n" +
                                "BARCODE 69," + BarcodeStatingValue + ",\"39\",66,0,0,2,5," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 342," + sbblabelStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 357," + treenoStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTreeNumber() + "\"" + "\n" +
                                "TEXT 357," + WSpiceStatingValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getWoodSpieceCode() + "\"" + "\n" +
                                "TEXT 435," + qualityStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getQuality() + "\"" + "\n" +
                                "BOX 471," + CheckBoxStartValue + ",536," + CheckBoxEndValue + ",3\n";

                BarStgBildr.append(ScannedResultStr);

                MainStatingBox = MainStatingBox + HeadValueDiff;
                MainEndingBox = MainEndingBox + HeadValueDiff;

                NumberStatingBox = NumberStatingBox + HeadValueDiff;
                NumberEndingBox = NumberEndingBox + HeadValueDiff;

                NumberStaringBoxValue = NumberStaringBoxValue + HeadValueDiff;

                BarcodeStatingValue = BarcodeStatingValue + HeadValueDiff;

                sbblabelStaringBoxValue = sbblabelStaringBoxValue + HeadValueDiff;

                treenoStaringBoxValue = treenoStaringBoxValue + HeadValueDiff;

                qualityStaringBoxValue = qualityStaringBoxValue + HeadValueDiff;

                WSpiceStatingValue = WSpiceStatingValue + HeadValueDiff;

                CheckBoxStartValue = CheckBoxStartValue + HeadValueDiff;

                CheckBoxEndValue = CheckBoxEndValue + HeadValueDiff;

                ArraySizeAddingValue = ArraySizeAddingValue + Gap;
            }
            BarcodeDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeAddingValue) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,3,545,46,3\n" +
                    "TEXT 27,14,\"ROMAN.TTF\",0,1,8,\"No\"\n" +
                    "TEXT 476,14,\"ROMAN.TTF\",0,1,8,\"Check\"\n" +
                    "TEXT 196,14,\"ROMAN.TTF\",0,1,8,\"Barcode\"\n" +
                    BarStgBildr +
                    "PRINT 1,1";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("FellingRegisterBarcode-Receipt", e.toString(), false);
        }
        return BarcodeDetails;
    }

    public String FellingRegisterDimensions() {
        String BarcodeDetails = "";
        ArrayList<FellingTreeDetailsModel> treeNumberDetails = new ArrayList<>();
        try {
            double ArraySizeAddingValue = 7, Gap = 23.5, HeadValueDiff = 186,
                    MainBoxStatingBox = 46, MainBoxEndingBox = 234,
                    CheckBoxStatingBox = 159, CheckBoxEndingBox = 189,
                    SbbLabelStart = 51, TreeNumberStart = 87,
                    WSpiceStatingValue = 124, FirstBar = 83,
                    SecBar = 121, Thirdbar = 159, ForthBar = 195, SecHeading = 129,
                    SecNoteValue = 164;

            StringBuilder BarStgBildr = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.FellingRegisterLogsDetails.size(); TransferIndex++) {
                treeNumberDetails = mDBInternalHelper.getFellingRegWithTreeDetails(Common.FellingRegisterLogsDetails.get(TransferIndex).getTreeNumber());
                String ScannedResultStr =
                        "BOX 13," + MainBoxStatingBox + ",545," + MainBoxEndingBox + ",3\n" +

                                "TEXT 18," + SbbLabelStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 23," + TreeNumberStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTreeNumber() + "\"" + "\n" +
                                "TEXT 90," + TreeNumberStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTreePartType() + "\"" + "\n" +
                                "TEXT 36," + WSpiceStatingValue + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getWoodSpieceCode() + "\"" + "\n" +

                                "BOX 44," + CheckBoxStatingBox + ",76," + CheckBoxEndingBox + ",3\n" +

                                "BOX 108," + MainBoxStatingBox + ",218," + MainBoxEndingBox + ",3\n" +
                                "BOX 216," + MainBoxStatingBox + ",331," + MainBoxEndingBox + ",3\n" +
                                "BOX 329," + MainBoxStatingBox + ",440," + MainBoxEndingBox + ",3\n" +
                                "BOX 438," + MainBoxStatingBox + ",545," + MainBoxEndingBox + ",3\n" +

                                "BAR 109," + FirstBar + ",436,3\n" +
                                "BAR 109," + SecBar + ",436,3\n" +
                                "BAR 109," + Thirdbar + ",436,3\n" +
                                "BAR 109," + ForthBar + ",436,3\n" +

                                "TEXT 113," + SbbLabelStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getFooter_1() + "\"" + "\n" +
                                "TEXT 225," + SbbLabelStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getFooter_2() + "\"" + "\n" +
                                "TEXT 336," + SbbLabelStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTop_1() + "\"" + "\n" +
                                "TEXT 444," + SbbLabelStart + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTop_2() + "\"" + "\n" +

                                "TEXT 157," + SecHeading + ",\"0\",0,9,9,\"L\"\n" +
                                "TEXT 260," + SecHeading + ",\"0\",0,9,9,\"NF\"\n" +
                                "TEXT 370," + SecHeading + ",\"0\",0,9,9,\"NT\"\n" +
                                "TEXT 479," + SecHeading + ",\"0\",0,9,9,\"NL\"\n" +

                                "TEXT 113," + SecNoteValue + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getLength() + "\"" + "\n" +
                                "TEXT 225," + SecNoteValue + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getNoteF() + "\"" + "\n" +
                                "TEXT 336," + SecNoteValue + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getNoteT() + "\"" + "\n" +
                                "TEXT 444," + SecNoteValue + ",\"0\",0,9,9," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getNoteL() + "\"" + "\n";
                BarStgBildr.append(ScannedResultStr);


                MainBoxStatingBox = MainBoxStatingBox + HeadValueDiff;
                MainBoxEndingBox = MainBoxEndingBox + HeadValueDiff;

                CheckBoxStatingBox = CheckBoxStatingBox + HeadValueDiff;
                CheckBoxEndingBox = CheckBoxEndingBox + HeadValueDiff;

                SbbLabelStart = SbbLabelStart + HeadValueDiff;
                TreeNumberStart = TreeNumberStart + HeadValueDiff;
                WSpiceStatingValue = WSpiceStatingValue + HeadValueDiff;

                FirstBar = FirstBar + HeadValueDiff;
                SecBar = SecBar + HeadValueDiff;
                Thirdbar = Thirdbar + HeadValueDiff;
                ForthBar = ForthBar + HeadValueDiff;

                SecHeading = SecHeading + HeadValueDiff;
                SecNoteValue = SecNoteValue + HeadValueDiff;

                ArraySizeAddingValue = ArraySizeAddingValue + Gap;
            }
            BarcodeDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeAddingValue) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,5,545,48,3\n" +
                    "TEXT 36,14,\"0\",0,9,9,\"Label\"\n" +
                    "TEXT 151,14,\"0\",0,9,9,\"F1\"\n" +
                    "TEXT 262,14,\"0\",0,9,9,\"F2\"\n" +
                    "TEXT 373,14,\"0\",0,9,9,\"T1\"\n" +
                    "TEXT 481,14,\"0\",0,9,9,\"T2\"\n" +
                    BarStgBildr +
                    "PRINT 1,1";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("FellingRegisterBarcode-Receipt", e.toString(), false);
        }
        return BarcodeDetails;
    }

    public String FellingRegisterDetails() {
        String BarcodeDetails = "";
        ArrayList<FellingTreeDetailsModel> treeNumberDetails = new ArrayList<>();
        try {
            double ArraySizeAddingValue = 7, Gap = 18.4, HeadValueDiff = 150,
                    MainStatingBox = 44, MainEndingBox = 138,
                    NumberStatingBox = 76, NumberEndingBox = 107,
                    NumberStaringBoxValue = 83, BarcodeStatingValue = 54,
                    sbblabelStaringBoxValue = 69, WSpiceStatingValue = 105,
                    qualityStaringBoxValue = 81, treenoStaringBoxValue = 97,
                    CheckBoxStartValue = 62, CheckBoxEndValue = 119,
                    TreeNumberStart = 69, SbbLabelStart = 97;

            StringBuilder BarStgBildr = new StringBuilder();
            for (int TransferIndex = 0; TransferIndex < Common.FellingRegisterLogsDetails.size(); TransferIndex++) {
                treeNumberDetails = mDBInternalHelper.getFellingRegWithTreeDetails(Common.FellingRegisterLogsDetails.get(TransferIndex).getTreeNumber());
                String ScannedResultStr =
                        "BOX 13," + MainStatingBox + ",545," + MainEndingBox + ",3\n" +
                                "BOX 21," + NumberStatingBox + ",51," + NumberEndingBox + ",3\n" +
                                "TEXT 26," + NumberStaringBoxValue + ",\"0\",0,7,8," + "\"" + FellingADDValue(String.valueOf(TransferIndex + 1)) + "\"" + "\n" +

                                "TEXT 367," + sbblabelStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTreeNumber() + "\"" + "\n" +
                                "TEXT 433," + sbblabelStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getWoodSpieceCode() + "\"" + "\n" +
                                "TEXT 367," + treenoStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getSbbLabel() + "\"" + "\n" +
                                "TEXT 453," + treenoStaringBoxValue + ",\"0\",0,7,7," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getQuality() + "\"" + "\n";
                BarStgBildr.append(ScannedResultStr);
                if (treeNumberDetails.size() > 0) {
                    String treeNumberDetailsStr =
                            "TEXT 65," + TreeNumberStart + ",\"0\",0,6,6," + "\"" + treeNumberDetails.get(0).getFooter_1() + "\"" + "\n" +
                                    "TEXT 124," + TreeNumberStart + ",\"0\",0,6,6," + "\"" + treeNumberDetails.get(0).getFooter_2() + "\"" + "\n" +
                                    "TEXT 183," + TreeNumberStart + ",\"0\",0,6,6," + "\"" + treeNumberDetails.get(0).getTop_1() + "\"" + "\n" +
                                    "TEXT 243," + TreeNumberStart + ",\"0\",0,6,6," + "\"" + treeNumberDetails.get(0).getTop_2() + "\"" + "\n" +
                                    "TEXT 304," + TreeNumberStart + ",\"0\",0,6,6," + "\"" + treeNumberDetails.get(0).getLength() + "\"" + "\n";
                    BarStgBildr.append(treeNumberDetailsStr);
                }
                String SbbLabeDaimensionsStr =
                        "TEXT 65," + SbbLabelStart + ",\"0\",0,6,6," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getFooter_1() + "\"" + "\n" +
                                "TEXT 124," + SbbLabelStart + ",\"0\",0,6,6," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getFooter_2() + "\"" + "\n" +
                                "TEXT 183," + SbbLabelStart + ",\"0\",0,6,6," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTop_1() + "\"" + "\n" +
                                "TEXT 243," + SbbLabelStart + ",\"0\",0,6,6," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getTop_2() + "\"" + "\n" +
                                "TEXT 304," + SbbLabelStart + ",\"0\",0,6,6," + "\"" + Common.FellingRegisterLogsDetails.get(TransferIndex).getLength() + "\"" + "\n" +
                                "BOX 471," + CheckBoxStartValue + ",536," + CheckBoxEndValue + ",3\n";

                BarStgBildr.append(SbbLabeDaimensionsStr);

                MainStatingBox = MainStatingBox + HeadValueDiff;
                MainEndingBox = MainEndingBox + HeadValueDiff;

                NumberStatingBox = NumberStatingBox + HeadValueDiff;
                NumberEndingBox = NumberEndingBox + HeadValueDiff;

                NumberStaringBoxValue = NumberStaringBoxValue + HeadValueDiff;

                BarcodeStatingValue = BarcodeStatingValue + HeadValueDiff;

                sbblabelStaringBoxValue = sbblabelStaringBoxValue + HeadValueDiff;

                treenoStaringBoxValue = treenoStaringBoxValue + HeadValueDiff;

                qualityStaringBoxValue = qualityStaringBoxValue + HeadValueDiff;

                WSpiceStatingValue = WSpiceStatingValue + HeadValueDiff;

                CheckBoxStartValue = CheckBoxStartValue + HeadValueDiff;

                CheckBoxEndValue = CheckBoxEndValue + HeadValueDiff;

                TreeNumberStart = TreeNumberStart + HeadValueDiff;

                SbbLabelStart = SbbLabelStart + HeadValueDiff;

                ArraySizeAddingValue = ArraySizeAddingValue + Gap;
            }
            BarcodeDetails = "SIZE 69.9 mm," + String.valueOf(ArraySizeAddingValue) + " mm" + "\n" +
                    "DIRECTION 0,0\n" +
                    "REFERENCE 0,0\n" +
                    "OFFSET 0 mm\n" +
                    "SET PEEL OFF\n" +
                    "SET CUTTER OFF\n" +
                    "SET PARTIAL_CUTTER OFF\n" +
                    "SET TEAR ON\n" +
                    "CLS\n" +
                    "CODEPAGE 1252\n" +
                    "BOX 13,6,545,49,3\n" +
                    "TEXT 531,38,\"0\",180,9,8,\"No\"\n" +
                    "TEXT 82,38,\"0\",180,7,8,\"Check\"\n" +
                    "TEXT 481,38,\"0\",180,9,8,\"SbbLabel\"\n" +
                    "TEXT 369,38,\"0\",180,9,8,\"F1\"\n" +
                    "TEXT 315,38,\"0\",180,9,8,\"F2\"\n" +
                    "TEXT 261,38,\"0\",180,9,8,\"T1\"\n" +
                    "TEXT 208,38,\"0\",180,9,8,\"T2\"\n" +
                    "TEXT 149,38,\"0\",180,9,8,\"L\"\n" +
                    "BAR 383,6, 3, 41\n" +
                    "BAR 330,6, 3, 41\n" +
                    "BAR 276,6, 3, 41\n" +
                    "BAR 223,6, 3, 41\n" +
                    "BAR 169,6, 3, 41\n" +
                    "BAR 115,6, 3, 41\n" +
                    "BAR 489,6, 3, 41\n" +
                    BarStgBildr +
                    "PRINT 1,1";
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("FellingRegisterBarcode-Receipt", e.toString(), false);
        }
        return BarcodeDetails;
    }

    public String ADDValue(String OldValue) {
        String Value = null;
        if (OldValue.length() == 1) {
            Value = "00" + OldValue;
        } else if (OldValue.length() == 2) {
            Value = "0" + OldValue;
        } else {
            Value = OldValue;
        }
        return Value;
    }

    public String FellingADDValue(String OldValue) {
        String Value = null;
        if (OldValue.length() == 1) {
            Value = "0" + OldValue;
        } else {
            Value = OldValue;
        }
        return Value;
    }

    public String ADDSpace(String OldValue, int Lenght) {
        int HeadBackFrtLen = 0;
        StringBuilder result = new StringBuilder();
        if (Lenght == 13) {
            HeadBackFrtLen = (Lenght - OldValue.length()) / 2;
        } else if (Lenght == Common.ScannedValueLenght) {
            HeadBackFrtLen = (Lenght - OldValue.length()) / 2;
        }
        for (int i = 0; i < HeadBackFrtLen; i++) {
            result.append(" ");
        }
        return result.toString() + OldValue + result.toString();
    }

    public String LeftADDSpace(String OldValue, int Lenght) {
        int HeadBackFrtLen = 0;
        StringBuilder result = new StringBuilder();
        if (Lenght == 13) {
            HeadBackFrtLen = (Lenght - OldValue.length()) / 2;
        } else if (Lenght == Common.ScannedValueLenght) {
            HeadBackFrtLen = (Lenght - OldValue.length()) / 2;
        }
        for (int i = 0; i < HeadBackFrtLen; i++) {
            result.append(" ");
        }
        return OldValue + result.toString() + result.toString();
    }

    public String RightADDSpace(String OldValue, int Lenght) {
        int HeadBackFrtLen = 0;
        StringBuilder result = new StringBuilder();
        if (Lenght == 13) {
            HeadBackFrtLen = (Lenght - OldValue.length()) / 2;
        } else if (Lenght == Common.ScannedValueLenght) {
            HeadBackFrtLen = (Lenght - OldValue.length()) / 2;
        }
        for (int i = 0; i < HeadBackFrtLen; i++) {
            result.append(" ");
        }
        return result.toString() + result.toString() + OldValue;
    }

    public String HeadCenterAlignment(String Value, int Totlenght) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Totlenght; i++) {
            result.append(" ");
        }
        return (result.toString() + Value + result.toString());
    }

    public String CommaSeprater(double Values) {
        String conValue = String.format("%,.2f", Values);
        return conValue;
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(context_ths, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return context_ths.getResources().getString(Common_Msg);
    }

}
