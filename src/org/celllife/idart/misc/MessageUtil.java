package org.celllife.idart.misc;

import java.lang.reflect.InvocationTargetException;

import org.celllife.idart.commonobjects.iDartProperties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class MessageUtil {

	public static void showError(Throwable e, final String title, String message) {
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getTargetException();
		}
		IStatus status = null;
		if (message == null) {
			message = e.getMessage();
		}
		if (message == null) {
			message = e.toString();
		}
		status = new Status(IStatus.ERROR, "iDART", IStatus.OK, message, e);
		new iDARTErrorDialog(null, title, null, status, IStatus.ERROR).open();
	}

	public static void showError(Throwable e) {
		showError(e, null, null);
	}

	public static String getCrashMessage() {
		String message = "An error has occurred in iDART that requires it to restart.\n\n"
			+ "If this same error happens regularly in iDART, please contact the iDART Help Desk. To see the technical reasons for this crash, "
			+ "please click on the 'Details' button.\n\n"
			+ "Version infromation:"
			+ "\niDART version: "
				+ iDartProperties.idartVersionNumber;
		return message;
	}

	public static String getIDARTWebCrashMessage(Exception e) {
		String statusMessage;
		if (e.getCause() != null) {
			statusMessage = processErrorMessage(e.getCause().getMessage());
		} else {
			statusMessage = processErrorMessage(e.getMessage());
		}
		String message = "An error has occurred while connecting to iDARTweb. \n\n"
				+ statusMessage
				+ "If this error persists, please contact the Support Desk. " 
				+ "To see the technical reasons for this crash, please click on the 'Details' button.\n\n"
				+ "Version infromation:"
				+ "\niDART version: "
					+ iDartProperties.idartVersionNumber;
			return message;
	}
	
	private static String processErrorMessage(String errorMessage) {
		String statusMessage = "";
		if (errorMessage != null && errorMessage.length() >= 3) {
			System.out.println("HELLO="+errorMessage.trim().substring(0, 3));
			try {
				Integer statusCode = Integer.parseInt(errorMessage.trim().substring(0, 3));
				if (statusCode == 407) {
					statusMessage = "Please check your proxy login details. \n\n";
				} else if (statusCode == 502) {
					statusMessage = "Your proxy has denied you access to iDARTweb. \n\n";
				} else if (statusCode == 404 || statusCode == 503 || statusCode == 504) {
					statusMessage = "The iDARTweb server is currently unavailable. Please try again later. \n\n";
				}
			} catch (NumberFormatException nfe) {
				// ignore this error
			}
		}
		return statusMessage;
	}

}
