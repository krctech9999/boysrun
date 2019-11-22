
/**
 * AChartEngine中文 example 
 *   http://www.dotblogs.com.tw/psjhuo/archive/2013/04/22/102113.aspx
 *   
 *   
 *   chart本體size:GlobalVar.setSize(llBarChart, 1180, 550);
 *				  GlobalVar.setSize(vChart, 1180, 550);
 * 
 */
package com.app.boysrun;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.ormdb.data.BTRecord;
import com.app.boysrun.ormdb.data.User;

public class ChartActivity extends Activity {
	
	final String TAG = "ChartActivity";
	private Context context;
	
	//使用者
	private User user = null;
    
	//區間選擇
	private final int RANGE_MONTH = 0;
	private final int RANGE_YEAR = 1;
	
	//資料方向
	private final int DATA_RIGHT = 1;
	private final int DATA_LEFT = -1;
    
	//資料選擇
	
	private final int DATA_STEP = 0;
	private final int DATA_DIS = 1;
	private final int DATA_CAL = 2;
	
	private int data_cate = DATA_STEP;
	private int rangeType = RANGE_YEAR;
	
	private List<BTRecord> recordList;
	private int idx = 0;

	
	//widget------------------------------
	Button btn_value;
	Button btn_chart;
	
	//------------------------------------
	
	ImageView img_user;
	TextView txv_user;
	
	ImageView img_a;
	ImageView img_b;
	ImageView img_c;
	ImageView img_d;
	
	TextView txv_a;
	TextView txv_b;
	TextView txv_c;
	TextView txv_d;
	TextView txv_e;
	TextView txv_f;
	TextView txv_g;
	TextView txv_h;
	
	//------------------------------------
	
	private LinearLayout LL0;
	private LinearLayout LL1;
	private LinearLayout LL2;
	
	private Button btn_return; 
	
	private TextView txv_chart_title;
	
	private LinearLayout LL_SWITCH;
	
	private Button btn_year;
	private Button btn_month;
	
	private Button btn_left;
	private Button btn_right;
    private LinearLayout llBarChart;
    
    //widget------------------------------
    
    private class ChartValue {
    	private String XTextLabel = "";
    	private float yValue = 0;
    	
    	public ChartValue() {
    		
    	} 
    	
		public String getXTextLabel() {
			return XTextLabel;
		}
		public void setXTextLabel(String xTextLabel) {
			XTextLabel = xTextLabel;
		}
		public float getyValue() {
			return yValue;
		}
		public void setyValue(float yValue) {
			this.yValue = yValue;
		}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		context = this;
		
		findView();
		setWidgetSize();
		init();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	private void findView() {
		LL0 = (LinearLayout) findViewById(R.id.LL0);
		LL1 = (LinearLayout) findViewById(R.id.LL1);
		LL2 = (LinearLayout) findViewById(R.id.LL2);
		
		btn_return = (Button) findViewById(R.id.btn_return);
		
		//------------------------------------------------
		btn_value = (Button) findViewById(R.id.btn_value);
		btn_chart = (Button) findViewById(R.id.btn_chart); 
		//------------------------------------------------
		img_user = (ImageView) findViewById(R.id.img_user); 
		txv_user = (TextView) findViewById(R.id.txv_user);
		
		img_a = (ImageView) findViewById(R.id.img_a);
		img_b = (ImageView) findViewById(R.id.img_b);
		img_c = (ImageView) findViewById(R.id.img_c);
		img_d = (ImageView) findViewById(R.id.img_d);
				
		txv_a = (TextView) findViewById(R.id.txv_a);
		txv_b = (TextView) findViewById(R.id.txv_b);
		txv_c = (TextView) findViewById(R.id.txv_c);
		txv_d = (TextView) findViewById(R.id.txv_d);
		txv_e = (TextView) findViewById(R.id.txv_e);
		txv_f = (TextView) findViewById(R.id.txv_f);
		txv_g = (TextView) findViewById(R.id.txv_g);
		txv_h = (TextView) findViewById(R.id.txv_h);
		
		//------------------------------------------------
		
		txv_chart_title = (TextView) findViewById(R.id.txv_chart_title);
		LL_SWITCH = (LinearLayout) findViewById(R.id.LL_SWITCH);
		
		btn_year = (Button) findViewById(R.id.btn_year);
		btn_month = (Button) findViewById(R.id.btn_month);
		
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		
		llBarChart = (LinearLayout) findViewById(R.id.llBarChart);
	}
	
