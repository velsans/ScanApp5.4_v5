package com.zebra.main.model.vessel

import com.zebra.main.model.SyncStatusModel
import java.util.*
import kotlin.collections.ArrayList

data class VesselScannedLogModel(var SBBlBarcode: String? = null, var VBBNumber: String? = null, var IMEINumber: String? = null, var EndTime: String? = null, var TruckPlateNumber: String? = null, var VSTransferUniqueID: String? = null, var Quality: String? = null, var LocationID: Int? = 0, var ToLocationID: Int? = 0, var VesselTrsnID: Int? = 0, var TransferModeID: Int? = 0, var TransferAgencyId: Int? = 0, var DriverId: Int? = 0, var TranferredCount: Int? = 0, var UserID: Int? = 0, var LoadedTypeID: Int? = 0, var PortID: Int? = 0, var EntryMode: Int? = 0, var ExportID: Int? = 0, var TransportId: Int? = 0, var Status: ArrayList<SyncStatusModel> = ArrayList(), var ExportLogDetails: ArrayList<VesselLogsDetailsModel> = ArrayList())