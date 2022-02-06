package com.student.api.controller;


import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<byte[]> generateReport(@RequestBody WeightSlipRequest weightSlipRequest) throws JRException, IOException
	{
		return service.exportReport(weightSlipRequest);
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
}
