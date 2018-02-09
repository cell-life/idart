package org.celllife.idart.gui.patient;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.manager.AlertManager;
import model.manager.AppointmentReminderManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.integration.mobilisr.MobilisrManager;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.utils.iDARTUtil;
import org.celllife.mobilisr.api.validation.MsisdnValidator;
import org.celllife.mobilisr.api.validation.MsisdnValidator.ValidationError;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AppointmentReminderDialog extends GenericOthersGui {

	private final AppointmentReminder appointmentReminder;
	// cellphone number, language, message time
	private Text txtCellphone;
	private CCombo cmbLanguage;
	private CCombo cmbCustomMsgTime;
	// subscribe to appointment reminders
	private Button btnYes;
	
	private Label lblLastMessageSent;
	
	private Label lblRedGreenDot;
	
	// indicates that the appointment reminder details should be saved when the window is disposed of
	private boolean saveWhenDone = true;

	public AppointmentReminderDialog(Shell parent, Session session, AppointmentReminder appointmentReminder) {
		this(parent, session, appointmentReminder, true);
	}

	public AppointmentReminderDialog(Shell parent, Session session, AppointmentReminder appointmentReminder, boolean saveWhenDone) {
		super(parent, session);
		this.appointmentReminder = appointmentReminder;
		this.saveWhenDone = saveWhenDone;
	}

	@Override
	protected void createCompButtons() {
		//Composite myCmp = new Composite(getShell(), SWT.NONE);

		RowLayout rowlyt = new RowLayout();
		rowlyt.justify = true;
		rowlyt.pack = false;
		rowlyt.spacing = 10;
		getCompButtons().setLayout(rowlyt);
		
		Button btnSave = new Button(getCompButtons(), SWT.NONE);
		btnSave.setText(Messages.getString("genericformgui.button.save.text"));
		btnSave.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSave.setToolTipText(Messages.getString("genericformgui.button.save.tooltip"));
		btnSave.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdSaveWidgetSelected();
			}
		});

		Button btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setText(Messages.getString("genericformgui.button.cancel.text"));
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setToolTipText(Messages.getString("genericformgui.button.cancel.tooltip"));
		btnCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});
		
		RowData rowD = new RowData(170, 30);
		Control[] buttons = getCompButtons().getChildren();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setLayoutData(rowD);
		}
		getCompButtons().pack();
		Rectangle b = getShell().getBounds();
		getCompButtons().setBounds(0, 0, b.width - 179, b.height - 100);
	}

	protected void cmdCancelWidgetSelected() {
		closeShell(false);
	}

	protected void cmdSaveWidgetSelected() {
		if (fieldsOk()) {
			doSave();
		}
	}

	private boolean fieldsOk() {
		boolean result = true;
		
		if (!txtCellphone.getText().trim().isEmpty() && MobilisrManager.validateMsisdn(txtCellphone.getText().trim()) != null){
			String title = Messages.getString("patient.error.invalidfield.title");
			String message = EMPTY;
			ValidationError error = MobilisrManager.validateMsisdn(txtCellphone.getText().trim());
			if (MsisdnValidator.Code.COUNTRY_CODE.equals(error.code)){
				message = MessageFormat.format(Messages.getString("patient.error.incorrectCellphoneCode"), 
						PropertiesManager.sms().msisdnPrefix());
			} else {
				message = MessageFormat.format(Messages.getString("patient.error.incorrectCellphone"), 
						error.message);
			}
			showMessage(MessageDialog.ERROR, title, message);
			txtCellphone.setFocus();
			result = false;
			
		} 
		if (btnYes.getSelection()) {
			if (cmbLanguage.getSelectionIndex() == -1 || cmbLanguage.getSelectionIndex() == 0) {
				showMessage(MessageDialog.ERROR, 
						Messages.getString("patient.error.missingfield.title"),
						Messages.getString("appointmentreminders.language.title"));
				cmbLanguage.setFocus();
				result = false;
			} else if (cmbCustomMsgTime.getSelectionIndex() == -1) {
				showMessage(MessageDialog.ERROR, 
						Messages.getString("patient.error.missingfield.title"),
						Messages.getString("appointmentreminders.messagetime.title"));
				cmbCustomMsgTime.setFocus();
				result = false;
			} else if (txtCellphone.getText().trim().isEmpty()) {
				showMessage(MessageDialog.ERROR, 
						Messages.getString("patient.error.missingfield.title"),
						Messages.getString("appointmentreminders.cellphone.title"));
				txtCellphone.setFocus();
				result = false;
			}
		}

		return result;
	}

	private void doSave() {
		// update appointmentreminder entity object
		appointmentReminder.setModified(true);
		appointmentReminder.setLanguage(cmbLanguage.getText().trim());
		if (btnYes.getSelection()) {
			appointmentReminder.setSubscribed(true);
		} else {
			appointmentReminder.setSubscribed(false);
		}
		appointmentReminder.setMessageTime(cmbCustomMsgTime.getText().trim());
		appointmentReminder.getPatient().setCellphone(txtCellphone.getText().trim());

		if (!saveWhenDone) {
			// abort - someone else wishes to save the appointment reminder
			closeShell(false);
			return;
		}
		// save now
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();

			PatientManager.saveAppointmentReminder(getHSession(), appointmentReminder);

			getHSession().flush();
			tx.commit();

			MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
			m.setText(Messages.getString("appointmentreminders.title"));
			String message = EMPTY;
			if (btnYes.getSelection()) {
				message = MessageFormat.format(Messages.getString("appointmentreminders.subscribe.success"), appointmentReminder.getPatient().getPatientId());
			} else {
				message = MessageFormat.format(Messages.getString("appointmentreminders.unsubscribe.success"), appointmentReminder.getPatient().getPatientId());
			}
			m.setMessage(message);
			m.open();

			closeShell(false);
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}

			getLog().error("Error saving patient appointment reminder details to the database.", he);
			MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
			m.setText(Messages.getString("common.error"));
			m.setMessage(MessageFormat.format(Messages.getString("appointmentreminders.error"),appointmentReminder.getPatient().getPatientId()));
			m.open();
		}
	}

	@Override
	protected void createCompHeader() {
		String headerTxt = Messages.getString("appointmentreminders.title");
		iDartImage icoImage = iDartImage.APPOINTMENTREMINDERS;
		buildCompHeader(headerTxt, icoImage);
		
		boolean alerts = AlertManager.hasCurrentAlerts(Alerts.ALERT_TYPE_ARS_REMINDER, getHSession());
		lblRedGreenDot = new Label(getCompHeader(), SWT.NONE);
		try {
			if (alerts) {
				lblRedGreenDot.setImage(ResourceUtils.getImage(iDartImage.REDDOT));
			} else {
				lblRedGreenDot.setImage(ResourceUtils.getImage(iDartImage.GREENDOT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lblRedGreenDot.setBounds(new Rectangle(475, 10, 25, 25));
	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void createShell() {
		String shellTxt = Messages.getString("appointmentreminders.title");
		Rectangle bounds = new Rectangle(25, 0, 600, 365);
		buildShell(shellTxt, bounds);
		createContents();
	}

	private void createContents() {
		Composite compContents = new Composite(getShell(), SWT.NONE);

		GridLayout gl = new GridLayout(2, true);
		gl.horizontalSpacing = 15;
		gl.verticalSpacing = 10;
		gl.marginLeft = 95;
		compContents.setLayout(gl);
		
		// Subscribe yes/no radio
		Label subscribeLabel = new Label(compContents, SWT.CENTER);
		subscribeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		subscribeLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		subscribeLabel.setText(Messages.getString("appointmentreminders.subscribe"));

		Composite compRadio = new Composite(compContents, SWT.NONE);
		compRadio.setLayout(new RowLayout());
		compRadio.setLayoutData(new GridData(150, 25));
		btnYes = new Button(compRadio, SWT.RADIO);
		btnYes.setText(Messages.getString("common.yes"));
		Button btnNo = new Button(compRadio, SWT.RADIO);
		btnNo.setText(Messages.getString("common.no"));
		if (appointmentReminder.isSubscribed()) {
			btnYes.setSelection(true);
		} else {
			btnNo.setSelection(true);
		}
		
		// Phone Cell
		Label lblPhoneCell = new Label(compContents, SWT.CENTER);
		lblPhoneCell.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		lblPhoneCell.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPhoneCell.setText(Messages.getString("appointmentreminders.cellphone"));
		
		txtCellphone = new Text(compContents, SWT.BORDER);
		txtCellphone.setLayoutData(new GridData(150, 15));
		txtCellphone.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtCellphone.setText(appointmentReminder.getPatient().getCellphone());
		
		// Languages
		Label lblLanguage = new Label(compContents, SWT.CENTER);
		lblLanguage.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		lblLanguage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblLanguage.setText(Messages.getString("patient.label.language"));
		
		cmbLanguage = new CCombo(compContents, SWT.BORDER);
		cmbLanguage.setLayoutData(new GridData(150, 20));
		populateLanguages();
		if (appointmentReminder.getLanguage() != null) {
			cmbLanguage.setText(appointmentReminder.getLanguage());
		}
		
		// Custom message
		Label lblCustomMsgTime = new Label(compContents, SWT.CENTER);
		lblCustomMsgTime.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		lblCustomMsgTime.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCustomMsgTime.setText(Messages.getString("campaigns.label.customMsgTime"));
		
		cmbCustomMsgTime = new CCombo(compContents, SWT.BORDER);
		cmbCustomMsgTime.setLayoutData(new GridData(150, 20));
		populateMsgTimes();
		if (appointmentReminder.getMessageTime() == null || appointmentReminder.getMessageTime().trim().isEmpty()) {
			cmbCustomMsgTime.setText(PropertiesManager.sms().defaultCustomMsgTime());
		} else {
			cmbCustomMsgTime.setText(appointmentReminder.getMessageTime());
		}
		
		// Status Label
		if (appointmentReminder.getPatient().getId() != -1) { // don't display message on new patient screen
			
			lblLastMessageSent = new Label(compContents, SWT.CENTER);
			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.horizontalSpan = 2;
			lblLastMessageSent.setLayoutData(gd);
			lblLastMessageSent.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			
			// Check what the message text should say
			List<MessageSchedule> scheduledMessages = AppointmentReminderManager.getMessagesSuccessfullyScheduled(getHSession(), appointmentReminder.getPatient());
			List<MessageSchedule> notScheduledMessages = AppointmentReminderManager.getMessagesNotYetScheduled(getHSession(), appointmentReminder.getPatient());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (scheduledMessages.isEmpty()) { // there have been successes and potentially some errors
				if (notScheduledMessages.isEmpty()) { 
					// there have been no successes or errors
					lblLastMessageSent.setVisible(false);
				} else { 
					// there have only been errors
					lblLastMessageSent.setText(MessageFormat.format(Messages.getString("appointmentreminders.lastmessagealertonly.text"), notScheduledMessages.size()));
				}
			} else {
				if (notScheduledMessages.isEmpty()) {
					// there have only been successes
					lblLastMessageSent.setText(MessageFormat.format(Messages.getString("appointmentreminders.lastmessagesent.text"), 
							sdf.format(scheduledMessages.get(0).getScheduleDate()), scheduledMessages.size()));
				} else {
					// there have been successes and errors
					lblLastMessageSent.setText(MessageFormat.format(Messages.getString("appointmentreminders.lastmessagesentalert.text"), 
							sdf.format(scheduledMessages.get(0).getScheduleDate()), scheduledMessages.size(), notScheduledMessages.size()));
				}
			}
		}
		
		Rectangle b = getShell().getBounds();
		compContents.setBounds(0, 100, b.width, 200);
	}
	
	private void populateLanguages() {
		List<String> languages = PropertiesManager.sms().languages();
		cmbLanguage.clearSelection();
		cmbLanguage.removeAll();
		if (languages != null && !languages.isEmpty()) {
			cmbLanguage.add(Messages.getString("addtostudy.please-select"), 0);
			for (String lang : languages) {
				cmbLanguage.add(lang);
			}
		}
	}
	
	private void populateMsgTimes(){
		cmbCustomMsgTime.clearSelection();
		cmbCustomMsgTime.removeAll();
		Calendar cal = Calendar.getInstance();
		cal.setTime(iDARTUtil.zeroTimeStamp(new Date()));
		cal.add(Calendar.MINUTE, 60);
		SimpleDateFormat timeFormat = new SimpleDateFormat(AppointmentReminder.MESSAGE_TIME_FORMAT);
		for (int i = 0; i < 44; i++) {
			cal.add(Calendar.MINUTE, 30);
			cmbCustomMsgTime.add(timeFormat.format(cal.getTime()));
		}
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	public void openAndWait() {
		activate();
		while (!getShell().isDisposed()) {
			if (!getShell().getDisplay().readAndDispatch()) {
				getShell().getDisplay().sleep();
			}
		}
	}
}
