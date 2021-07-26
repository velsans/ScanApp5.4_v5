package com.zebra.main.activity.purchase.ui.transfer

data class PurchaseTransferModel(var IMEI: String? = null, var StartDateTime: kotlin.String? = null, var EndDateTime: kotlin.String? = null, var SyncTime: kotlin.String? = null, var PurchaseTransferUniqueID: kotlin.String? = null, var PurchaseNo: kotlin.String? = null,
                                 var ID: Int = 0, var PurchaseId: Int = 0, var UserID: Int = 0, var ToLocationID: Int = 0, var TransferAgencyID: Int = 0, var DriverID: Int = 0, var Count: Int = 0, var SyncStatus: Int = 0, var TransportTypeId: Int = 0,
                                 var FromLocationID: Int = 0, var IsActive: Int = 0, var LoadedTypeID: Int = 0, var TruckId: Int = 0, var Volume: Double = 0.0)