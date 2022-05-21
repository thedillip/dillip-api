package com.dillip.api.service;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.dillip.api.dto.BankDetailsDTO;
import com.dillip.api.dto.PinCodeDTO;
import com.dillip.api.dto.PinCodeDetailsDTO;
import com.dillip.api.dto.PostOfficeDetailsDTO;
import com.dillip.api.dto.RandomQuoteDTO;
import com.dillip.api.response.BankDetailsResponse;
import com.dillip.api.response.ConsumeUniversityBody;
import com.dillip.api.util.ProjectConstant;

@Service
public class CommonServiceImpl implements CommonService {
	
	private final static Logger LOGGER = Logger.getLogger("Dillip Logger");

	@Override
	public RandomQuoteDTO getRandomQuote() {
//		RandomQuoteDTO fetchRandomQuote = fetchRandomQuote();
//		return fetchRandomQuote;
		return null;
	}
	
	@Override
	public List<PinCodeDTO> getPinCodeDetails(String pinCode) {
//		List<PinCodeDTO> fetchPinCodeDetails = fetchPinCodeDetails(pinCode);
//		return fetchPinCodeDetails;
		return null;
	}
	
	@Override
	public BankDetailsResponse getBankDetailsByIfsc(String ifscCode) {
		BankDetailsResponse response = null;
		try {
			BankDetailsDTO fetchBankDetailsByIfsc = fetchBankDetailsByIfsc(ifscCode);
			if(fetchBankDetailsByIfsc != null)
			{
				response = new BankDetailsResponse();
				response.setMicrCode(fetchBankDetailsByIfsc.getMICR());
				response.setBranchName(fetchBankDetailsByIfsc.getBRANCH());
				response.setAddress(fetchBankDetailsByIfsc.getADDRESS());
				response.setState(fetchBankDetailsByIfsc.getSTATE());
				response.setContact(fetchBankDetailsByIfsc.getCONTACT());
				response.setCityName(fetchBankDetailsByIfsc.getCITY());
				response.setCenter(fetchBankDetailsByIfsc.getCENTRE());
				response.setDistrict(fetchBankDetailsByIfsc.getDISTRICT());
				response.setSwift(fetchBankDetailsByIfsc.getSWIFT());
				response.setBankName(fetchBankDetailsByIfsc.getBANK());
				response.setBankCode(fetchBankDetailsByIfsc.getBANKCODE());
				response.setIfscCode(fetchBankDetailsByIfsc.getIFSC());
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured in getBankDetailsByIfsc() ServiceImpl Layer ##########", e);
		}
		return response;
	}
	
//	public RandomQuoteDTO fetchRandomQuote()
//	{
//		RandomQuoteDTO body = null;
//		String apiUrl = ProjectConstant.randomQuoteUrl;
//		RestTemplate restTemplate = new RestTemplate();
//		MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
//		headers.add("Content-Type", "application/json");
//		headers.add("Accept", "application/json");
//		
//		try {
//			ResponseEntity<RandomQuoteDTO> response = 
//					restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers),RandomQuoteDTO.class);
//			body = response.getBody();
//		} catch (Exception e) {
//			LOGGER.log(Level.INFO, "############# Exception Occured in fetchRandomQuote() ServiceImpl Layer ##########", e);
//		}
//		return body;
//	}
	
//	public List<PinCodeDTO> fetchPinCodeDetails(String urlParam)
//	{
//		List<PinCodeDTO> reResponse = null;
//		String apiUrl = ProjectConstant.pinCodeDetailsUrl+urlParam;
//		RestTemplate restTemplate = new RestTemplate();
//		MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
//		headers.add("Content-Type", "application/json");
//		headers.add("Accept", "application/json");
		
//		try {
//			PinCodeDetailsDTO[] response = restTemplate.getForObject(apiUrl, PinCodeDetailsDTO[].class);
//			List<PinCodeDetailsDTO> asList = Arrays.asList(response);
//			for(PinCodeDetailsDTO tempObj : asList)
//			{
//				List<PinCodeDTO> postOffice = tempObj.getPostOffice();
//				reResponse = postOffice;
//			}
			
//			ResponseEntity<PostOfficeDetailsDTO> response = 
//					restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers),PostOfficeDetailsDTO.class);
//			PostOfficeDetailsDTO body = response.getBody();
//			List<PinCodeDetailsDTO> data = body.getData();
//			for(PinCodeDetailsDTO tempObj : data)
//			{
//				reResponse = tempObj.getPostOffice();
//			}
//		} catch (Exception e) {
//			LOGGER.log(Level.INFO, "############# Exception Occured in fetchPinCodeDetails() ServiceImpl Layer ##########", e);
//		}
//		return reResponse;
//	}
	
	public BankDetailsDTO fetchBankDetailsByIfsc(String urlParam)
	{
		BankDetailsDTO response = null;
		String apiUrl = ProjectConstant.ifscUrl+urlParam;
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
//		headers.add("Content-Type", "application/json");
//		headers.add("Accept", "application/json");
		
		try {
//			ResponseEntity<BankDetailsDTO> response = 
//					restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers),BankDetailsDTO.class);
//			body = response.getBody();
			
			response = restTemplate.getForObject(apiUrl, BankDetailsDTO.class);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured in fetchBankDetailsByIfsc() ServiceImpl Layer ##########", e);
		}
		return response;
	}
}
