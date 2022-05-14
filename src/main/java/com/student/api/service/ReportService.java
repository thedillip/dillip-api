package com.student.api.service;

import java.io.IOException;
import java.util.List;

import com.student.api.entity.ContactDetails;
import com.student.api.request.WeightSlipRequest;
import com.student.api.response.MediaFile;
import com.student.api.response.ReportResponse;

import net.sf.jasperreports.engine.JRException;

public interface ReportService {
	String startReportApi();
	
	MediaFile exportReport(WeightSlipRequest weightSlipRequest) throws JRException, IOException;
	
	List<ReportResponse> findAll();
	
	List<ReportResponse> findByVehicleNumber(String vehicleNumber);
	
	String deleteAllWeightSlip();
	
	String sendEmail(ContactDetails contact);
}
