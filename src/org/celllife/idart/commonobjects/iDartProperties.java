package org.celllife.idart.commonobjects;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.PropertiesEncrypter;
import org.celllife.idart.misc.iDARTRuntimeException;

/**
 */
public class iDartProperties {

	public static final String SWTBOT_KEY = "org.eclipse.swtbot.widget.key";

	public static final String FILE = "idart.properties";

	public final static String OFFLINE_DOWNREFERRAL_MODE = "offline";

	public final static String ONLINE_DOWNREFERRAL_MODE = "online";

	public final static String randbDataFolder = "randb";

	public static boolean allowMultipleUncollectedPackages = false;

	public static String downReferralMode = "offline";

	public static boolean showDownReferButton = false;

	public static String roundUpForms = "";

	public static String hibernateConnectionUrl = "jdbc:postgresql://localhost:5432/test";

	public static String hibernatePassword = "postgres";

	public static String hibernateUsername = "postgres";

	public static String hibernateDriver = "org.postgresql.Driver";
	
	public static String hibernateDialect = "org.hibernate.dialect.PostgreSQLDialect";

	public static String updateScriptDir = "scripts";

	public static String exportDir = "dataExports";

	public static boolean patientNameOnDrugLabel = false;

	public static boolean patientNameOnReports = false;

	public static boolean patientNameOnPackageLabel = false;

	public static boolean summaryLabelDefault = true;

	public static boolean dispenseDirectlyDefault = true;

	public static boolean printSideTreatmentLabels = true;

	public static boolean printDrugLabels = true;

	public static String timesPerDayLanguage1 = "times per day";

	public static String timesPerDayLanguage2 = "ngemini";

	public static String timesPerDayLanguage3 = "keur per dag";

	public static boolean isEkapaVersion = true;

	public static String eKapa_user = "user";

	public static String eKapa_password = "pass";

	public static String eKapa_dburl = "oracle.cell-life.org";

	public static String eKapa_dbport = "1521";

	public static String eKapa_dbtype = "oracle";

	public static String eKapa_dbname = "idart";
	
	public static boolean prehmisIntegration = false;

    public static boolean idartWebEnabled = false;

    public static String idartWebUrl = "http://www.cell-life.org/idart";

    public static String idartWebSystemId = "99999999";

    public static String idartWebApplicationKey = "E8246BF1-B058-440D-A3E4-783F1B983722";
    
    public static boolean proxyBypass = true;
    
    public static String proxyUrl = "";
    
    public static int proxyPort = 0;
    
    public static String proxyUser = "";
    
    public static String proxyPassword = "";
    
    public static String proxyUserDomain = "";

	public static String patientBarcodeRegex = "\\w+";

	public static int intValueOfAlternativeBarcodeEndChar = -1;

	public static boolean accumByDefault = true;

	public static int logoutTime = -1;

	public static String idartVersionNumber = "@IDART.VERSION.SHORT@";

	public static boolean nextAppointmentDateOnLabels = true;

	public static boolean showBatchInfoOnSummaryLabels = false;
	public static boolean showBatchInfoOnDrugLabels = false;
	
	public static String country = "South Africa";

	public static Locale currentLocale = new Locale("en", "ZA");

	public static String importDateFormat = "dd/MM/yyyy";
	
	public static boolean enableDrugGroupEditor = true;
	
	public static boolean enableDrugEditor = true;
	
	public static boolean isCidaStudy = false;
	
	public static String illegalPatientIdChars = "'`^";
	public static String illegalPatientIdRegex = "["+illegalPatientIdChars+"]+";
	
	/**
	 */
	public enum LabelType {
		EKAPA, IDART
	}

	public static LabelType labelType = LabelType.IDART;

	private static Logger log = null;

	private static Properties loadedProperties;

	public static String updateUrl = "http://update.cell-life.org/idart/updates.xml";

	/**
	 * private constructor to prevent instantiation
	 */
	private iDartProperties() {
	}

