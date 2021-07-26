package com.zebra.main.model.InvTransfer

data class InventoryTransferModel(var VBB_Number: String? = null, var IMEI: String? = null, var StartDateTime: String? = null,
                                  var EndDateTime: String? = null, var TransferMode: String? = null, var TruckPlateNumber: String? = null,
                                  var SyncTime: String? = null, var TransUniqueID: String? = null, var DriverName: String? = null,
                                  var AgencyName: String? = null,
                                  var TransferID: Int = 0, var UserID: Int = 0, var ToLocationID: Int = 0, var TransferAgencyID: Int = 0,
                                  var DriverID: Int = 0, var Count: Int = 0, var SyncStatus: Int = 0, var TransportTypeId: Int = 0,
                                  var FromLocationID: Int = 0, var ISActive: Int = 0, var OLDTransferID: Int = 0, var LoadedTypeID: Int = 0,
                                  var TruckId: Int = 0,var Volume:Double=0.0,var PurchaseID: String? = null)