	private void setWidgetSize() {
		btn_value.setTextSize(23);
		btn_chart.setTextSize(23);
		
		GlobalVar.setMargin(btn_value, 75, 10, 0, 10);
		GlobalVar.setMargin(btn_chart, 75, 10, 0, 10);
		
		//--------------------------------------------
		GlobalVar.setSize(LL0, 1280, 350);
		
		GlobalVar.setSize(img_user, 300, 300);
		
		GlobalVar.setSize(txv_a, 100, 40);
		GlobalVar.setWidth(txv_b, 100);
		GlobalVar.setSize(txv_c, 100, 40);
		GlobalVar.setWidth(txv_d, 100);
		
		GlobalVar.setSize(txv_e, 100, 40);
		GlobalVar.setWidth(txv_f, 100);
		GlobalVar.setSize(txv_g, 100, 40);
		GlobalVar.setWidth(txv_h, 100);
		
		GlobalVar.setSize(img_a, 40, 40);
		GlobalVar.setSize(img_b, 40, 40);
		GlobalVar.setSize(img_c, 40, 40);
		GlobalVar.setSize(img_d, 40, 40);
		
		txv_user.setTextSize(25);
		
		txv_a.setTextSize(20);
		txv_b.setTextSize(25);

		txv_c.setTextSize(20);
		txv_d.setTextSize(25);
		
		txv_e.setTextSize(20);
		txv_f.setTextSize(25);
		
		txv_g.setTextSize(20);
		txv_h.setTextSize(25);
		
		//--------------------------------------------
		
		GlobalVar.setSize(LL1, -1, 150);
		
		GlobalVar.setSize(LL_SWITCH, 550, 150);
		
		GlobalVar.setSize(btn_year, 315, 150);
		GlobalVar.setSize(btn_month, 315, 150);
		
		GlobalVar.setMargin(btn_return, 40, 40, 0, 0);
		btn_return.setTextSize(23);
		
		btn_year.setTextSize(35);
		btn_month.setTextSize(35);
		
		//--------------------------------------------
		
		GlobalVar.setSize(llBarChart, 1180, 430);
		
		GlobalVar.setSize(btn_left, 50, 80);
		GlobalVar.setSize(btn_right, 50, 80);
		
		txv_chart_title.setTextColor(Color.WHITE);
		txv_chart_title.setTextSize(38);
		GlobalVar.setPadding(txv_chart_title, 20, 0, 23, 15);
	}
	
	private void showChart(boolean b) {
		if(b) {
			LL0.setVisibility(View.GONE);
			
			LL1.setVisibility(View.VISIBLE);
			LL2.setVisibility(View.VISIBLE);
		} else {
			LL0.setVisibility(View.VISIBLE);
			
			LL1.setVisibility(View.GONE);
			LL2.setVisibility(View.GONE);
		}
	}
	
