package com.zebra.main.model.InvTransfer

data class InventoryTransferInputListModel(var ID: Int = 0, var WoodSpieceID: Int = 0,
                                           var ToLocationId: Int = 0,
                                           var EntryModeID: Int = 0,
                                           var IsSBBLabelCorrected: Int = 0,
                                           var IsActive: Int = 0,
                                           var FellingSectionId: Int = 0,
                                           var TreeNumber: Int = 0,
                                           var SBBLabel: String? = null, var IMEI: String? = null,
                                           var BarCode: String? = null, var Datetime: String? = null,
                                           var WoodSpieceCode: String? = null, var OrgSBBLabel: String? = null,
                                           var Quality: String? = null, var Length: Double = 0.0,
                                           var Volume: Double = 0.0)