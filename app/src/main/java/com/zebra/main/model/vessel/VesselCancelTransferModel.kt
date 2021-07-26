package com.zebra.main.model.vessel

import com.zebra.main.model.SyncStatusModel
import java.util.*

data class VesselCancelTransferModel(var UserId: Int = 0,
                                     var Barcode: String? = null, var VesselTrUniqueID: String? = null,
                                     var Status: ArrayList<SyncStatusModel?>? = ArrayList())