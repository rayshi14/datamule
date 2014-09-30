package info.fshi.datamule;

import info.fshi.datamule.data.DbBTDevice;
import info.fshi.datamule.data.DbBTScanLog;
import info.fshi.datamule.data.DbHelper;
import info.fshi.datamule.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle.GridStyle;
import com.jjoe64.graphview.LineGraphView;

public class BTDeviceScanHistory extends Activity {
	private DbHelper dbHelper;
	
	private final static String TAG = "BTDeviceScanHisotoryActivity";
	// init example series data
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bt_device_scan_history);
		dbHelper = new DbHelper(this);
		Intent intent = getIntent();
		String Mac = intent.getStringExtra(Constants.INTENT_EXTRA_DEVICE_MAC);
		DbBTDevice device = dbHelper.getBTDevice(Mac);
		List<DbBTScanLog> scanLogsList = dbHelper.getAllBTScanLogsByDeviceId(device.getId());

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(Locale.UK.getCountry()));
		cal.setTime(new Date(scanLogsList.get(0).getTimestamp()));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long startTime = cal.getTimeInMillis();
		Log.d(TAG, String.valueOf(startTime));
		long endTime = scanLogsList.get(scanLogsList.size()-1).getTimestamp();
		Log.d(TAG, String.valueOf(endTime - startTime));
		int daySpan = (int) ((endTime - startTime) / 24 / 1000 / 3600);
		int numEvent = 24*60;
		ArrayList<GraphViewData[]> scanLogsListDataSeriesArray = new ArrayList<GraphViewData[]>();
		ArrayList<Date> seriesDate = new ArrayList<Date>();
		Log.d(TAG, "Day span " + String.valueOf(daySpan));
		for(int i=0; i<=daySpan; i++){
			GraphViewData[] scanData = new GraphViewData[numEvent];
			Log.d(TAG, String.valueOf(i));
			long startOfDay = startTime + (long) (i)*24*3600*1000;
			Log.d(TAG, "start of day " + String.valueOf(startOfDay));
			seriesDate.add(new Date(startOfDay));
			for(int j=0; j<numEvent; j++){
				scanData[j] = new GraphViewData(j, 0);
			}
			scanLogsListDataSeriesArray.add(scanData);
		}

		ArrayList<Integer> dayIndexArray = new ArrayList<Integer>();

		for(DbBTScanLog scanLog : scanLogsList){
			Log.d(TAG, "scan time " + String.valueOf(scanLog.getTimestamp()));
			int dayIndex = (int) ((scanLog.getTimestamp() - startTime)/24/3600/1000);
			long dayTimestamp = scanLog.getTimestamp() - startTime - dayIndex * 24 * 3600 * 1000;
			int dayTimeIndex = (int) dayTimestamp/1000/60;
			if(! dayIndexArray.contains(dayIndex)){
				dayIndexArray.add(dayIndex);
			}
			scanLogsListDataSeriesArray.get(dayIndex)[dayTimeIndex] = new GraphViewData(dayTimeIndex, 1);
		}

		GraphView graphView = new LineGraphView(
				this // context
				, "Scan History" // heading
				);

		DateFormat formatter = new SimpleDateFormat("MMM dd", Locale.UK);
		int[] color = {Color.RED, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA};

		int colorIndex = 0;
		Log.d(TAG, "Day " + String.valueOf(dayIndexArray.size()));
		for(int index : dayIndexArray){
			GraphViewData[] dataSeries = scanLogsListDataSeriesArray.get(index);

			Log.d(TAG, String.valueOf(seriesDate.get(index).getTime()));
			GraphViewSeries scanLogsSeries = new GraphViewSeries(formatter.format(seriesDate.get(index)), new GraphViewSeriesStyle(color[colorIndex % 6], 2), dataSeries);
			graphView.addSeries(scanLogsSeries); // data
			colorIndex++;
		}
		String[] xLabels = {"0:00", "6:00", "12:00", "18:00", "24:00"};
//		for(int i=0; i<12; i++){
//			xLabels[i] = String.valueOf(2*i) + ":00";
//		}

		graphView.setBackgroundColor(Color.BLACK);
		graphView.getGraphViewStyle().setGridStyle(GridStyle.VERTICAL);
		graphView.getGraphViewStyle().setNumVerticalLabels(3);
		graphView.setHorizontalLabels(xLabels);
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(200);
		graphView.setVerticalLabels(new String[] {"yes", "no"});
		LinearLayout logsLayout = (LinearLayout) findViewById(R.id.device_detail);
		logsLayout.addView(graphView);
	}
}