	public static void setiDartProperties() {
		log = Logger.getLogger(iDartProperties.class);
		log.info("Loading Encrypted System Properties");

		PropertiesEncrypter pe = new PropertiesEncrypter();
		try {
			pe.loadPropertiesFromFile(FILE);
		} catch (IOException e) {
			throw new iDARTRuntimeException("Failed to load properties");
		}
		pe.decryptProperties();
		loadedProperties = pe.getProperties();

		setLocale("localeLanguage", "localeCountry");

		allowMultipleUncollectedPackages = setBooleanProperty("allowMultipleUncollectedPackages");
		downReferralMode = setStringProperty("downReferralMode");
		showDownReferButton = setBooleanProperty("showDownReferButton");
		summaryLabelDefault = setBooleanProperty("summaryLabelDefault");
		dispenseDirectlyDefault = setBooleanProperty("dispenseDirectlyDefault");
		printSideTreatmentLabels = setBooleanProperty("printSideTreatmentLabels");
		printDrugLabels = setBooleanProperty("printDrugLabels");
		patientNameOnDrugLabel = setBooleanProperty("patientNameOnDrugLabel");
		patientNameOnReports = setBooleanProperty("patientNameOnReports");
		patientNameOnPackageLabel = setBooleanProperty("showPatientNameOnPackageLabel");
		isEkapaVersion = setBooleanProperty("isEkapaVersion");
		accumByDefault = setBooleanProperty("accumByDefault");
		country = setStringProperty("country");
		timesPerDayLanguage1 = setStringProperty("timesPerDayLanguage1");
		timesPerDayLanguage2 = setStringProperty("timesPerDayLanguage2");
		timesPerDayLanguage3 = setStringProperty("timesPerDayLanguage3");
		logoutTime = setIntegerProperty("logoutTime");
		roundUpForms = setStringProperty("roundUpForms");

		nextAppointmentDateOnLabels = setBooleanProperty("nextAppointmentDateOnLabels");
		showBatchInfoOnSummaryLabels = setBooleanProperty("showBatchInfoOnSummaryLabels");
		showBatchInfoOnDrugLabels = setBooleanProperty("showBatchInfoOnDrugLabels");
		enableDrugEditor = setBooleanProperty("enableDrugEditor");
		enableDrugGroupEditor = setBooleanProperty("enableDrugGroupEditor");

		patientBarcodeRegex = setStringProperty("patientBarcodeRegex");

		exportDir = setStringProperty("export_dir");

		prehmisIntegration = setBooleanProperty("prehmis_integration");
        idartWebEnabled = setBooleanProperty("idart_web_enabled");
        idartWebUrl = setStringProperty("idart_web_url");
        idartWebApplicationKey = setStringProperty("encrypted_idart_web_application_key");
        idartWebSystemId = setStringProperty("idart_web_system_id");
        
        proxyUrl = setStringProperty("proxy_url");
        proxyPort = setIntegerProperty("proxy_port");
        proxyUser = setStringProperty("proxy_user");
        proxyPassword = setStringProperty("encrypted_proxy_password");
        proxyUserDomain = setStringProperty("proxy_user_domain");
        proxyBypass = setBooleanProperty("proxy_bypass");

		hibernateConnectionUrl = setStringProperty("encrypted_hibernate_url");
		hibernatePassword = setStringProperty("encrypted_hibernate_password");
		hibernateUsername = setStringProperty("encrypted_hibernate_username");
		updateScriptDir = setStringProperty("update_script_dir");
		importDateFormat = setStringProperty("importDateFormat");
		updateUrl = setStringProperty("updateUrl");

		eKapa_dbname = setStringProperty("eKapa_dbname");
		eKapa_dbtype = setStringProperty("eKapa_dbtype");
		eKapa_dbport = setStringProperty("eKapa_dbport");
		eKapa_dburl = setStringProperty("eKapa_dburl");
		eKapa_password = setStringProperty("eKapa_password");
		eKapa_user = setStringProperty("eKapa_user");

		intValueOfAlternativeBarcodeEndChar = setIntegerProperty("intValueOfAlternativeBarcodeEndChar");
		
		isCidaStudy = setBooleanProperty("cidaStudy");

		String labelTypeString = setStringProperty("labelType");
		// Should paeds labels have quantities blank by default?
		if (labelTypeString != null) {
			if (labelTypeString.equalsIgnoreCase("idart")) {
				labelType = LabelType.IDART;
			} else if (labelTypeString.equalsIgnoreCase("ekapa")) {
				labelType = LabelType.EKAPA;
			}
		}

		PatientBarcodeParser
		.initialisePatientBarcodeParser(patientBarcodeRegex);
	}

	public static String getPropertiesString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		// general/locale
		sb.append("country="+country);
		sb.append("\n");
		sb.append("localeCountry="+currentLocale.getCountry());
		sb.append("\n");
		sb.append("localeLanguage="+currentLocale.getLanguage());
		sb.append("\n");
		
