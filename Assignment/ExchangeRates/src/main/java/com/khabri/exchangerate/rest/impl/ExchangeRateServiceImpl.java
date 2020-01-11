package com.khabri.exchangerate.rest.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ExchangeRateServiceImpl {

	Map<String, Double> map = new HashMap<String, Double>();
	// Assumption is difference between two dates will be Long's max value at max
	Long count = (long)0;

	public String getAverageRates(String from_date, String to_date, String currencies) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date minDate = formatter.parse("2000-01-01");
			Date date_from = formatter.parse(from_date);
			Date date_to = formatter.parse(to_date);
			if (date_from.before(minDate))
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String strDate = dateFormat.format(minDate);
				throw new RuntimeException("From Date " + from_date + " must be greater than or equal to " + strDate);
			}
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate now = LocalDate.now();
			Date maxDate = formatter.parse(dtf.format(now));
			if (date_to.after(maxDate))
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String strDate = dateFormat.format(maxDate);
				throw new RuntimeException("To Date " + to_date + " must be smaller than or equal to " + strDate);
			}
			if (date_from.after(date_to))
				throw new RuntimeException(
						"To Date " + to_date + " must be greater than or equal to From Date" + from_date);

			fetchCurrencyAverage(currencies.split(","), date_from, date_to);
			String result = "";
			for (Map.Entry<String, Double> en : map.entrySet()) {
				result += en.getKey() + ":" + (en.getValue() / count) + "\n";
			}
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private void fetchCurrencyAverage(String[] currencies, Date from, Date to) {
		try {
			Calendar start = Calendar.getInstance();
			start.setTime(from);
			Calendar end = Calendar.getInstance();
			end.setTime(to);

			for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
				count++;
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String strDate = dateFormat.format(date);
				URL url = new URL("https://api.exchangeratesapi.io/" + strDate);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String jsonStr = "";
				String inputLine = "";
				while (inputLine != null) {
					inputLine = in.readLine();

					jsonStr += (inputLine == null ? "" : inputLine);
				}
				con.disconnect();
				in.close();
				JSONParser parser = new JSONParser();
				JSONObject jsonObj = (JSONObject) ((JSONObject) parser.parse(jsonStr)).get("rates");
				for (String cur : currencies) {
					if (!jsonObj.containsKey(cur))
						throw new RuntimeException("currency " + cur + " is not present in the API.");
					if (map.containsKey(cur)) {
						map.put(cur, map.get(cur) + (Double) jsonObj.get(cur));
					} else {
						map.put(cur, (Double) jsonObj.get(cur));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
