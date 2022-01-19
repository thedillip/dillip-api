package com.student.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.student.api.response.ConsumeUniversityBody;
import com.student.api.response.UniversityDetailsDTO;
import com.student.api.response.UniversityDetailsResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
public class UniversityController {
	
	
	@Operation(summary = "getAllUniversityDetailsByCountryName")
	@GetMapping(path = "/get-university-details")
	public UniversityDetailsResponse getUniversityDetails(@RequestParam String country)
	{
		RestTemplate restTemplate = new RestTemplate();
		List<ConsumeUniversityBody> body = new ArrayList<ConsumeUniversityBody>();
		UniversityDetailsResponse reResponse = new UniversityDetailsResponse();
		List<UniversityDetailsDTO> listUniversity = new ArrayList<UniversityDetailsDTO>();
 		String uri = "http://universities.hipolabs.com/search?country="+country;
		
		try {
			ConsumeUniversityBody[] response = restTemplate.getForObject(uri, ConsumeUniversityBody[].class);
			body=Arrays.asList(response);
		    
		    for(int i=0;i<body.size();i++)
		    {
		    	UniversityDetailsDTO dto = new UniversityDetailsDTO();
		    	
		        dto.setCountryCode(body.get(i).getAlpha_two_code());
		    	dto.setCountryName(body.get(i).getCountry());
		    	dto.setUniversityName(body.get(i).getName());
		    	dto.setUniversityWebsite(body.get(i).getWeb_pages()[0]);
		    	
		    	listUniversity.add(dto);
		    }
		    reResponse.setData(listUniversity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return reResponse;
		
	}
}
