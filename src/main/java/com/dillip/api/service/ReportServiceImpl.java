package com.dillip.api.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dillip.api.entity.ContactDetails;
import com.dillip.api.entity.ReportEntity;
import com.dillip.api.repository.ReportEntityRepository;
import com.dillip.api.request.UniversityDetailsDTO;
import com.dillip.api.request.WeightSlipRequest;
import com.dillip.api.response.ConsumeUniversityBody;
import com.dillip.api.response.MediaFile;
import com.dillip.api.response.ReportResponse;
import com.dillip.api.util.ProjectConstant;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	private ReportEntityRepository repository;

	@Autowired
	private JavaMailSender mailSender;

	private final static Logger LOGGER = Logger.getLogger("Dillip Logger");

	@Override
	public String startReportApi() {
		LOGGER.log(Level.INFO,
				"##############################Report API has been started###################################");
		String message = ProjectConstant.SUCCESS_MSG;
		return message;
	}

	@Override
	public MediaFile exportReport(WeightSlipRequest weightSlipRequest) throws JRException, IOException {
		LOGGER.log(Level.INFO, "Hitting exportReport() method in Service Layer");

		String fileName = "Weight Slip_" + weightSlipRequest.getVehicleNumber().toUpperCase() + "_"
				+ formattedDateTime(LocalDateTime.now()) + ".pdf";

		if (weightSlipRequest.isChecked()) {
			saveWeightSlipDetails(weightSlipRequest);
			LOGGER.log(Level.INFO,
					"############################# Weight Slip Details Saved Successfully in the PostgresSQL Database #######################");
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

		MediaFile mediaFile = new MediaFile();
		mediaFile.setFileName(fileName);
		mediaFile.setByteData(data);

		Date date = new Date();
		headers.set(HttpHeaders.CONTENT_DISPOSITION,
				"attachment;filename=Weight Slip_" + String.valueOf(date) + ".pdf");
		LOGGER.log(Level.INFO, "Generating Report PDF .........");
		return mediaFile;
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
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm:ss a");
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
			LOGGER.log(Level.INFO, "#############Exception Occured############", e);
		}
	}

	@Override
	public List<ReportResponse> findAll() {
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

	@Override
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

	@Override
	public String deleteAllWeightSlip() {
		repository.deleteAll();
		String message = ProjectConstant.SUCCESS_MSG;
		return message;
	}

	@Override
	public String sendEmail(ContactDetails contact) {

		String emailBody = "Dear, " + contact.getName() + "\n\n"
				+ "I hope you are having a productive day.\n\nI greatly appreciate the time you spent for visiting my Portfolio.\n\n"
				+ "Thank you for sharing your valuable feedback - Keep in Touch"
				+ "\n\nNOTE: This is an auto generated mail. Please do not reply to this message or on this email address.\n\n"
				+ "Thanks & Regards \nDillip K Sahoo\nContact Number :- +91 8117941692\nMailto:- lit.dillip2017@gmail.com\nWebsite:- https://dillipfolio.web.app";

		String subject = "Welcome to DillipFolio! – Thanks for joining";

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("dongjinmaster9@gmail.com");
		message.setTo(contact.getEmail());
		message.setText(emailBody);
		message.setSubject(subject);

		mailSender.send(message);

		System.out.println("EMAIL BODY ##############" + emailBody);

		ContactDetails contactDetails = new ContactDetails();
		contactDetails.setName(contact.getName());
		contactDetails.setEmail(contact.getEmail());
		contactDetails.setMessage(contact.getMessage());
		contactDetails.setSubject(contact.getSubject());

		return ProjectConstant.SUCCESS_MSG;
	}

	@Override
	public List<UniversityDetailsDTO> getUniversityDetailsByCountryName(String countryName) {
		List<ConsumeUniversityBody> body = null;
		List<UniversityDetailsDTO> listUniversity = new ArrayList<UniversityDetailsDTO>();
		String uri = "http://universities.hipolabs.com/search?country=" + countryName;

		try {
			RestTemplate restTemplate = new RestTemplate();
			ConsumeUniversityBody[] response = restTemplate.getForObject(uri, ConsumeUniversityBody[].class);
			body = Arrays.asList(response);

			for (int i = 0; i < body.size(); i++) {
				UniversityDetailsDTO dto = new UniversityDetailsDTO();

				dto.setCountryCode(body.get(i).getAlpha_two_code());
				dto.setCountryName(body.get(i).getCountry());
				dto.setUniversityName(body.get(i).getName());
				dto.setUniversityWebsite(body.get(i).getWeb_pages()[0]);

				listUniversity.add(dto);
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "########## Exception Occured ##########", e);
		}
		return listUniversity;
	}
}
