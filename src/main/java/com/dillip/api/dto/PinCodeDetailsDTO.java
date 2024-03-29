package com.dillip.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinCodeDetailsDTO {
	private String Message;
	private String Status;
	private List<PinCodeDTO> PostOffice;
}
