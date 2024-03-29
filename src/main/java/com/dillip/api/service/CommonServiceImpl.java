package com.dillip.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.dillip.api.dto.UniversityDetailsDTO;
import com.dillip.api.response.BankDetailsResponse;
import com.dillip.api.response.ConsumeUniversityBody;
import com.dillip.api.response.PostOfficeDetailsResponse;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Gson gson;

	@Value("${randomquote.url}")
	private String randomQuoteUrl;
	@Value("${pincode.url}")
	private String pinCodeUrl;
	@Value("${bankdetails.url}")
	private String bankDetailsUrl;
	@Value("${universitydetails.url}")
	private String universityDetailsUrl;

	@Override
	public RandomQuoteDTO getRandomQuote() {
		RandomQuoteDTO response = null;
		String apiUrl = randomQuoteUrl;
		String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
		try {
			log.info("############# Hitting getRandomQuote() ServiceImpl Layer :: apiUrl :: " + apiUrl);
			if (fetchDataFromOtherApi != null) {
				response = gson.fromJson(fetchDataFromOtherApi, RandomQuoteDTO.class);
			} else {
				response = new RandomQuoteDTO();
				response.setAuthor("Dillip K Sahoo");
				response.setContent("The greatest battle are fought with the closest people.");
			}
		} catch (Exception e) {
			log.info("############# Exception Occured in getRandomQuote() in ServiceImpl Layer ##########" + e);
		}
		return response;
	}

	@Override
	public List<PostOfficeDetailsResponse> getPostOfficeDetailsByPinCode(String pinCode) {
		String apiUrl = pinCodeUrl + "pincode/" + pinCode;
		log.info("############# Hitting getPostOfficeDetailsByPinCode() ServiceImpl Layer :: apiUrl :: " + apiUrl);
		List<PostOfficeDetailsResponse> reList = getPostOfficeDetailsByPinCodeOrBranchName(apiUrl);
		return reList;
	}

	@Override
	public List<PostOfficeDetailsResponse> getPostOfficeDetailsByBranchName(String branchName) {
		String apiUrl = pinCodeUrl + "postoffice/" + branchName;
		log.info("############# Hitting getPostOfficeDetailsByBranchName() ServiceImpl Layer :: apiUrl :: " + apiUrl);
		List<PostOfficeDetailsResponse> reList = getPostOfficeDetailsByPinCodeOrBranchName(apiUrl);
		return reList;
	}

	@Override
	public BankDetailsResponse getBankDetailsByIfsc(String ifscCode) {
		BankDetailsResponse response = null;
		String apiUrl = bankDetailsUrl + ifscCode;
		log.info("############# Hitting getBankDetailsByIfsc() ServiceImpl Layer :: apiUrl :: " + apiUrl);
		String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
		try {
			if (fetchDataFromOtherApi != null) {
				BankDetailsDTO fetchBankDetailsByIfsc = gson.fromJson(fetchDataFromOtherApi, BankDetailsDTO.class);
				if (fetchBankDetailsByIfsc != null) {
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
					log.info("############# Executed Logic in getBankDetailsByIfsc() in ServiceImpl Layer ##########");
				}
			}
		} catch (Exception e) {
			log.info("############# Exception Occured in getBankDetailsByIfsc() ServiceImpl Layer ##########" + e);
		}
		return response;
	}

	public List<PostOfficeDetailsResponse> getPostOfficeDetailsByPinCodeOrBranchName(String apiUrl) {
		PinCodeDetailsDTO[] response = null;
		List<PinCodeDTO> reResponse = null;
		List<PostOfficeDetailsResponse> reList = new ArrayList<>();
		String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
		try {
			log.info(
					"############# Hitting getPostOfficeDetailsByPinCodeOrBranchName() ServiceImpl Layer :: apiUrl ########## "
							+ apiUrl);
			if (fetchDataFromOtherApi != null) {
				response = gson.fromJson(fetchDataFromOtherApi, PinCodeDetailsDTO[].class);
				List<PinCodeDetailsDTO> asList = Arrays.asList(response);
				for (PinCodeDetailsDTO tempObj : asList) {
					reResponse = tempObj.getPostOffice();
				}
			}
			if (reResponse != null) {
				for (PinCodeDTO tempPinCodeDto : reResponse) {
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
			log.info(
					"############# Exception Occured in getPostOfficeDetailsByPinCodeOrBranchName() ServiceImpl Layer ##########"
							+ e);
		}
		return reList;
	}

	public String fetchDataFromOtherApi(String apiUrl) {
		String body = null;
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		log.info("###### APIURL :: " + apiUrl);
		try {
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers),
					String.class);
			body = response.getBody();
			log.info("############# Hitting fetchDataFromOtherApi() ServiceImpl Layer ##########");
		} catch (Exception e) {
			log.info("############# Exception Occured in fetchDataFromOtherApi() ServiceImpl Layer ##########", e);
		}
		return body;
	}

	@Override
	public List<UniversityDetailsDTO> getUniversityDetailsByCountryName(String countryName) {
		List<ConsumeUniversityBody> body = null;
		List<UniversityDetailsDTO> listUniversity = new ArrayList<UniversityDetailsDTO>();
		String apiUrl = universityDetailsUrl + "?country=" + countryName;

		try {
			log.info("########## Entered in getUniversityDetailsByCountryName() in ServiceImpl Layer :: apiUrl :: "
					+ apiUrl);
			String fetchDataFromOtherApi = fetchDataFromOtherApi(apiUrl);
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
			log.info(
					"########## Exception Occured in getUniversityDetailsByCountryName() in ServiceImpl Layer ########## "
							+ e);
		}
		return listUniversity;
	}
}
