package com.zebra.main.model.vessel

data class VesselListModel(var IMEI: String? = null, var StartDateTime: String? = null, var EndDateTime: String? = null, var SyncTime: String? = null, var VesselUniqueID: String? = null, var TransferDetailsId: String? = null, var TotalVolume: String? = null, var ExportCode: String? = null,
                           var VesselListID: Int = 0, var UserID: Int = 0, var Count: Int = 0, var FromLocationID: Int = 0, var DriverID: Int = 0, var LoadedTypeID: Int = 0, var ToLocationID: Int = 0, var TransferAgencyID: Int = 0, var TransportTypeId: Int = 0, var TransportId: Int = 0,
                           var IsDeliveryConfirmed: Boolean = false)