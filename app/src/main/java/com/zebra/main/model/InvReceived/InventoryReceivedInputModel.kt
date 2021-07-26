package com.zebra.main.model.InvReceived

data class InventoryReceivedInputModel (
    var BarCode: String? = null,
    var Datetime: String? = null,
    var OrgSBBLabel: String? = null,
    var TreeNumber: String? = null,
    var WoodSpieceCode: String? = null,
    var SBBLabel: String? = null,
    var Quality: String? = null,
    var TransferUniqueID: String? = null,
    var EntryModeID: Int = 0,
    var FellingSectionId: Int = 0,
    var FromLocationID: Int = 0,
    var ID: Int = 0,
    var IsActive: Int = 0,
    var IsSBBLabelCorrected: Int = 0,
    var ReceivedID: Int = 0,
    var ToLocationId: Int = 0,
    var WoodSpieceID: Int = 0,
    var IsReceived: Int = 0,
    var Length: Double = 0.0,
    var Volume: Double = 0.0
)