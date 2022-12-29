package backend.pages;

import java.util.ArrayList;
import java.util.HashMap;

import backend.Element;

public class Report {
	HashMap<ReportListType, ArrayList<Element.ElementInstance>> listMap = new HashMap<ReportListType, ArrayList<Element.ElementInstance>>();
	
	public Report()
	{
		for (ReportListType reportListType : ReportListType.values())
		{
			listMap.put(reportListType, new ArrayList<Element.ElementInstance>());
		}
	}
	
	private enum ReportListType
	{
		MADEELEMENTINSTANCES, REMOVEDELEMENTINSTANCES;
	}
	
	public void addMadeElement(Element.ElementInstance elementInstance) throws Exception
	{
		if (elementInstance == null)
			throw new Exception("Adding a null to a report.");
		ArrayList<Element.ElementInstance> list = this.listMap.get(ReportListType.MADEELEMENTINSTANCES);
		list.add(elementInstance);
	}
	
	public void addRemovedElement(Element.ElementInstance elementInstance) throws Exception
	{
		if (elementInstance == null)
			throw new Exception("Adding a null to a report.");
		ArrayList<Element.ElementInstance> list = this.listMap.get(ReportListType.REMOVEDELEMENTINSTANCES);
		list.add(elementInstance);
	}
	
	public ArrayList<Element.ElementInstance> getReportList(String reportElementName)
	{
		ReportListType reportList = ReportListType.valueOf(reportElementName.toUpperCase());
		return this.listMap.get(reportList);
	}
}