		// application settings
		sb.append("logoutTime="+logoutTime);
		sb.append("\n");
		sb.append("accumByDefault="+accumByDefault);
		sb.append("\n");
		sb.append("allowMultipleUncollectedPackages=" + allowMultipleUncollectedPackages);
		sb.append("\n");
		sb.append("dispenseDirectlyDefault=" + dispenseDirectlyDefault);
		sb.append("\n");
		sb.append("downReferralMode=" + downReferralMode);
		sb.append("\n");
		sb.append("enableDrugEditor="+enableDrugEditor);
		sb.append("\n");
		sb.append("enableDrugGroupEditor="+enableDrugGroupEditor);
		sb.append("\n");
		sb.append("cidaStudy=" + isCidaStudy);
		sb.append("\n");
		sb.append("showDownReferButton=" + showDownReferButton);
		sb.append("\n");
		sb.append("roundUpForms="+roundUpForms);
		sb.append("\n");
		sb.append("patientNameOnReports="+patientNameOnReports);
		sb.append("\n");
		
		// barcodes
		sb.append("patientBarcodeRegex="+patientBarcodeRegex);
		sb.append("\n");
		sb.append("intValueOfAlternativeBarcodeEndChar=" + intValueOfAlternativeBarcodeEndChar);
		sb.append("\n");
		
		// importing/exporting
		sb.append("importDateFormat="+importDateFormat);
		sb.append("\n");
		sb.append("export_dir="+exportDir);
		sb.append("\n");
		// database update scripts
		sb.append("update_script_dir="+updateScriptDir);
		sb.append("\n");
		// application update url
		sb.append("updateUrl="+updateUrl);
		sb.append("\n");
		
		// label settings
		sb.append("showPatientNameOnPackageLabel=" + patientNameOnPackageLabel);
		sb.append("\n");
		sb.append("showBatchInfoOnSummaryLabels=" + showBatchInfoOnSummaryLabels);
		sb.append("\n");
		sb.append("showBatchInfoOnDrugLabels=" + showBatchInfoOnDrugLabels);
		sb.append("\n");
		sb.append("printSideTreatmentLabels=" + printSideTreatmentLabels);
		sb.append("\n");
		sb.append("printDrugLabels="+printDrugLabels);
		sb.append("\n");
		sb.append("patientNameOnDrugLabel="+patientNameOnDrugLabel);
		sb.append("\n");
		sb.append("nextAppointmentDateOnLabels="+nextAppointmentDateOnLabels);
		sb.append("\n");
		switch (labelType) {
		case EKAPA:
			sb.append("labelType=eKAPA");
			sb.append("\n");
			break;
		case IDART:
			sb.append("labelType=iDART");
			sb.append("\n");
			break;
		}
		sb.append("summaryLabelDefault=" + summaryLabelDefault);
		sb.append("\n");
		sb.append("timesPerDayLanguage1="+timesPerDayLanguage1);
		sb.append("\n");
		sb.append("timesPerDayLanguage2="+timesPerDayLanguage2);
		sb.append("\n");
		sb.append("timesPerDayLanguage3="+timesPerDayLanguage3);
		sb.append("\n");
		
		// ekapa
		sb.append("isEkapaVersion="+isEkapaVersion);
		sb.append("\n");
		sb.append("eKapa_dbname=" + eKapa_dbname);
		sb.append("\n");
		sb.append("eKapa_dbport=" + eKapa_dbport);
		sb.append("\n");
		sb.append("eKapa_dbtype=" + eKapa_dbtype);
		sb.append("\n");
		sb.append("eKapa_dburl=" + eKapa_dburl);
		sb.append("\n");
		sb.append("eKapa_password=" + eKapa_password);
		sb.append("\n");
		sb.append("eKapa_user=" + eKapa_user);
		sb.append("\n");
		
		// hibernate
		sb.append("encrypted_hibernate_password="+hibernatePassword);
		sb.append("\n");
		sb.append("encrypted_hibernate_url="+hibernateConnectionUrl);
		sb.append("\n");
		sb.append("encrypted_hibernate_username="+hibernateUsername);
		sb.append("\n");
		sb.append("hibernate_dialect="+hibernateDialect);
		sb.append("\n");
		sb.append("hibernate_driver="+hibernateDriver);
		sb.append("\n");
		
