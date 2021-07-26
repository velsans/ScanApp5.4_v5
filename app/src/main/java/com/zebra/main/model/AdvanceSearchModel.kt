package com.zebra.main.model

data class AdvanceSearchModel(
    var BarCode: String? = null,
    var SBBLabel: String? = null,
    var Length_dm: String? = null,
    var Volume: String? = null,
    var WoodSpeciesCode: String? = null,
    var FellingSectionId: String? = null,
    var TreeNumber: String? = null,
    var Classification: String? = null,
    var F1: String? = null,
    var F2: String? = null,
    var T1: String? = null,
    var T2: String? = null,
    var HoleVolume: String? = null,
    var GrossVolume: String? = null,
    var WoodSpeciesId: Int = 0,
    var Exisiting: Boolean = false,
    var PurchaseID: String? = null
)