	private void init() {
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("bluetooth.user");
		
		showChart(false);
		
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn_value.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showChart(false);
				
			}
		});
		
		btn_chart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showChart(true);
			}
		});
		
		//-------------------------------------------------
		img_user.setBackgroundResource(Misc.getUserPhotoResID(user.getPhotoIdx()));
		txv_user.setText(user.getName());
		
		txv_b.setText(Misc.genStrFloatByDigit(DataActivity.distance_val / 1000, 3) + " km");
		txv_f.setText(Misc.genStrFloatByDigit(DataActivity.calorie_val, 3) + " cal");
		txv_h.setText("" + Math.round(DataActivity.step_val));
		
		//-------------------------------------------------
		
		
		llBarChart.setFocusable(false); //表格不可focus
		recordList = queryDataSet();
		renderChart();
		
		llBarChart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					Log.d(TAG, "onFocus");
				}
			}
		});
		
		btn_year.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					yearState();
				}
			}
		});
		
		btn_month.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					monthState();
				} 
			}
		});
		
		btn_month.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				idx = 0;
				rangeType = RANGE_MONTH;
				recordList = queryDataSet();
				renderChart();
				focusState();
			}
		});
		
		btn_left.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					focusState();
				}
			}
		});
		
		btn_right.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					focusState();
				}
			}
		});
		
		btn_year.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				idx = 0;
				rangeType = RANGE_YEAR;
				recordList = queryDataSet();
				renderChart();
				focusState();
			}
		});
		
		btn_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				idx = 0;
				changeType(DATA_LEFT);
				renderChart();
			}
		});
		
		btn_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				idx = 0;
				changeType(DATA_RIGHT);
				renderChart();
			}
		});
		
		btn_year.requestFocus();
		focusState();
	}
	
	private void focusState() {
		if(rangeType == RANGE_YEAR) {
			yearState();
		} else {
			monthState();
		}
	}
	
	private void yearState() {
		GlobalVar.setSize(btn_year, 315, 150);
		//btn_month.setText("");
		btn_year.setBackgroundResource(R.drawable.b06_01);
		btn_month.setBackgroundResource(R.drawable.transparent);
		
		btn_year.setTextColor(Color.WHITE);
		btn_month.setTextColor(Color.parseColor("#00A0E9"));
		
		GlobalVar.setPadding(btn_year, 95, 0, 0, 25);
		GlobalVar.setPadding(btn_month, 0, 0, 0, 25);  
	}
	
	private void monthState() {
		GlobalVar.setSize(btn_year, 235, 150);
		btn_month.setText("月 Month");
		btn_year.setBackgroundResource(R.drawable.transparent);
		btn_month.setBackgroundResource(R.drawable.b06_02);
		
		btn_year.setTextColor(Color.parseColor("#00A0E9"));
		btn_month.setTextColor(Color.WHITE);
		
		GlobalVar.setPadding(btn_year, 85, 0, 0, 25);
		GlobalVar.setPadding(btn_month, 55, 0, 0, 25);  
	}
	
	private void resetChartTitle() {
		String chart_date_time = "";
		Date now = new Date();

		switch (rangeType) {
		case RANGE_MONTH:
			chart_date_time = Misc.convertSimpleDateStr(now, Misc.format_MM) + "月";
			break;
		case RANGE_YEAR:
			chart_date_time = Misc.convertSimpleDateStr(now, Misc.format_year) + "年";
			break;
		}

		switch (data_cate) {
		case DATA_STEP:
			txv_chart_title.setText("步數" + "/" + chart_date_time);
			break;
		case DATA_DIS:
			txv_chart_title.setText("距離" + "/" + chart_date_time);
			break;
		case DATA_CAL:
			txv_chart_title.setText("卡路里" + "/" + chart_date_time);
			break;
		}
	}
	
	private void changeType(final int dir) {
		switch(dir) {
		case DATA_RIGHT:
			data_cate++;
			if(data_cate > DATA_CAL) {
				data_cate = DATA_STEP;
			}
			
			break;
		case DATA_LEFT:
			data_cate--;
			if(data_cate < DATA_STEP){
				data_cate = DATA_CAL;
			}
			break;
		}
	}
	
	private List<BTRecord> queryDataSet() {
		Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

		Calendar cldr_low = Calendar.getInstance(); // this takes current date
		cldr_low.set(Calendar.DAY_OF_MONTH, cldr_low.getActualMinimum(Calendar.DAY_OF_MONTH));
		cldr_low.set(Calendar.HOUR_OF_DAY,cldr_low.getActualMinimum(Calendar.HOUR_OF_DAY));
		cldr_low.set(Calendar.MINUTE, 0);
		cldr_low.set(Calendar.SECOND, 0);
		Calendar cldr_high = Calendar.getInstance();
		cldr_high.set(Calendar.DAY_OF_MONTH, cldr_high.getActualMaximum(Calendar.DAY_OF_MONTH));
		cldr_high.set(Calendar.HOUR_OF_DAY,cldr_low.getActualMaximum(Calendar.HOUR_OF_DAY));
		cldr_high.set(Calendar.MINUTE, 59);
		cldr_high.set(Calendar.SECOND, 59);
		Log.d(TAG, "getActualMinimum = " + cldr_low.getActualMinimum(Calendar.DAY_OF_MONTH));
		Log.d(TAG, "getActualMaximum = " + cldr_high.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Date lowDate = cldr_low.getTime();
		Date highDate = cldr_high.getTime();
		
		List<BTRecord> rcdList = new ArrayList<BTRecord>();
		try {
			if(rangeType == RANGE_YEAR){
				rcdList = GlobalVar.recordDao.queryBuilder().
					where().eq("userName", user.getName()).query();
			} else {
				rcdList = GlobalVar.recordDao.queryBuilder().
					where().between("date", (Date) lowDate, (Date) highDate).and().
					eq("userName", user.getName()).query();
			}
		} catch (SQLException e1) {
			Log.e(TAG, "renderChart exception");
			e1.printStackTrace();
		}
		
		Collections.sort(rcdList);
		return rcdList;
	}
	
	private String getCateTitle() {
		String title = "";
		switch(data_cate) {
		case DATA_STEP:
			title = "步數";
			break;
		case DATA_DIS:
			title = "距離(m)";
			break;
		case DATA_CAL:
			title = "卡路里";
			break;
		default:
			title = "";
			break;
		}
		return title;
	}

	private BTRecord cloneRecord(BTRecord record) {
		BTRecord clone = new BTRecord();
		clone.setCalorie(record.getCalorie());
		clone.setDate(record.getDate());
		clone.setDeviceName(record.getDeviceName());
		clone.setDistance(record.getDistance());
		clone.setStep(record.getStep());
		clone.setUserName(record.getUserName());
		return clone;
	}
	
	/**
	 * 加總同使用者同一天數個不同裝置的數值
	 */
	private List<BTRecord> sumSameDateData() {
		Collections.sort(recordList);
		List<BTRecord> renderList = new ArrayList<BTRecord>();
				
		// 加總同使用者同一天數個不同裝置的數值
		for (BTRecord rcd : recordList) {
			if (renderList.size() > 0) {
				BTRecord existRcd = renderList.get(renderList.size() - 1);
				if (Misc.isSameDate(existRcd.getDate(), rcd.getDate())) {
					existRcd.setCalorie(existRcd.getCalorie() + rcd.getCalorie());
					existRcd.setStep(existRcd.getStep() + rcd.getStep());
					existRcd.setDistance(existRcd.getDistance() + rcd.getDistance());
				} else {
					renderList.add(cloneRecord(rcd));
				}
			} else {
				renderList.add(cloneRecord(rcd));
			}
		}
		
		return renderList;
	}
	
	/**
	 * Render user chart
	 */
	private void renderChart() {
		resetChartTitle();
		List<ChartValue> valueList = new ArrayList<ChartValue>();
		
		List<BTRecord> tmpList = sumSameDateData();
		
		if (rangeType == RANGE_MONTH) {
			for (BTRecord record : tmpList) {
				ChartValue value = new ChartValue();
				value.setXTextLabel(Misc.convertSimpleDateStr(record.getDate(), Misc.format_dd));
				value.setyValue(valueByCate(record));
				valueList.add(value);
			}
		} else if(rangeType == RANGE_YEAR) {
			Map<Integer, ChartValue> valueMap = new HashMap<Integer, ChartValue>();
			
			for(BTRecord record : tmpList) {
				if (new Date().getYear() == record.getDate().getYear()) {
					int mm = record.getDate().getMonth() + 1; //月份
					if (valueMap.containsKey(mm)) {
						ChartValue existValue = valueMap.get(mm);
						existValue.setyValue(existValue.getyValue() + valueByCate(record));
						valueMap.put(mm, existValue);
					} else {
						ChartValue value = new ChartValue();
						value.setXTextLabel("" + mm);
						value.setyValue(valueByCate(record));
						valueMap.put(mm, value);
					}
				}
			}
			
			Set<Integer> tmpkeys = (Set<Integer>) valueMap.keySet();
			List<Integer> keys = new ArrayList<Integer>();
			for(Integer key : tmpkeys) {
				keys.add(key);
			}
			Collections.sort(keys);
			
			for(Integer key : keys) {
				valueList.add(valueMap.get(key));
			}
		}
		
		View vChart = geneBarChart(getCateTitle(), "Code", "", valueList, idx);
		
		llBarChart.removeAllViews();
		llBarChart.addView(vChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
		
		GlobalVar.setSize(vChart, 1180, 430);
	}

	/**
	 * 設定圖外margine顏色  renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01))
	 * AchartEngine的bug:
	 * http://stackoverflow.com/questions/9314342/achartengine-transparent-background 
	 * 
	 * @param chartTitle
	 * @param XTitle
	 * @param YTitle
	 * @param valueList
	 * @param index
	 * @return
	 */
	private View geneBarChart(String chartTitle, String XTitle, String YTitle,
			final List<ChartValue> valueList, int index) {
		
		XYSeries xySeries = new XYSeries(YTitle);

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(xySeries);

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setInScroll(true); //in order to avoid the issues 
		
		XYSeriesRenderer yRenderer = new XYSeriesRenderer();
		yRenderer.setPointStyle(PointStyle.CIRCLE);   //端點的形狀   <------------------------
		
		yRenderer.setFillPoints(true);   // <------------------
		yRenderer.setLineWidth(3f);
		
		renderer.addSeriesRenderer(yRenderer);
		renderer.setPointSize(7.5f); //點的大小
		renderer.setShowLegend(false); // <------------------ 不顯示X軸左下角 legend

		/*********************************************************/
		renderer.setApplyBackgroundColor(true);   //設定背景顏色
		renderer.setBackgroundColor(Color.TRANSPARENT); //設定圖內圍背景顏色
		/*********************************************************/
		renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01)); // 設定圖外margine顏色(achrtengine bug)
		renderer.setTextTypeface(null, Typeface.BOLD); // 設定文字style
		/*********************************************************/
		
		renderer.setShowGrid(false);            // 設定顯示網格線
		renderer.setGridColor(Color.WHITE); 	// 設定網格顏色

		 // renderer.setChartTitle(chartTitle); 	// 設定標頭文字(disable)
		renderer.setLabelsColor(Color.WHITE); 	// 設定標頭文字顏色
		renderer.setChartTitleTextSize(20); 	// 設定標頭文字大小     
		renderer.setAxesColor(Color.WHITE); 	// 設定雙軸顏色
		renderer.setLabelsTextSize(23); 		// XY軸單位文字大小   <-----------------------
		renderer.setBarSpacing(0.5); 			// 設定bar間的距離(0.5) <----------------------

		// renderer.setXTitle(XTitle); 			//設定X軸文字
		// renderer.setYTitle(YTitle); 			//設定Y軸文字
		
		renderer.setXLabelsColor(Color.WHITE); // 設定X軸文字顏色
		renderer.setYLabelsColor(0, Color.WHITE); // 設定Y軸文字顏色
		renderer.setXLabelsAlign(Align.CENTER); // 設定X軸文字置中
		renderer.setYLabelsAlign(Align.RIGHT); // 設定Y軸文字對齊    <--------------------------
		renderer.setXLabelsAngle(0);  // 設定X軸文字傾斜度 <---------------------
		
		renderer.setMargins(new int[] {0, 115, 20, 115}); // 設定margine(左右設大一點,使Y軸label數值不被切掉)  <--------------------------
		renderer.setXAxisMin(0);   		//-----(X軸最小index(格數))==============================================
		if(rangeType == RANGE_MONTH) {  //-----(X軸最大index(格數),依資料型態決定)==============================================
			//month資料數:31
			renderer.setXAxisMax(31); 		
		} else {
			//year資料數:12
			renderer.setXAxisMax(14);
		}
		
		renderer.setXLabels(0); // 設定X軸不顯示數字, 改以程式設定文字
		renderer.setYAxisMin(0); // 設定Y軸文最小值
		renderer.setShowGridX(false); // 是否顯示Y軸的對齊線  <---------------
		renderer.setShowGridY(false);

		renderer.setPanEnabled(false);                  //<------------------ (取消滑鼠pan)
		
		/****(test   edit after testing)***************/
		
		//	test...test
		
		/****(test)************************************/
		
		yRenderer.setColor(Color.WHITE); // 設定Series顏色
		// yRenderer.setDisplayChartValues(true); //展現Series數值
		
		renderer.addXTextLabel(0, "");

		List<Float> valList = new ArrayList<Float>();
		int i = 0;
		for(ChartValue value : valueList) {
			//X軸時間字串
			renderer.addXTextLabel(i + 1, value.getXTextLabel()); 
			xySeries.add(i + 1, value.getyValue());
			valList.add(value.getyValue());
			i++;
		}
		
		try {
			Collections.sort(valList);
			renderer.setYAxisMax(valList.get(valList.size() - 1) * 1.35); // 設Y軸座標最大值 <--------------------
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//renderer.addXTextLabel(recordList.size() + 1, "");
		
		View view;
		//view = ChartFactory.getBarChartView(getBaseContext(), dataset, Renderer, Type.DEFAULT);
		view = ChartFactory.getLineChartView(getBaseContext(), dataset, renderer);
		return view;
	}
	
	private float valueByCate(BTRecord record) {
		float value;
		switch(data_cate) {
		case DATA_STEP:
			value = record.getStep();
			break;
		case DATA_DIS:
			value = record.getDistance();
			break;
		case DATA_CAL:
			value = record.getCalorie();
			break;
		default:
			value = record.getStep();
			break;
		}
		return value;
	}
}
