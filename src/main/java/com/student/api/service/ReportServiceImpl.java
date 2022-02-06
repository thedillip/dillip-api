package com.student.api.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.student.api.entity.ReportEntity;
import com.student.api.repository.ReportEntityRepository;
import com.student.api.request.WeightSlipRequest;
import com.student.api.response.ReportResponse;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportServiceImpl {
	
	
	@Autowired
	private ReportEntityRepository repository;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public Map<String, String> startReportApi()
	{
		LOGGER.log(Level.INFO, "##############################Report API has been started###################################");
		Map<String, String> hello = new HashMap<>();
		hello.put("message", "API has been started");
		return hello;
	}

	public ResponseEntity<byte[]> exportReport(WeightSlipRequest weightSlipRequest) throws JRException, IOException {
		LOGGER.log(Level.INFO, "Hitting exportReport() method in Service Layer");
		if(weightSlipRequest.isChecked())
		{
			saveWeightSlipDetails(weightSlipRequest);
			LOGGER.log(Level.INFO, "############################# Weight Slip Details Saved Successfully in the PostgresSQL Database #######################");
		}
		
		List<WeightSlipRequest> list = new ArrayList<>();
		list.add(new WeightSlipRequest());

		String netWeight = String.valueOf(Integer.parseInt(weightSlipRequest.getGrossWeight())
				- Integer.parseInt(weightSlipRequest.getTareWeight()));

		ClassPathResource classPathResource = new ClassPathResource("WeightSlip.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(classPathResource.getInputStream());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("address", weightSlipRequest.getAddress());
		parameters.put("vehicleNumber", weightSlipRequest.getVehicleNumber());
		parameters.put("grossWeight", weightSlipRequest.getGrossWeight());
		parameters.put("tareWeight", weightSlipRequest.getTareWeight());
		parameters.put("netWeight", netWeight);
		parameters.put("grossWeightDate", formattedDate(weightSlipRequest.getGrossWeightDate()));
		parameters.put("tareWeightDate", formattedDate(weightSlipRequest.getTareWeightDate()));
		parameters.put("grossWeightTime", formattedTime(weightSlipRequest.getGrossWeightDate()));
		parameters.put("tareWeightTime", formattedTime(weightSlipRequest.getTareWeightDate()));

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

		HttpHeaders headers = new HttpHeaders();
		Date date = new Date();
		headers.set(HttpHeaders.CONTENT_DISPOSITION,
				"attachment;filename=Weight Slip_" + String.valueOf(date) + ".pdf");
		LOGGER.log(Level.INFO, "Generating Report PDF .........");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	}

	public String formattedDate(LocalDateTime date) {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return date.format(myFormatObj);
	}

	public String formattedTime(LocalDateTime date) {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("hh : mm : ss  a");
		return date.format(myFormatObj).toUpperCase();
	}
	
	public String formattedDateTime(LocalDateTime date) {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy  hh : mm : ss a");
		return date.format(myFormatObj).toUpperCase();
	}
	
	public void saveWeightSlipDetails(WeightSlipRequest weightSlipRequest) {
		ReportEntity entity = new ReportEntity();
		String netWeight = String.valueOf(Integer.parseInt(weightSlipRequest.getGrossWeight())
				- Integer.parseInt(weightSlipRequest.getTareWeight()));
		
		try {
			entity.setAddress(weightSlipRequest.getAddress().toUpperCase());
			entity.setVehicleNumber(weightSlipRequest.getVehicleNumber().toUpperCase());
			entity.setGrossWeight(weightSlipRequest.getGrossWeight());
			entity.setTareWeight(weightSlipRequest.getTareWeight());
			entity.setNetWeight(netWeight);
			entity.setGrossWeightDate(weightSlipRequest.getGrossWeightDate());
			entity.setTareWeightDate(weightSlipRequest.getTareWeightDate());
			entity.setCreatedDate(LocalDateTime.now());
			repository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ReportResponse> findAll()
	{
		List<ReportEntity> response = repository.findAll();
		List<ReportResponse> reRespopnse = new ArrayList<>();
		
		try {
			for (ReportEntity report : response) {
				ReportResponse obj = new ReportResponse();
				obj.setAddress(report.getAddress());
				obj.setVehicleNumber(report.getVehicleNumber());
				obj.setGrossWeight(report.getGrossWeight());
				obj.setTareWeight(report.getTareWeight());
				obj.setNetWeight(report.getNetWeight());
				obj.setGrossWeightDate(formattedDateTime(report.getGrossWeightDate()));
				obj.setTareWeightDate(formattedDateTime(report.getTareWeightDate()));
				obj.setCreatedDate(formattedDateTime(report.getCreatedDate()));
				
				reRespopnse.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reRespopnse;
	}

	public List<ReportResponse> findByVehicleNumber(String vehicleNumber) {
		List<ReportEntity> response = repository.findByVehicleNumber(vehicleNumber);
		List<ReportResponse> reRespopnse = new ArrayList<>();
		
		try {
			for (ReportEntity report : response) {
				ReportResponse obj = new ReportResponse();
				obj.setAddress(report.getAddress());
				obj.setVehicleNumber(report.getVehicleNumber());
				obj.setGrossWeight(report.getGrossWeight());
				obj.setTareWeight(report.getTareWeight());
				obj.setNetWeight(report.getNetWeight());
				obj.setGrossWeightDate(formattedDateTime(report.getGrossWeightDate()));
				obj.setTareWeightDate(formattedDateTime(report.getTareWeightDate()));
				obj.setCreatedDate(formattedDateTime(report.getCreatedDate()));
				
				reRespopnse.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reRespopnse;
	}
	public void deleteWeightSlip() {
		repository.deleteAll();
	}
}
