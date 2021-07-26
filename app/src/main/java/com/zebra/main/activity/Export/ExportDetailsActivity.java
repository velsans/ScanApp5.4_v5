package com.zebra.main.activity.Export;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.zebra.R;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.fragments.ContainersFragment;
import com.zebra.main.fragments.LogDetailsFragment;
import com.zebra.main.fragments.LogSummaryFragment;
import com.zebra.main.fragments.VesselFragments;
import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportOrderDetailsModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.GwwException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExportDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ExportDetails";
    AlertDialogManager alert = new AlertDialogManager();
    TextView LocationNameTXT, OrderNumTxT, ContainerCountTxT, ExportNoTxT, StuffingDateTxT, BookingNOTxT, CutOffDateAndTimeTxT, ShippingCompanyTxT;
    FrameLayout AlreadySyncLAY;
    ImageView handleIMG;
    private TabLayout export_tabLayout;
    private ViewPager export_viewPager;
    ImageView CloseExportDetailsActivity;
    //private InternalDataBaseHelperClass mDBInternalHelper;
    int ViewPageID;
    LocalBroadcastManager lbm;
    SlidingDrawer simpleSlidingDrawer;
    SwipeRefreshLayout exportDetails_swipeLayout;
    ApiInterface ExportSync = null;
    ExportOrderDetailsModel exportOrdermodel;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Called with the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exportdetails);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ExportSync = ApiClient.getInstance().getUserService();
        getSupportActionBar().hide();
        Initialization();

        /*Fragment for Export Details*/
        setupViewPager(export_viewPager);
        export_viewPager.setCurrentItem(Common.SelectedTabPosition);
        LocationNameTXT.setText(Common.ToLocationName);
        //getQuotationDetailsApi();
        if (Common.IsExportEditListFlag == false) {
            AlreadySyncLAY.setVisibility(View.GONE);
        } else {
            AlreadySyncLAY.setVisibility(View.VISIBLE);
        }

        OrderNumTxT.setText(Common.ExportCode);
        ContainerCountTxT.setText(Common.Export_ContainerCount);
        ExportNoTxT.setText(String.valueOf(Common.ExportID));
        StuffingDateTxT.setText(Common.StuffingDate);
        BookingNOTxT.setText(Common.BookingNumber);
        CutOffDateAndTimeTxT.setText(Common.CuttOffDateTime);
        ShippingCompanyTxT.setText(Common.ShippingAgentId);

        // mDBInternalHelper = new InternalDataBaseHelperClass(this);
        export_tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Common.SelectedTabPosition = tab.getPosition();
                export_viewPager.setCurrentItem(tab.getPosition());
                ViewPageID = tab.getPosition();
                if (ViewPageID == 0) {
                    Common.BarcodeScannerforLogdetails = false;
                    Common.BarcodeScannerforVessel = true;
                    Common.BarcodeScannerFlagForExport = true;
                    lbm = LocalBroadcastManager.getInstance(ExportDetailsActivity.this);
                    Intent i = new Intent("VESSEL_REFRESH");
                    lbm.sendBroadcast(i);
                } else if (ViewPageID == 1) {
                    lbm = LocalBroadcastManager.getInstance(ExportDetailsActivity.this);
                    Intent i = new Intent("LOGSUMMARY_REFRESH");
                    lbm.sendBroadcast(i);
                } else if (ViewPageID == 2) {
                    lbm = LocalBroadcastManager.getInstance(ExportDetailsActivity.this);
                    Intent i = new Intent("CONTAINERS_REFRESH");
                    lbm.sendBroadcast(i);
                } else if (ViewPageID == 3) {
                    Common.BarcodeScannerforLogdetails = true;
                    Common.BarcodeScannerforVessel = false;
                    Common.BarcodeScannerFlagForExport = true;
                    lbm = LocalBroadcastManager.getInstance(ExportDetailsActivity.this);
                    Intent i = new Intent("LOGDETAILS_REFRESH");
                    lbm.sendBroadcast(i);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        simpleSlidingDrawer.setOnDrawerOpenListener(() -> handleIMG.setImageDrawable(ContextCompat.getDrawable(ExportDetailsActivity.this, R.mipmap.delete_cross)));
        // implement setOnDrawerCloseListener event
        simpleSlidingDrawer.setOnDrawerCloseListener(() -> {
            // change the handle button text
            handleIMG.setImageDrawable(ContextCompat.getDrawable(ExportDetailsActivity.this, R.mipmap.view));
        });
        exportDetails_swipeLayout.setOnRefreshListener(() -> {
            // Your code here
            GetOrderDetailsApi();
            // To keep animation for 4 seconds
            new Handler().postDelayed(() -> {
                // Stop animation (This will be after 3 seconds)
                exportDetails_swipeLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Refreshed !", Toast.LENGTH_LONG).show();
            }, 2000); // Delay in millis
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Signout("Are you sure want to close?");
        }
        return true;
    }

    // Called when the activity is about to start interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void Initialization() {
        // Inflate our UI from its XML layout description.
        LocationNameTXT = findViewById(R.id.locationNameTXT);
        AlreadySyncLAY = findViewById(R.id.alreadySyncedLayout);
        findViewById(R.id.alreadySyncedLayout).setOnClickListener(mAlreadySyncListener);
        export_viewPager = findViewById(R.id.export_viewpager);
        export_tabLayout = findViewById(R.id.export_tabs);
        export_tabLayout.setupWithViewPager(export_viewPager);
        ContainerCountTxT = findViewById(R.id.containerCountTxT);
        OrderNumTxT = findViewById(R.id.orderNumTxT);
        ExportNoTxT = findViewById(R.id.exportNoTxT);
        StuffingDateTxT = findViewById(R.id.stuffingDateTxT);
        // QuotationNOTxT = findViewById(R.id.quotationNOTxT);
        BookingNOTxT = findViewById(R.id.bookingNOTxT);
        CutOffDateAndTimeTxT = findViewById(R.id.cutOffDateAndTimeTxT);
        ShippingCompanyTxT = findViewById(R.id.shippingCompanyTxT);
        CloseExportDetailsActivity = findViewById(R.id.closeActivity);
        findViewById(R.id.closeActivity).setOnClickListener(mCloseActivityListener);
        simpleSlidingDrawer = findViewById(R.id.simpleSlidingDrawer);
        handleIMG = findViewById(R.id.handle);
        exportDetails_swipeLayout = findViewById(R.id.exportdetails_swipe_refresh_layout);
        // Scheme colors for animation
        exportDetails_swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }

    public void clearBackStackInclusive(String tag) {
        getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new VesselFragments(), "VESSEL");
        adapter.addFrag(new LogSummaryFragment(), "LOGSUMMARY");
        adapter.addFrag(new ContainersFragment(), "CONTAINERS");
        adapter.addFrag(new LogDetailsFragment(), "LOGDETAILS");
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        //viewPager.setOffscreenPageLimit(1);// for single page
        viewPager.setAdapter(adapter);
        //viewPager.setOnPageChangeListener(myOnPageChangeListener);
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener =
            new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrollStateChanged(int state) {
                    //Called when the scroll state changes.
                }

                @Override
                public void onPageScrolled(int position,
                                           float positionOffset, int positionOffsetPixels) {
                    Log.e(">>>>>>>>>", "onPageScrolled:" + position + "\n");
                }

                @Override
                public void onPageSelected(int position) {
                    //This method will be invoked when a new page becomes selected.
                    Log.e(">>>>>>>>>", "onPageSelected:" + position + "\n");
                }
            };

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    View.OnClickListener mCloseActivityListener = v -> Signout("Are you sure want to close?");

    View.OnClickListener mAlreadySyncListener = v -> AlertDialogBox(CommonMessage(R.string.TransferHead), "Already synced,You can't edit", false);

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public void Signout(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                (dialog, id) -> dialog.cancel());
        builder1.setNegativeButton("Yes",
                (dialog, id) -> {
                    try {
                        if (Common.BarcodeScannerforVessel == true) {
                            VesselFragments vesselFrag = new VesselFragments();
                            vesselFrag.vesselPauseANDResume();
                        }
                        if (Common.BarcodeScannerforLogdetails == true) {
                            LogDetailsFragment logFrag = new LogDetailsFragment();
                            logFrag.LogDetailsPauseANDResume();
                        }
                        ExportActivty();
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
                    }
                    dialog.cancel();
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    public void ExportActivty() {
        Intent _gwwIntent = new Intent(this, ExportListActivity.class);
        startActivity(_gwwIntent);
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return this.getResources().getString(Common_Msg);
    }

    public double TotalVolume(ArrayList<ExportDetailsModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (ExportDetailsModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getVolume();
        }
        return TotVolume;
    }

    public void GetOrderDetailsApi() {
        try {
            Common.Export.LogSummaryList.clear();
            Common.Export.ContainerList.clear();
            Common.Export.ExportDetailsList.clear();
            Common.Export.TotalCBMDetailsList.clear();
            exportOrdermodel = new ExportOrderDetailsModel();
            exportOrdermodel.setExportID(Common.ExportID);

            ExportSync = ApiClient.getApiInterface();
            ExportSync.GetExportOrderDetails(exportOrdermodel).enqueue(new Callback<ExportOrderDetailsModel>() {
                @Override
                public void onResponse(Call<ExportOrderDetailsModel> call, Response<ExportOrderDetailsModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.isSuccessful()) {
                            Common.Export.LogSummaryList.addAll(response.body().getLogSummaryDetails());
                            Common.Export.ContainerList.addAll(response.body().getContainersDetails());
                            Common.Export.ExportDetailsList.addAll(response.body().getLogDetails());
                            Common.Export.TotalCBMDetailsList.addAll(response.body().getTotalCBMDetails());
                            if (Common.Export.LogSummaryList.size() > 0) {
                                // Refresh activity not blinks
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + CommonMessage(R.string.NoValueFound) + ", Please try some other export number", false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + response.message(), false);
                        }
                    } else {
                        Common.AuthorizationFlag = true;
                        AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<ExportOrderDetailsModel> call, Throwable t) {
                    AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }
}
