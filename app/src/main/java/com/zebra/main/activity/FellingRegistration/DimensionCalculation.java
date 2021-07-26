package com.zebra.main.activity.FellingRegistration;

import android.util.Log;

import com.zebra.utilities.Common;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DimensionCalculation {

    public static double SbblabelDiameter = 0.0, SbblableHeartDiameter = 0.0, SbbLableVolumePercentage = 0.0, SbbLableVolumeHeartPercentage = 0.0;

    public DimensionCalculation() {
    }

    public static double VolumeCalculation(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2, String SbbLabelLenght,
                                           String SbbLabelNoteF, String SbbLabelNoteT, String SbbLabelNoteL) {
        double TotalVolume = 0.0, LogVolume = 0.00, CavityVolume = 0.00;
        if (!SbbLabelDF1.equals("") && !SbbLabelDF2.equals("") && !SbbLabelDT1.equals("") && !SbbLabelDT2.equals("") && !SbbLabelLenght.equals("")) {
            double F1 = Double.parseDouble(SbbLabelDF1);
            double F2 = Double.parseDouble(SbbLabelDF2);
            double T1 = Double.parseDouble(SbbLabelDT1);
            double T2 = Double.parseDouble(SbbLabelDT2);
            double LENGTH = Double.parseDouble(SbbLabelLenght);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            LogVolume = (Diameter * Diameter * Common.DensityOFWood * LENGTH) / 10000;
        }
        /*Diameter Calculation*/
        if (!SbbLabelNoteF.equals("") || !SbbLabelNoteT.equals("") && !SbbLabelNoteL.equals("")) {
            double F = 0.00, T = 0.00, L = 0.00, CavityAvergDiameter = 0.00;
            if (SbbLabelNoteF.length() > 0) {
                F = Double.parseDouble(SbbLabelNoteF);
            }
            if (SbbLabelNoteT.length() > 0) {
                T = Double.parseDouble(SbbLabelNoteT);
            }
            if (SbbLabelNoteL.length() > 0) {
                L = Double.parseDouble(SbbLabelNoteL);
            }
            if (!SbbLabelNoteF.equals("") && !SbbLabelNoteT.equals("")) {
                CavityAvergDiameter = ((F + T) / 2);
            } else if (!SbbLabelNoteT.equals("") || !SbbLabelNoteT.equals("")) {
                CavityAvergDiameter = ((F + T) / 1);
            }
            CavityVolume = (CavityAvergDiameter * CavityAvergDiameter * Common.DensityOFWood * L) / 10000;
        }
        TotalVolume = LogVolume - CavityVolume;
        return TotalVolume;
    }

    public static double UpdateVolumeCalculation(String UpdateSbbLabelDF1, String UpdateSbbLabelDF2, String UpdateSbbLabelDT1, String UpdateSbbLabelDT2, String UpdateSbbLabelLenght,
                                                 String UpdateSbbLabelNoteF, String UpdateSbbLabelNoteT, String UpdateSbbLabelNoteL) {
        double TotalVolume = 0.00, LogVolume = 0.00, CavityVolume = 0.00;
        if (!UpdateSbbLabelDF1.equals("") && !UpdateSbbLabelDF2.equals("") && !UpdateSbbLabelDT1.equals("") && !UpdateSbbLabelDT2.equals("") && !UpdateSbbLabelLenght.equals("")) {
            double F1 = Double.parseDouble(UpdateSbbLabelDF1);
            double F2 = Double.parseDouble(UpdateSbbLabelDF2);
            double T1 = Double.parseDouble(UpdateSbbLabelDT1);
            double T2 = Double.parseDouble(UpdateSbbLabelDT2);
            double LENGTH = Double.parseDouble(UpdateSbbLabelLenght);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            LogVolume = (Diameter * Diameter * Common.DensityOFWood * LENGTH) / 10000;
        }
        /*Diameter Calculation*/
        if (!UpdateSbbLabelNoteF.equals("") || !UpdateSbbLabelNoteT.equals("") && !UpdateSbbLabelNoteL.equals("")) {
            double F = 0.00, T = 0.00, L = 0.00, CavityAvergDiameter = 0.00;
            if (UpdateSbbLabelNoteF.length() > 0) {
                F = Double.parseDouble(UpdateSbbLabelNoteF);
            }
            if (UpdateSbbLabelNoteT.length() > 0) {
                T = Double.parseDouble(UpdateSbbLabelNoteT);
            }
            if (UpdateSbbLabelNoteL.length() > 0) {
                L = Double.parseDouble(UpdateSbbLabelNoteL);
            }
            if (!UpdateSbbLabelNoteF.equals("") && !UpdateSbbLabelNoteT.equals("")) {
                CavityAvergDiameter = ((F + T) / 2);
            } else if (!UpdateSbbLabelNoteF.equals("") || !UpdateSbbLabelNoteT.equals("")) {
                CavityAvergDiameter = ((F + T) / 1);
            }
            CavityVolume = (CavityAvergDiameter * CavityAvergDiameter * Common.DensityOFWood * L) / 10000;
        }
        TotalVolume = LogVolume - CavityVolume;
        return TotalVolume;
    }

    public static int SBBLableDiameter(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2) {
        double F1 = 0, F2 = 0, T1 = 0, T2 = 0;
        if (SbbLabelDF1.length() > 0) {
            if (SbbLabelDF1.equals(".")) {
                F1 = 0;
            } else {
                F1 = Double.parseDouble(SbbLabelDF1);
            }
        }
        if (SbbLabelDF2.length() > 0) {
            if (SbbLabelDF2.equals(".")) {
                F2 = 0;
            } else {
                F2 = Double.parseDouble(SbbLabelDF2);
            }
        }
        if (SbbLabelDT1.length() > 0) {
            if (SbbLabelDT1.equals(".")) {
                T1 = 0;
            } else {
                T1 = Double.parseDouble(SbbLabelDT1);
            }
        }
        if (SbbLabelDT2.length() > 0) {
            if (SbbLabelDT2.equals(".")) {
                T2 = 0;
            } else {
                T2 = Double.parseDouble(SbbLabelDT2);
            }

        }
        SbblabelDiameter = ((F1 + F2 + T1 + T2) / 4);
        int Sbblabelinteger = (int) Math.round(SbblabelDiameter);
        return Sbblabelinteger;
    }

    public static Double SBBLableDiameterPurchase(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2) {
        double F1 = 0, F2 = 0, T1 = 0, T2 = 0;
        if (SbbLabelDF1.length() > 0) {
            if (SbbLabelDF1.equals(".")) {
                F1 = 0;
            } else {
                F1 = Double.parseDouble(SbbLabelDF1);
            }
        }
        if (SbbLabelDF2.length() > 0) {
            if (SbbLabelDF2.equals(".")) {
                F2 = 0;
            } else {
                F2 = Double.parseDouble(SbbLabelDF2);
            }
        }
        if (SbbLabelDT1.length() > 0) {
            if (SbbLabelDT1.equals(".")) {
                T1 = 0;
            } else {
                T1 = Double.parseDouble(SbbLabelDT1);
            }
        }
        if (SbbLabelDT2.length() > 0) {
            if (SbbLabelDT2.equals(".")) {
                T2 = 0;
            } else {
                T2 = Double.parseDouble(SbbLabelDT2);
            }

        }
        SbblabelDiameter = ((F1 + F2 + T1 + T2) / 4);
        //double Sbblabelinteger = (double) Math.round(SbblabelDiameter);
        return SbblabelDiameter;
    }

    public static int SBBLableHeartDiameter(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2) {
        double F1 = 0, F2 = 0, T1 = 0, T2 = 0;
        if (SbbLabelDF1.length() > 0) {
            if (SbbLabelDF1.equals(".")) {
                F1 = 0;
            } else {
                F1 = Double.parseDouble(SbbLabelDF1);
            }
        }
        if (SbbLabelDF2.length() > 0) {
            if (SbbLabelDF2.equals(".")) {
                F2 = 0;
            } else {
                F2 = Double.parseDouble(SbbLabelDF2);
            }
        }
        if (SbbLabelDT1.length() > 0) {
            if (SbbLabelDT1.equals(".")) {
                T1 = 0;
            } else {
                T1 = Double.parseDouble(SbbLabelDT1);
            }
        }
        if (SbbLabelDT2.length() > 0) {
            if (SbbLabelDT2.equals(".")) {
                T2 = 0;
            } else {
                T2 = Double.parseDouble(SbbLabelDT2);
            }

        }
        SbblableHeartDiameter = ((F1 + F2 + T1 + T2) / 4);
        int SbblabelHeartinteger = (int) Math.round(SbblableHeartDiameter);
        return SbblabelHeartinteger;
    }

    public static String Heart_Diameter_Percentage() {
        double HeartDiameterPercentage = 0;
        if (SbblableHeartDiameter > 0) {
            //HeartDiameterPercentage = (SbblabelDiameter / SbblableHeartDiameter) * 100;
            HeartDiameterPercentage = (SbblableHeartDiameter / SbblabelDiameter) * 100;
        }
        int SbblabelHeartinteger = (int) Math.round(HeartDiameterPercentage);
        return String.valueOf(SbblabelHeartinteger);
    }

    public static String Heart_Volume_Percentage() {
        double HeartVolumePercentage = 0;
        if (SbbLableVolumeHeartPercentage > 0) {
            //HeartVolumePercentage = (SbbLableVolumePercentage / SbbLableVolumeHeartPercentage) * 100;
            HeartVolumePercentage = (SbbLableVolumeHeartPercentage / SbbLableVolumePercentage) * 100;
        }
        int SbblabelHeartinteger = (int) Math.round(HeartVolumePercentage);
        return String.valueOf(SbblabelHeartinteger);
    }

    public static double HeartVolumeCalculation(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2, String Volume, String length,
                                                String LenghtCutTop, String LengthCutFoot, String CrackF1, String CrackF2, String CrackT1, String CrackT2) {
        double TotalVolume = 0.0, LengthCutVolume, LengthCutafterVolume, LengthCutHeartVolume = 0.0, Lengthcutafterheartvolume, Crackheartvolume=0.0;
        if (!SbbLabelDF1.equals("") && !SbbLabelDF2.equals("") && !SbbLabelDT1.equals("") && !SbbLabelDT2.equals("")) {
            double F1 = Double.parseDouble(SbbLabelDF1);
            double F2 = Double.parseDouble(SbbLabelDF2);
            double T1 = Double.parseDouble(SbbLabelDT1);
            double T2 = Double.parseDouble(SbbLabelDT2);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            double LCF = Double.parseDouble(LengthCutFoot);
            double LCT = Double.parseDouble(LenghtCutTop);
            LengthCutVolume = LCF + LCT;
            LengthCutafterVolume = Double.parseDouble(length) - (LCF + LCT);
            if(isEditTextEmptyDouble(LenghtCutTop) > 0.0 ||isEditTextEmptyDouble(LengthCutFoot) > 0.0){
                LengthCutHeartVolume = (Diameter * Diameter * Common.DensityOFWood * LengthCutVolume) / 10000;
                TotalVolume=LengthCutHeartVolume;
            }else{
                Lengthcutafterheartvolume= (Diameter * Diameter * Common.DensityOFWood * LengthCutafterVolume) / 10000;
                TotalVolume=Lengthcutafterheartvolume;
            }
            double HF1 = Double.parseDouble(SbbLabelDF1) - Double.parseDouble(CrackF1);
            double HF2 = Double.parseDouble(SbbLabelDF2) - Double.parseDouble(CrackF2);
            double HT1 = Double.parseDouble(SbbLabelDT1) - Double.parseDouble(CrackT1);
            double HT2 = Double.parseDouble(SbbLabelDT2) - Double.parseDouble(CrackT2);
            double HDiameter = ((HF1 + HF2 + HT1 + HT2) / 4);
            //Crackheartvolume = (Double.parseDouble(Volume) - (HDiameter * HDiameter * Common.DensityOFWood * Double.parseDouble(length))) / 10000;
        }
        return TotalVolume;
    }

    public static double LengthCutVolumeCalculation(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2,
                                                    String LenghtCutTop, String LengthCutFoot) {
        double TotalVolume = 0.0, LengthCutVolume;
        if (!SbbLabelDF1.equals("") && !SbbLabelDF2.equals("") && !SbbLabelDT1.equals("") && !SbbLabelDT2.equals("") && !LenghtCutTop.equals("") && !LengthCutFoot.equals("")) {
            LengthCutVolume = Double.parseDouble(LenghtCutTop) + Double.parseDouble(LengthCutFoot);
            double F1 = Double.parseDouble(SbbLabelDF1);
            double F2 = Double.parseDouble(SbbLabelDF2);
            double T1 = Double.parseDouble(SbbLabelDT1);
            double T2 = Double.parseDouble(SbbLabelDT2);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            TotalVolume = (Diameter * Diameter * Common.DensityOFWood * LengthCutVolume) / 10000;
        }
        return TotalVolume;
    }

    public static double HoleVolumeCalculation(String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2,
                                               String LenghtCutTop, String LengthCutFoot, String length) {
        double holeVolume = 0.0, LengthCutVolume;
        if (!SbbLabelDF1.equals("") && !SbbLabelDF2.equals("") && !SbbLabelDT1.equals("") && !SbbLabelDT2.equals("") && !LenghtCutTop.equals("") && !LengthCutFoot.equals("")) {
            double F1 = Double.parseDouble(SbbLabelDF1);
            double F2 = Double.parseDouble(SbbLabelDF2);
            double T1 = Double.parseDouble(SbbLabelDT1);
            double T2 = Double.parseDouble(SbbLabelDT2);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            double LCF = Double.parseDouble(LengthCutFoot);
            double LCT = Double.parseDouble(LenghtCutTop);
            LengthCutVolume= Double.parseDouble(length) - (LCF + LCT);
            holeVolume = (Diameter * Diameter * Common.DensityOFWood * LengthCutVolume) / 10000;
        }
        return holeVolume;
    }

    public static double CrackVolumeCalculation(String CrackF1, String CrackF2, String CrackT1, String CrackT2,
                                                String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2,
                                                String Volume, String length) {
        double TotalVolume = 0.0;
        if (!CrackF1.equals("") && !CrackF2.equals("") && !CrackT1.equals("") && !CrackT2.equals("") &&
                !SbbLabelDF1.equals("") && !SbbLabelDF2.equals("") && !SbbLabelDT1.equals("") && !SbbLabelDT2.equals("") &&
                !Volume.equals("") && !length.equals("")) {
            double F1 = Double.parseDouble(SbbLabelDF1) - Double.parseDouble(CrackF1);
            double F2 = Double.parseDouble(SbbLabelDF2) - Double.parseDouble(CrackF2);
            double T1 = Double.parseDouble(SbbLabelDT1) - Double.parseDouble(CrackT1);
            double T2 = Double.parseDouble(SbbLabelDT2) - Double.parseDouble(CrackT2);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            TotalVolume = (Double.parseDouble(Volume) - (Diameter * Diameter * Common.DensityOFWood * Double.parseDouble(length))) / 10000;
        }
        return TotalVolume;
    }

    public static double SapVolumeCalculation(String SapDeduction, String SbbLabelDF1, String SbbLabelDF2, String SbbLabelDT1, String SbbLabelDT2,
                                              String length, String Volume, String LenghtCutTop, String LengthCutFoot, String lengthCutVolume) {
        double TotalVolume = 0.0, LengthCutDeduction, VolumeDeduction, SapDiameter;

        if (!SapDeduction.equals("") && !LenghtCutTop.equals("") && !LengthCutFoot.equals("") && !lengthCutVolume.equals("") &&
                !SbbLabelDF1.equals("") && !SbbLabelDF2.equals("") && !SbbLabelDT1.equals("") && !SbbLabelDT2.equals("") &&
                !Volume.equals("") && !length.equals("")) {
            double F1 = Double.parseDouble(SbbLabelDF1);
            double F2 = Double.parseDouble(SbbLabelDF2);
            double T1 = Double.parseDouble(SbbLabelDT1);
            double T2 = Double.parseDouble(SbbLabelDT2);
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            double LCF = Double.parseDouble(LengthCutFoot);
            double LCT = Double.parseDouble(LenghtCutTop);
            LengthCutDeduction= Double.parseDouble(length) - (LCF + LCT);
            VolumeDeduction = Double.parseDouble(Volume) - Double.parseDouble(lengthCutVolume);
            SapDiameter = Diameter - Double.parseDouble(SapDeduction);
            TotalVolume = (VolumeDeduction - (SapDiameter * SapDiameter * Common.DensityOFWood * LengthCutDeduction)) / 10000;
        }
        return TotalVolume;
    }

    public static double isEditTextEmptyDouble(String editTxT) {
        double Value = 0.0;
        if (isNullOrEmpty(editTxT)) {
            Value = 0.0;
        } else {
            Value = Double.parseDouble(editTxT.trim());
        }
        return Value;
    }


    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }
}
