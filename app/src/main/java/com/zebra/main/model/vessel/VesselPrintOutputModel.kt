package com.zebra.main.model.vessel

import java.util.*

data class VesselPrintOutputModel(var VSTransferUniqueID: String? = null,
                                  var UserID: Int = 0,
                                  var GetVesselTransportPrintValue: ArrayList<VesselLogDetailsPrintModel?>? = ArrayList())