package com.khabri.exchangerate.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khabri.exchangerate.rest.impl.ExchangeRateServiceImpl;

@RestController
public class ExchangeRateService {

	@RequestMapping(value = "/getAverageRates/{from_date}/{to_date}/{curr}")
	public String getAverageRates(@PathVariable("from_date") String from_date, @PathVariable("to_date") String to_date,
			@PathVariable("curr") String curr) {
		return new ExchangeRateServiceImpl().getAverageRates(from_date, to_date, curr);
	}

	@RequestMapping(value = "/home")
	public String home() {
		try {

			URL url = new URL("https://api.exchangeratesapi.io/2010-01-31");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine = "";
			String result = "";
			while (inputLine != null) {
				inputLine = in.readLine();
				result += (inputLine == null ? "" : inputLine);
			}

			in.close();
			con.disconnect();
			return result;
		} catch (Exception e) {
			return "error fetching home due to " + e.getMessage();
		}
	}

}
