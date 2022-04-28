package com.student.api.controller;


import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.student.api.response.ApiResponse;
import com.student.api.response.MediaFile;
import com.student.api.entity.ContactDetails;
import com.student.api.request.WeightSlipRequest;
import com.student.api.response.ReportResponse;
import com.student.api.service.ReportServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import net.sf.jasperreports.engine.JRException;

@RestController
@CrossOrigin
public class ReportController {
	
	@Autowired
	private ReportServiceImpl service;

        @Operation(summary = "Welcome Message")
	@GetMapping(path = "/")
	public Map<String, String> startApi()
	{
		return service.startReportApi();
	}
	
	@Operation(summary = "Download Weight Slip in PDF")
	@PostMapping(path = "/weightslip")
	public ResponseEntity<MediaFile> generateReport(@RequestBody WeightSlipRequest weightSlipRequest) throws JRException, IOException
	{
		MediaFile mediaFile = service.exportReport(weightSlipRequest);
		return new ResponseEntity<>(mediaFile, HttpStatus.OK);
	}
	
	@Operation(summary = "Find All Weight Slip Details")
	@GetMapping(path = "/weightslipdetails")
	public ResponseEntity<List<ReportResponse>> findReportDetails()
	{
		List<ReportResponse> response = service.findAll();
		return new ResponseEntity<List<ReportResponse>>(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Find Weight Slip Details With Vehicle Number")
	@GetMapping(path = "/weightslipdetails/{vehicleNumber}")
	public ResponseEntity<List<ReportResponse>> findReportDetailsByVehicleNumber(@PathVariable String vehicleNumber)
	{
		List<ReportResponse> response = service.findByVehicleNumber(vehicleNumber);
		return new ResponseEntity<List<ReportResponse>>(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Delete all the weight slip record data from Database")
	@DeleteMapping(path = "/delete")
	public ResponseEntity<ApiResponse> deleteAllWeightSlipRecord()
	{
		service.deleteWeightSlip();
		ApiResponse response = new ApiResponse();
		response.setMessage("All Weight Slip Record Data Deleted Successfully.");
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Send Email")
	@PostMapping(path = "/send-email")
	public ResponseEntity<Map<String, String>> sendEmail(@RequestBody ContactDetails contact)
	{
		Map<String, String> data = null;
		String message = service.sendEmail(contact);
		if(message.equals("SUCCESS"))
		{
			data = new HashMap<>();
			data.put("message", message);
		}
		return new ResponseEntity<Map<String, String>>(data, HttpStatus.OK);
	}
	
}
