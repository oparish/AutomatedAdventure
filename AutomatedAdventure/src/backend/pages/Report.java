package backend.pages;

import java.util.ArrayList;
import java.util.HashMap;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Element.ElementInstance.AdjustmentInstance;
import backend.ReportInstance;

public class Report {
	HashMap<ReportListType, ArrayList<ReportInstance>> listMap = new HashMap<ReportListType, ArrayList<ReportInstance>>();
	
	public Report()
	{
		for (ReportListType reportListType : ReportListType.values())
		{
			listMap.put(reportListType, new ArrayList<ReportInstance>());
		}
	}
	
	private enum ReportListType
	{
		MADEELEMENTINSTANCES, REMOVEDELEMENTINSTANCES, ADJUSTMENT;
	}
	
	public void addMadeElement(Element.ElementInstance elementInstance) throws Exception
	{
		if (elementInstance == null)
			throw new Exception("Adding a null to a report.");
		ArrayList<ReportInstance> list = this.listMap.get(ReportListType.MADEELEMENTINSTANCES);
		list.add(elementInstance);
	}
	
	public void addRemovedElement(Element.ElementInstance elementInstance) throws Exception
	{
		if (elementInstance == null)
			throw new Exception("Adding a null to a report.");
		ArrayList<ReportInstance> list = this.listMap.get(ReportListType.REMOVEDELEMENTINSTANCES);
		list.add(elementInstance);
	}
	
	public void addStringAdjustment(ElementInstance elementInstance, String qualityName, String value)
	{
		AdjustmentInstance adjustmentInstance = elementInstance.makeAdjustmentInstance(qualityName, value);
		ArrayList<ReportInstance> list = this.listMap.get(ReportListType.ADJUSTMENT);
		list.add(adjustmentInstance);
	}
	
	public void addIntegerAdjustment(ElementInstance elementInstance, String qualityName, int value)
	{
		AdjustmentInstance adjustmentInstance = elementInstance.makeAdjustmentInstance(qualityName, String.valueOf(value));
		ArrayList<ReportInstance> list = this.listMap.get(ReportListType.ADJUSTMENT);
		list.add(adjustmentInstance);
	}
	
	public ArrayList<ReportInstance> getReportList(String reportElementName)
	{
		ReportListType reportList = ReportListType.valueOf(reportElementName.toUpperCase());
		return this.listMap.get(reportList);
	}
}
