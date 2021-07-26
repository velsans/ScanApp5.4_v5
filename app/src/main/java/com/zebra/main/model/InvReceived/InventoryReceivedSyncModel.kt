package com.zebra.main.model.InvReceived

import com.zebra.main.model.SyncStatusModel
import java.util.*

data class InventoryReceivedSyncModel (
    var VBBNumber: String? = null,
    var IMEINumber: String? = null,
    var StartTime: String? = null,
    var EndTime: String? = null,
    var TruckPlateNumber: String? = null,
    var DriverName: String? = null,
    var AgencyName: String? = null,
    var ReceivedID: Int = 0,
    var TransferID: Int = 0,
    var LocationID: Int = 0,
    var TransferModeID: Int = 0,
    var TransferAgencyId: Int = 0,
    var DriverId: Int = 0,
    var TranferredCount: Int = 0,
    var UserID: Int = 0,
    var ToLocationID: Int = 0,
    var TruckId: Int = 0,
    var PurchaseStatus:Boolean = false,
    var HHReceived: ArrayList<InventoryReceivedInputModel> = ArrayList<InventoryReceivedInputModel>(),
    //2-12-2019  ArrayList<SyncStatusModel> ----> Added setter getter methods
    var Status: ArrayList<SyncStatusModel> = ArrayList<SyncStatusModel>()
)