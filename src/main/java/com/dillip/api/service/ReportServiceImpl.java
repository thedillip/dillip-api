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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.dillip.api.dto.UniversityDetailsDTO;
import com.dillip.api.entity.ContactDetails;
import com.dillip.api.entity.ReportEntity;
import com.dillip.api.repository.ReportEntityRepository;
import com.dillip.api.request.WeightSlipRequest;
import com.dillip.api.response.ConsumeUniversityBody;
import com.dillip.api.response.MediaFile;
import com.dillip.api.response.ReportResponse;
import com.dillip.api.util.ProjectConstant;
import com.google.gson.Gson;

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
	
	@Autowired
	private CommonServiceImpl commonServiceImpl;
	
	@Autowired
	private Gson gson;

	private final static Logger LOGGER = Logger.getLogger("Dillip Logger");
	
	@Value("${universitydetails.url}")
	private String universityDetailsUrl;

	@Override
	public String startReportApi() {
		LOGGER.log(Level.INFO,
				"########## API has been Started :: Status :: UP :: SUCCESS ##########");
		String message = ProjectConstant.SUCCESS_MSG;
		return message;
	}

	@Override
	public MediaFile exportReport(WeightSlipRequest weightSlipRequest) throws JRException, IOException {
		
		LOGGER.log(Level.INFO, "########## Hitting exportReport() method in ServiceImpl Layer ##########");

		String fileName = "Weight Slip_" + weightSlipRequest.getVehicleNumber().toUpperCase() + "_"
				+ ProjectConstant.formattedDateTime(LocalDateTime.now()) + ".pdf";

		if (weightSlipRequest.isChecked()) {
			saveWeightSlipDetails(weightSlipRequest);
			LOGGER.log(Level.INFO,
					"########## Weight Slip Details Saved Successfully in the PostgresSQL Database ##########");
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
		LOGGER.log(Level.INFO, "########## Report Generated in PDF ......... ##########");
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
		
		LOGGER.log(Level.INFO, "########## Entered into saveWeightSlipDetails() :: WeightSlipRequest :: "+weightSlipRequest);

		try {
			entity.setAddress(weightSlipRequest.getAddress().toUpperCase());
			entity.setVehicleNumber(weightSlipRequest.getVehicleNumber().toUpperCase());
			entity.setGrossWeight(weightSlipRequest.getGrossWeight());
			entity.setTareWeight(weightSlipRequest.getTareWeight());
			entity.setNetWeight(netWeight);
			entity.setGrossWeightDate(weightSlipRequest.getGrossWeightDate());
			entity.setTareWeightDate(weightSlipRequest.getTareWeightDate());
			entity.setCreatedDate( ProjectConstant.convertUTCtoISTtime(LocalDateTime.now()));
			repository.save(entity);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "########## Exception Occured While Saving the Weight Slip Details in saveWeightSlipDetails() in ServiceImpl Layer ##########"+e);
		}
	}

	@Override
	public List<ReportResponse> findAllWeightSlipDetails() {
		List<ReportEntity> response = repository.findAll();
		List<ReportResponse> reRespopnse = new ArrayList<>();

		try {
			LOGGER.log(Level.INFO, "########## Hitting findAllWeightSlipDetails() for getting all WeightSlip Details in ServiceImpl Layer ##########");
			for (ReportEntity report : response) {
				ReportResponse obj = new ReportResponse();
				obj.setAddress(report.getAddress());
				obj.setVehicleNumber(report.getVehicleNumber());
				obj.setGrossWeight(report.getGrossWeight()+" KG");
				obj.setTareWeight(report.getTareWeight()+ " KG");
				obj.setNetWeight(report.getNetWeight()+ " KG");
				obj.setGrossWeightDate(formattedDateTime(report.getGrossWeightDate()));
				obj.setTareWeightDate(formattedDateTime(report.getTareWeightDate()));
				obj.setCreatedDate(formattedDateTime(report.getCreatedDate()));

				reRespopnse.add(obj);
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "########## Exception Occured while fetching the Weight Slip Details in findAllWeightSlipDetails() in ServiceImpl Layer ##########"+e);
		}
		return reRespopnse;
	}

	@Override
	public List<ReportResponse> findByVehicleNumber(String vehicleNumber) {
		List<ReportEntity> response = repository.findByVehicleNumber(vehicleNumber);
		List<ReportResponse> reRespopnse = new ArrayList<>();

		try {
			LOGGER.log(Level.INFO, "########## Entered in findByVehicleNumber() in ServiceImpl Layer ##########");
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
			LOGGER.log(Level.INFO, "########## Exception Occured while fetching the Weight Slip Details in findByVehicleNumber() in ServiceImpl Layer ########## "+e);
		}
		return reRespopnse;
	}

	@Override
	public String deleteAllWeightSlip() {
		repository.deleteAll();
		LOGGER.log(Level.INFO, "########## Entered into deleteAllWeightSlip() in ServiceImpl Layer ##########");
		String message = ProjectConstant.SUCCESS_MSG;
		return message;
	}

	@Override
	public String sendEmail(ContactDetails contact) {
		
		LOGGER.log(Level.INFO, "########## Entered into sendEmail() in ServiceImpl Layer ##########");

		String emailBody = "Dear, " + contact.getName() + "\n\n"
				+ "I hope you are having a productive day.\n\nI greatly appreciate the time you spent for visiting my Portfolio.\n\n"
				+ "Thank you for sharing your valuable feedback - Keep in Touch"
				+ "\n\nNOTE: This is an auto generated mail. Please do not reply to this message or on this email address.\n\n"
				+ "Thanks & Regards \nDillip K Sahoo\nContact Number :- +91 8117941692\nMailto:- lit.dillip2017@gmail.com\nWebsite:- https://dillipfolio.web.app";

		String subject = "Welcome to DillipFolio – Thanks for Visiting !!";
		
		LOGGER.log(Level.INFO, "########## Email Body ########## :: Email Content :: "+emailBody);

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("dongjinmaster9@gmail.com");
		message.setTo(contact.getEmail());
		message.setText(emailBody);
		message.setSubject(subject);

		mailSender.send(message);

		LOGGER.log(Level.INFO, "########## Mail has been send Successfully :: SUCCESS ##########");

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
		String apiUrl = universityDetailsUrl+"?country=" + countryName;

		try {
			LOGGER.log(Level.INFO, "########## Entered in getUniversityDetailsByCountryName() in ServiceImpl Layer :: apiUrl :: "+apiUrl);
			String fetchDataFromOtherApi = commonServiceImpl.fetchDataFromOtherApi(apiUrl);
			ConsumeUniversityBody[] fromJson = gson.fromJson(fetchDataFromOtherApi, ConsumeUniversityBody[].class);
			body = Arrays.asList(fromJson);

			for (int i = 0; i < body.size(); i++) {
				UniversityDetailsDTO dto = new UniversityDetailsDTO();

				dto.setCountryCode(body.get(i).getAlpha_two_code());
				dto.setCountryName(body.get(i).getCountry());
				dto.setUniversityName(body.get(i).getName());
				dto.setUniversityWebsite(body.get(i).getWeb_pages()[0]);	

				listUniversity.add(dto);
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "########## Exception Occured in getUniversityDetailsByCountryName() in ServiceImpl Layer ########## "+e);
		}
		return listUniversity;
	}
}
