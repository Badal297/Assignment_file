package com.khabri.exchangerate.rest.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExchangeRateServiceImplTest {
    @Test(expected = Exception.class)
 	public void getAverageRates()
 	{
	 ExchangeRateServiceImpl er=new ExchangeRateServiceImpl();
	 String from_date="1999-10-10";
	 String minDate="2000-01-01";
	 String res= er.getAverageRates(from_date,"2010-10-10","CAD");
	 assertEquals(res,"From Date "+from_date+" must be greater than or equal to "+ minDate);
	 String date_to="2021-10-11";
	 String res1= er.getAverageRates("2001-10-10",date_to,"CAD");
	 assertEquals(res1,"To Date " + date_to + " must be smaller than or equal to " + "2020-01-11");
 	}

}
