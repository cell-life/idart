package org.celllife.idart.gui.welcome;

import java.text.MessageFormat;
import java.util.List;

import model.manager.AlertManager;
import model.manager.AppointmentReminderManager;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.gui.generalAdmin.GeneralAdmin;
import org.celllife.idart.gui.patientAdmin.PatientAdmin;
import org.celllife.idart.gui.reports.NewReports;
import org.celllife.idart.gui.stockControl.StockControl;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.Screens;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

/**
 */
public class PharmacyWelcome extends GenericWelcome {

	public PharmacyWelcome() {
		super();
	}
	
	@Override
	public void showAlerts() {
		if (iDartProperties.appointmentReminders) {
			boolean alerts = AlertManager.hasCurrentAlerts(Alerts.ALERT_TYPE_ARS_REMINDER);
			if (alerts) {
				MessageBox alert = new MessageBox(shell, SWT.ERROR | SWT.ICON_ERROR);
				alert.setText(Messages.getString("appointmentreminders.title"));
				alert.setMessage(Messages.getString("appointmentreminders.alerts.popup"));
				alert.open();
			}
		}
	}

	@Override
	protected String getWelcomeLabelText() {
		return Messages.getString("pharmacywelcome.screen.instructions"); //$NON-NLS-1$
	}

	@Override
	protected void createCompOptions(Composite compOptions) {
		
		if (iDartProperties.appointmentReminders) {
			overrideBtnLogLocation(compOptions, new Rectangle(360, 140, 50, 43) ,new Rectangle(320, 190, 130, 40));
			// ARS button
			Label lblAppointmentReminder = new Label(compOptions, SWT.NONE);
			lblAppointmentReminder.setBounds(new Rectangle(200, 140, 50, 43));
			lblAppointmentReminder.setImage(ResourceUtils.getImage(iDartImage.APPOINTMENTREMINDERS));
			lblAppointmentReminder.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent mu) {
					sendAppointmentReminders();
				}
			});
	
			Button btnAppointmentReminder = new Button(compOptions, SWT.NONE);
			btnAppointmentReminder.setBounds(new Rectangle(160, 190, 130, 40));
			btnAppointmentReminder.setText(Messages.getString("pharmacywelcome.button.appointmentreminder.text"));
			btnAppointmentReminder.setToolTipText(Messages.getString("pharmacywelcome.button.appointmentreminder.tooltip")); 
			btnAppointmentReminder.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			btnAppointmentReminder.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					sendAppointmentReminders();
				}
			});
		}
		
		// generalAdmin
		Label lblPicGeneralAdmin = new Label(compOptions, SWT.NONE);
		lblPicGeneralAdmin.setBounds(new Rectangle(40, 0, 50, 43));
		lblPicGeneralAdmin.setImage(ResourceUtils
				.getImage(iDartImage.GENERALADMIN));
		lblPicGeneralAdmin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new GeneralAdmin(shell);
			}
		});

		Button btnGeneralAdmin = new Button(compOptions, SWT.NONE);
		btnGeneralAdmin.setData(iDartProperties.SWTBOT_KEY, Screens.GENERAL_ADMIN.getAccessButtonId());
		btnGeneralAdmin.setBounds(new Rectangle(0, 50, 130, 40));
		btnGeneralAdmin.setText(Messages.getString("pharmacywelcome.button.generaladmin.text")); //$NON-NLS-1$
		btnGeneralAdmin
		.setToolTipText(Messages.getString("pharmacywelcome.button.generaladmin.tooltip")); //$NON-NLS-1$
		btnGeneralAdmin.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnGeneralAdmin
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				new GeneralAdmin(shell);
			}
		});

		// patientAdmin
		Label lblPicPatientAdmin = new Label(compOptions, SWT.NONE);
		lblPicPatientAdmin.setBounds(new Rectangle(200, 0, 50, 43));
		lblPicPatientAdmin.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTADMIN));
		lblPicPatientAdmin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new PatientAdmin(shell);
			}
		});

		Button btnPatientAdmin = new Button(compOptions, SWT.NONE);
		btnPatientAdmin.setData(iDartProperties.SWTBOT_KEY, Screens.PATIENT_ADMIN.getAccessButtonId());
		btnPatientAdmin.setBounds(new Rectangle(160, 50, 130, 40));
		btnPatientAdmin.setText(Messages.getString("pharmacywelcome.button.patientadmin.text")); //$NON-NLS-1$
		btnPatientAdmin
		.setToolTipText(Messages.getString("pharmacywelcome.button.patientadmin.tooltip")); //$NON-NLS-1$
		btnPatientAdmin.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPatientAdmin
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				new PatientAdmin(shell);
			}
		});

		// stockControl
		Label lblPicStockControl = new Label(compOptions, SWT.NONE);
		lblPicStockControl.setBounds(new Rectangle(360, 0, 50, 43));
		lblPicStockControl.setImage(ResourceUtils
				.getImage(iDartImage.STOCKCONTROL));
		lblPicStockControl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new StockControl();
			}
		});

		Button btnStockControl = new Button(compOptions, SWT.NONE);
		btnStockControl.setData(iDartProperties.SWTBOT_KEY, Screens.STOCK_CONTROL.getAccessButtonId());
		btnStockControl.setBounds(new Rectangle(320, 50, 130, 40));
		btnStockControl.setText(Messages.getString("pharmacywelcome.button.stockdispensing.text")); //$NON-NLS-1$
		btnStockControl
		.setToolTipText(Messages.getString("pharmacywelcome.button.stockdispensing.tooltip")); //$NON-NLS-1$
		btnStockControl.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnStockControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new StockControl();
			}
		});

		// reports
		Label lblPicReports = new Label(compOptions, SWT.NONE);
		lblPicReports.setBounds(new Rectangle(520, 0, 50, 43));
		lblPicReports.setImage(ResourceUtils.getImage(iDartImage.REPORTS));

		lblPicReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new NewReports(shell);
			}
		});

		Button btnReports = new Button(compOptions, SWT.NONE);
		btnReports.setData(iDartProperties.SWTBOT_KEY, Screens.REPORTS.getAccessButtonId());
		btnReports.setText(Messages.getString("welcome.button.reports.text")); //$NON-NLS-1$
		btnReports.setBounds(new Rectangle(480, 50, 130, 40));
		btnReports.setToolTipText(Messages
				.getString("welcome.button.reports.tooltip")); //$NON-NLS-1$
		btnReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnReports
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				new NewReports(shell);
			}
		});
	}
	
	private void sendAppointmentReminders() {
		MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setText(Messages.getString("pharmacywelcome.button.appointmentreminder.popup.title"));
		mb.setMessage(Messages.getString("pharmacywelcome.button.appointmentreminder.popup"));
		switch (mb.open()) {
		case SWT.YES:
			// Process missed appointments
			List<MessageSchedule> messages = AppointmentReminderManager.createAndSendMissedAppointmentMessages(true);
			List<Alerts> alerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_MISSED);

			if (alerts == null || alerts.size() == 0) {
				// on success
				MessageBox m1 = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
				m1.setText(Messages.getString("pharmacywelcome.button.appointmentreminder.popup.title"));
				m1.setMessage(MessageFormat.format(Messages.getString("pharmacywelcome.button.appointmentreminder.success"),messages.size()));
				m1.open();
			} else {
				// on error
				MessageBox m2 = new MessageBox(shell, SWT.ERROR | SWT.ICON_ERROR);
				m2.setText(Messages.getString("pharmacywelcome.button.appointmentreminder.popup.title"));
				m2.setMessage(MessageFormat.format(Messages.getString("pharmacywelcome.button.appointmentreminder.error"), messages.size(), alerts.size()));
				m2.open();				
			}
			
			break;
		case SWT.NO:
			break;
		}
	}
}
