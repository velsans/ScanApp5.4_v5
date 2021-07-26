package com.zebra.main.api;

import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsSyncInputModel;
import com.zebra.main.model.Export.ContainerDetailsMainModel;
import com.zebra.main.model.Export.DeleteExportLogModel;
import com.zebra.main.model.Export.ExportDetailstTotalSyncModel;
import com.zebra.main.model.Export.ExportListModel;
import com.zebra.main.model.Export.ExportListTotalInputModel;
import com.zebra.main.model.Export.ExportOrderDetailsModel;
import com.zebra.main.model.Export.ExportSbbLabelInputModel;
import com.zebra.main.model.Export.InsertExportLogsModel;
import com.zebra.main.model.Export.TransportDetailsModel;
import com.zebra.main.model.Export.UpdateContainerModel;
import com.zebra.main.model.externaldb.ExternalDataBaseTableSizesModel;
import com.zebra.main.model.FellingRegistration.FellingRegistrationSyncModel;
import com.zebra.main.model.InputAdvanceSearchOutputModel;
import com.zebra.main.model.InvCount.InventoryCountSyncModel;
import com.zebra.main.model.InvReceived.InventoryReceivedSyncModel;
import com.zebra.main.model.InvTransfer.InventoryTransferSyncModel;
import com.zebra.main.model.externaldb.PurchaseAgreementInputModel;
import com.zebra.main.model.vessel.VesselCancelTransferModel;
import com.zebra.main.model.vessel.VesselCreateModel;
import com.zebra.main.model.vessel.VesselInputAdvanceSearchModel;
import com.zebra.main.model.vessel.VesselInputList;
import com.zebra.main.model.vessel.VesselPrintOutputModel;
import com.zebra.main.model.vessel.VesselScannedDetailsModel;
import com.zebra.main.model.vessel.VesselScannedLogModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST(ServiceURL.ControllorName + "/GetMasterDatavalue/")
    Call<ExternalDataBaseTableSizesModel> getExternalDB(@Body ExternalDataBaseTableSizesModel extDBValues);

    @POST(ServiceURL.ControllorName + "/InsertHHInventoryCount_V2")
    Call<InventoryCountSyncModel> getInventoryCountSync(@Body InventoryCountSyncModel countSyncModel);

    //@POST(ServiceURL.ControllorName + "/InsertHHInventoryTransfer_v2")
    @POST(ServiceURL.ControllorName + "/InsertHHInventoryTransfer_v4")// 10-july-2021
    //@POST(ServiceURL.ControllorName + "/InsertHHInventoryTransfer_V2")
    Call<InventoryTransferSyncModel> getInventoryTransferSync(@Body InventoryTransferSyncModel inventoryTransferSyncModel);

    @POST(ServiceURL.ControllorName + "/FellingRegistration_v1")
    Call<FellingRegistrationSyncModel> getFellingRegSync(@Body FellingRegistrationSyncModel fellingSyncModel);

    @POST(ServiceURL.ExportControllerName + "/InsertExportDetails")
    Call<ExportDetailstTotalSyncModel> getExportDetailsSync(@Body ExportDetailstTotalSyncModel exportTotalSyncModel);

    //@POST(ServiceURL.ControllorName + "/InsertHHInventoryReceived")
    //@POST(ServiceURL.ControllorName + "/InsertHHInventoryReceived_V1")
    @POST(ServiceURL.ControllorName + "/InsertHHInventoryReceived_v4")// 10-july-2021
    Call<InventoryReceivedSyncModel> getInventoryReceivedSync(@Body InventoryReceivedSyncModel inventoryReceivedSyncModel);

    @POST(ServiceURL.ExportControllerName + "/GetQuotationDetails")
    Call<ExportOrderDetailsModel> getQuotationDetailsSync(@Body ExportOrderDetailsModel exportOrdermodel);

   /* @POST("/GWWHHDeviceAPI/" + ServiceURL.ControllorName + "/InsertHHInventoryCount_V2")
    Call<List<SyncStatusModel>> getInventoryCountSyncAll(@Body InventoryCountSyncModel inventoryCountModels);*/

    @POST(ServiceURL.ExportControllerName + "/ExportMasterDetails")
    Call<ExportSbbLabelInputModel> getBarCodeCheck(@Body ExportSbbLabelInputModel exportInputmodel);

    @POST(ServiceURL.ExportControllerName + "/InsertLoadPlan")
    Call<ExportListTotalInputModel> getInsertLoadPlan(@Body ExportListTotalInputModel exportListInputModel);

    /*New export methods*/

    @GET(ServiceURL.ExportControllerName + "/GetExportList")
    Call<ExportListModel> getExportList();

    @POST(ServiceURL.ExportControllerName + "/GetExportOrderDetails")
    Call<ExportOrderDetailsModel> GetExportOrderDetails(@Body ExportOrderDetailsModel exportListModel);

    //@POST(ServiceURL.ExportControllerName + "/GetInsertExportLogs")
    @POST(ServiceURL.ExportControllerName + "/InsertExportLogs_V1")
    Call<InsertExportLogsModel> InsertExportLogs(@Body InsertExportLogsModel exportListModel);

    @POST(ServiceURL.ExportControllerName + "/GetDeleteExportLogs")
    Call<DeleteExportLogModel> DeleteExportLogs(@Body DeleteExportLogModel exportListModel);

    @POST(ServiceURL.ExportControllerName + "/GetExportAdvanceSearch")
    Call<InputAdvanceSearchOutputModel> getExportAdvanceSearch(@Body InputAdvanceSearchOutputModel advanceSearchOutputModel);

    @POST(ServiceURL.ExportControllerName + "/GetContainerDetails")
    Call<ContainerDetailsMainModel> GetContainerDetails(@Body ContainerDetailsMainModel exportListModel);

    @GET(ServiceURL.ExportControllerName + "/GetTransportDetails")
    Call<TransportDetailsModel> GetTransportDetails();

    @POST(ServiceURL.ExportControllerName + "/UpdateContainer")
    Call<UpdateContainerModel> getUpdateContainer(@Body UpdateContainerModel updateContainerModel);

    @POST(ServiceURL.ControllorName + "/GetVesselAdvanceSearch")
    Call<VesselInputAdvanceSearchModel> GetVesselAdvanceSearch(@Body VesselInputAdvanceSearchModel updateContainerModel);

    // Vessel Transportation
    @POST(ServiceURL.ControllorName + "/GetVesseltranspotationDetails/")
    Call<VesselInputList> getVesselList(@Body VesselInputList vesselModel);

    @POST(ServiceURL.ControllorName + "/CreateVesselTransportList/")
    Call<VesselCreateModel> getVesselCreate(@Body VesselCreateModel vessel_createModel);

    @POST(ServiceURL.ControllorName + "/GetVesselScannedLogs/")
    Call<VesselScannedDetailsModel> getVesselTransportLogsDetails(@Body VesselScannedDetailsModel vessel_scannedModel);

    @POST(ServiceURL.ControllorName + "/GetVesselTransportLog/")
    Call<VesselScannedLogModel> getVesselTransportLogs(@Body VesselScannedLogModel vessel_scannedModel);

    @POST(ServiceURL.ControllorName + "/GetVesselTransportPrintValue/")
    Call<VesselPrintOutputModel> GetVesselTransportPrintValue(@Body VesselPrintOutputModel vessel_outputModel);


    @POST(ServiceURL.ControllorName + "/CancelledTransferList/")
    Call<VesselCancelTransferModel> GetVesselCancelledTransferList(@Body VesselCancelTransferModel vessel_cancelModel);

    @POST(ServiceURL.ExternalPurchase + "/GetExternalPurchaseSync/")
    Call<PurchaseAgreementInputModel> GetAgreementPurchaseLogs(@Body PurchaseAgreementInputModel purchase_AgreementModel);

    //@POST(ServiceURL.ExternalPurchase + "/InsertPurchaseAgrementLogs/")
    @POST(ServiceURL.ExternalPurchase + "/InsertPurchaseAgrementLogs_v1/")
    Call<PurchaseLogsSyncInputModel> InsertPurchaseLogs(@Body PurchaseLogsSyncInputModel purcahseSyncModel);

    @POST(ServiceURL.ExternalPurchase + "/InsertExternalPurchaseTransfer")
    Call<InventoryTransferSyncModel> getPurchaseTransferSync(@Body InventoryTransferSyncModel inventoryTransferSyncModel);

    @POST(ServiceURL.ExternalPurchase + "/InsertExternalPurchaseReceived")
    Call<InventoryReceivedSyncModel> getInsertExternalPurchaseReceived(@Body InventoryReceivedSyncModel inventoryReceivedSyncModel);

}
