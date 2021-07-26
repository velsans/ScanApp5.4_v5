package com.zebra.main.model.vessel

import com.zebra.main.model.SyncStatusModel
import java.util.*

data class VesselCreateModel(var IMEINumber: String? = null, var ExportCode: String? = null, var UserID: Int = 0, var DeviceID: Int = 0, var ExportID: Int = 0, var Status: ArrayList<SyncStatusModel?>? = ArrayList(), var CreateVesselTransport: ArrayList<CreateVesselTransportListModel?>? = ArrayList())