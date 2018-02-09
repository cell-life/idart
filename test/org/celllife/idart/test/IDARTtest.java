package org.celllife.idart.test;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.celllife.idart.commonobjects.iDartProperties;
import org.testng.annotations.BeforeSuite;

public class IDARTtest {

	public static final Logger log = Logger.getRootLogger();

	@BeforeSuite
	public void initialiseIDARTSystem() throws Exception {
		DOMConfigurator.configure("log4j.xml");
		iDartProperties.setiDartProperties();
		log.info("iDART system initialised");
	}

	public IDARTtest() {
		super();
	}

}