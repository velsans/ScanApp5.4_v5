package com.zebra.main.model.vessel

import java.util.*

data class VesselInputList(var IMEI: String? = null,
                           var UserID: Int = 0, var ExportID: Int = 0,
                           var CreateVesselTransportList: ArrayList<VesselListModel?>? = ArrayList())