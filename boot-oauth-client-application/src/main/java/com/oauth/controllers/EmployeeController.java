package com.oauth.controllers;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class EmployeeController {

	@RequestMapping(value = "/getEmployees", method = RequestMethod.GET)
	public ModelAndView getEmployeeInfo() {
		return new ModelAndView("getEmployees");
	}

	@RequestMapping(value = "/importEmployee", method = RequestMethod.GET)
	public ModelAndView testImport(@RequestParam("code") String code) throws JsonProcessingException, IOException {
		ResponseEntity<String> response = null;
		System.out.println("code------" + code);

		RestTemplate restTemplate = new RestTemplate();

		String credentials = "client_id:secret";
		String encoded = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encoded);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String url = "http://localhost:8080/oauth/token";
		url += "?code=" + code;
		url += "&grant_type=authorization_code";
		url += "&redirect_uri=http://localhost:8090/importEmployee";
		url += "&clientId=client_id";

		response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		System.out.println("response is ----" + response.getBody());
		url = "http://localhost:8080/user/getEmployeesList";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());
		String token = node.path("access_token").asText();

		url += "?access_token=" + token;
		System.out.println("url---" + url);

		String employees = restTemplate.getForObject(url, String.class);
		System.out.println(employees);
		return null;

	}

}