		// prehmis + idartweb
		sb.append("prehmis_integration="+prehmisIntegration);
		sb.append("\n");
		sb.append("idart_web_enabled="+idartWebEnabled);
		sb.append("\n");
		sb.append("idart_web_url="+idartWebUrl);
		sb.append("\n");
		sb.append("encrypted_idart_web_application_key="+idartWebApplicationKey);
		sb.append("\n");
		sb.append("idart_web_system_id="+idartWebSystemId);		
		sb.append("\n");
		
		// proxy
		sb.append("proxy_url="+proxyUrl);		
		sb.append("\n");
		sb.append("proxy_port="+proxyPort);		
		sb.append("\n");
		sb.append("proxy_user="+proxyUser);		
		sb.append("\n");
		sb.append("encrypted_proxy_password="+proxyPassword);		
		sb.append("\n");
		sb.append("proxy_user_domain="+proxyUserDomain);		
		sb.append("\n");
		sb.append("proxy_bypass="+proxyBypass);		
		sb.append("\n");
		
		return sb.toString();
	}

	/**
	 * Saves the current iDartProperties to the properties file (in case of changes during running the application)
	 * @throws IOException
	 */
	public static void saveiDartProperties() throws IOException {
		log.info("Writing updated properties to file.");
		PropertiesEncrypter pe = new PropertiesEncrypter();
		pe.loadPropertiesFromString(iDartProperties.getPropertiesString());
		pe.encryptProperties();
		pe.savePropertiesToFile(iDartProperties.FILE);
	}

	/**
	 * Method setIntegerProperty.
	 * 
	 * @param propertyName
	 *            String
	 * @return int
	 */
	private static int setIntegerProperty(String propertyName) {
		String theSetting = loadedProperties.getProperty(propertyName);
		if (theSetting != null) {
			try {
				return Integer.parseInt(theSetting);
			} catch (NumberFormatException pe) {
				log.info("System property " + propertyName
						+ " could not be parsed into an integer.");
			}
		}
		return -1;
	}

	/**
	 * Method setBooleanProperty.
	 * 
	 * @param propertyName
	 *            String
	 * @return boolean
	 */
	private static boolean setBooleanProperty(String propertyName) {
		String theSetting = loadedProperties.getProperty(propertyName);
		if (theSetting != null) {
			if (theSetting.equalsIgnoreCase("true"))
				return true;
		}
		return false;
	}

	/**
	 * Method setStringProperty.
	 * 
	 * @param propertyName
	 *            String
	 * @return String
	 */
	private static String setStringProperty(String propertyName) {
		String theSetting = loadedProperties.getProperty(propertyName);
		if (theSetting != null)
			return theSetting.trim();
		else {
			log.warn("Property: " + propertyName + " is null");
			return "";
		}
	}

	/**
	 * Set the current locale from the country and language codes
	 * 
	 * @param propertyNameLanguage
	 * @param propertyNameCountry
	 */
	private static void setLocale(String propertyNameLanguage,
			String propertyNameCountry) {
		String theLanguage = loadedProperties.getProperty(propertyNameLanguage);
		if (theLanguage == null) {
			log.warn("Property: " + propertyNameLanguage + " is null");
			return;
		}

		String theCountry = loadedProperties.getProperty(propertyNameCountry);
		if (theCountry == null) {
			log.warn("Property: " + propertyNameCountry + " is null");
			return;
		}

		try {
			currentLocale = new Locale(theLanguage, theCountry);
			log.info("Locale changed to " + currentLocale.getDisplayName());
		} catch (Exception e) {
			log.warn("Could not set locale", e);
		}

	}

	/**
	 * Generates a string of name value pairs for the fields in this class.
	 * 
	 * @return String listing all the values of the properties in this class.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static String printProperties() throws IllegalArgumentException,
	IllegalAccessException, ClassNotFoundException {
		Field[] fields = iDartProperties.class.getDeclaredFields();
		StringBuffer props = new StringBuffer();
		for (int i = 0; i < fields.length; i++) {
			props.append(fields[i].getName());
			props.append(" : '");
			Object value = fields[i].get(Class.forName(iDartProperties.class
					.getName()));
			String val = "";
			if (value != null) {
				val = value.getClass().cast(value).toString();
			}
			props.append(val);
			props.append("'\n");
		}
		return props.toString();
	}

}
