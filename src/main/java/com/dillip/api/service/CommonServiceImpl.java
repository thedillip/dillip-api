package com.dillip.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.dillip.api.dto.RandomQuoteDTO;
import com.dillip.api.response.BankDetailsResponse;
import com.dillip.api.response.PostOfficeDetailsResponse;
import com.dillip.api.util.ProjectConstant;
import com.google.gson.Gson;

@Service
public class CommonServiceImpl implements CommonService {
	
	private final static Logger LOGGER = Logger.getLogger("Dillip Logger");
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Gson gson;

	@Override
	public RandomQuoteDTO getRandomQuote() {
		RandomQuoteDTO response = null;
		String apiUrl = ProjectConstant.randomQuoteUrl;
		String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
		try {
			LOGGER.log(Level.INFO, "############# Hitting getRandomQuote() ServiceImpl Layer ##########");
			if(fetchDataFromOtherApi != null)
			{
				response = gson.fromJson(fetchDataFromOtherApi, RandomQuoteDTO.class);
			}
			else
			{
				response = new RandomQuoteDTO();
				response.setAuthor("Dillip K Sahoo");
				response.setContent("The greatest battle are fought with the closest people.");
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured in getRandomQuote() ServiceImpl Layer ##########", e);
		}
		return response;
	}
	
	@Override
	public List<PostOfficeDetailsResponse> getPostOfficeDetailsByPinCode(String pinCode) {
		String apiUrl = ProjectConstant.pinCodeDetailsUrl+"pincode/"+pinCode;
		List<PostOfficeDetailsResponse> reList = getPostOfficeDetailsByPinCodeOrBranchName(apiUrl);
		return reList;
	}
	
	@Override
	public List<PostOfficeDetailsResponse> getPostOfficeDetailsByBranchName(String branchName) {
		String apiUrl = ProjectConstant.pinCodeDetailsUrl+"postoffice/"+branchName;
		List<PostOfficeDetailsResponse> reList = getPostOfficeDetailsByPinCodeOrBranchName(apiUrl);
		return reList;
	}
	
	@Override
	public BankDetailsResponse getBankDetailsByIfsc(String ifscCode) {
		BankDetailsResponse response = null;
		String apiUrl = ProjectConstant.ifscUrl+ifscCode;
		String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
		try {
			if(fetchDataFromOtherApi != null)
			{
				BankDetailsDTO fetchBankDetailsByIfsc = gson.fromJson(fetchDataFromOtherApi, BankDetailsDTO.class);
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
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured in getBankDetailsByIfsc() ServiceImpl Layer ##########", e);
		}
		return response;
	}
	
	public List<PostOfficeDetailsResponse> getPostOfficeDetailsByPinCodeOrBranchName(String apiUrl) {
		PinCodeDetailsDTO[] response = null;
		List<PinCodeDTO> reResponse = null;
		List<PostOfficeDetailsResponse> reList = new ArrayList<>();
		String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
		try {
			LOGGER.log(Level.INFO, "############# Hitting getPostOfficeDetailsByPinCodeOrBranchName() ServiceImpl Layer ##########");
			if(fetchDataFromOtherApi != null)
			{
				response = gson.fromJson(fetchDataFromOtherApi, PinCodeDetailsDTO[].class);
				List<PinCodeDetailsDTO> asList = Arrays.asList(response);
				for(PinCodeDetailsDTO tempObj : asList)
				{
					reResponse = tempObj.getPostOffice();
				}
			}
			if(reResponse != null)
			{
				for(PinCodeDTO tempPinCodeDto : reResponse)
				{
					PostOfficeDetailsResponse tempPostOfficeDetailsResponse = new PostOfficeDetailsResponse();
					tempPostOfficeDetailsResponse.setPostOfficeName(tempPinCodeDto.getName());
					tempPostOfficeDetailsResponse.setDescription(tempPinCodeDto.getDescription());
					tempPostOfficeDetailsResponse.setBranchType(tempPinCodeDto.getBranchType());
					tempPostOfficeDetailsResponse.setDeliveryStatus(tempPinCodeDto.getDeliveryStatus());
					tempPostOfficeDetailsResponse.setCircleName(tempPinCodeDto.getCircle());
					tempPostOfficeDetailsResponse.setDistrictName(tempPinCodeDto.getDistrict());
					tempPostOfficeDetailsResponse.setDivisionName(tempPinCodeDto.getDivision());
					tempPostOfficeDetailsResponse.setRegionName(tempPinCodeDto.getRegion());
					tempPostOfficeDetailsResponse.setBlockName(tempPinCodeDto.getBlock());
					tempPostOfficeDetailsResponse.setState(tempPinCodeDto.getState());
					tempPostOfficeDetailsResponse.setCountry(tempPinCodeDto.getCountry());
					tempPostOfficeDetailsResponse.setPincode(tempPinCodeDto.getPincode());
					reList.add(tempPostOfficeDetailsResponse);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured in getPostOfficeDetailsByPinCodeOrBranchName() ServiceImpl Layer ##########", e);
		}
		return reList;
	}
	
	
	public String fetchDataFromOtherApi(String apiUrl) {
		String body = null;
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		try {
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers),
					String.class);
			body = response.getBody();
			LOGGER.log(Level.INFO,
					"############# Hitting fetchDataFromOtherApi() ServiceImpl Layer ##########");
		} catch (Exception e) {
			LOGGER.log(Level.INFO,
					"############# Exception Occured in fetchDataFromOtherApi() ServiceImpl Layer ##########", e);
		}
		return body;
	}
}
