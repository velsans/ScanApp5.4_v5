package com.zebra.main.model.vessel

import java.util.*

data class VesselScannedDetailsModel(var IMEI: String? = null, var VesselTrUniqueID: String? = null,
                                     var GetVesselScannedLogs: ArrayList<VesselLogsDetailsModel?>? = ArrayList())
