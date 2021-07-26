package com.zebra.main.model

data class SyncStatusModel(var status: Int = 0, var syncStatus: Int = 0, var DriverId: Int = 0, var TransferAgencyId: Int = 0, var TruckId: Int = 0,
                      var Message: String? = null, var SyncTime: String? = null)