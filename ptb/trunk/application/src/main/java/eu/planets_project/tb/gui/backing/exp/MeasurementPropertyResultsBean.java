package eu.planets_project.tb.gui.backing.exp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;

/**
 * Contains for a measurement propertyID all MeasurementResults over all execution runs in a way the
 * HtmlDataTable can handle it. (line information)
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 04.04.2009
 *
 */
public class MeasurementPropertyResultsBean {
	
	private static PlanetsLogger log = PlanetsLogger.getLogger(MeasurementPropertyResultsBean.class, "testbed-log4j.xml");
	
	public MeasurementPropertyResultsBean(String inputDigoRef, String mpropertyID,List<Calendar> allRunDates){
		initResultsHM(allRunDates);
		this.setMeasurementPropertyID(mpropertyID);
		this.setInputDigoRef(inputDigoRef);
	}
	
	private String mID="";
	
	public String getMeasurementPropertyID(){
		return mID;
	}
	public void setMeasurementPropertyID(String mID){
		this.mID = mID;
	}
	
	private String inputDigoRef="";
	/**
	 * For the inputDigitalObject ref this record belongs to
	 */
	public String getInputDigoRef(){
		return inputDigoRef;
	}
	
	public void setInputDigoRef(String inputDigoRef){
		this.inputDigoRef = inputDigoRef;
	}
	
	HashMap<Long,RecordBean> results = new HashMap<Long,RecordBean>();
	
	public HashMap<Long,RecordBean> getAllResults(){
		return results;
	}
	
	public void addResult(Calendar runDate, MeasurementRecordImpl result){
		this.results.put(runDate.getTimeInMillis(), new RecordBean(result));
	}
	
	
	private MeasurementImpl m;
	/**
	 * Not there to retrieve the value, but all other information as name, description, etc.
	 * @return
	 */
	public MeasurementImpl getMeasurementInfo(){
		return m;
	}
	
	public void setMeasurementInfo(MeasurementImpl m){
		this.m = m;
	}
	
	/**
	 * This prevents the hm of returning null for a RecordBean
	 * @param allRunDates
	 */
	private void initResultsHM(List<Calendar> allRunDates){	
		for(Calendar runDate : allRunDates){
			this.results.put(runDate.getTimeInMillis(), new RecordBean());
		}
	}
	
	public class RecordBean{
		
		public RecordBean(){}
		
		public RecordBean(MeasurementRecordImpl mrec){
			this.setRecordValue(mrec.getValue());
		}
		
		private String value = null;
		
		public String getRecordValue(){
			return this.value;
		}
		
		public void setRecordValue(String value){
			this.value = value;
		}
		
	}

}
