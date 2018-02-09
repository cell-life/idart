===================
iDART 3.9.0
===================

iDART is a software solution designed to support the dispensing of ARV drugs in the public health care sector. It supports pharmacists in their important role of dispensing accurately to an increasing number of patients whilst still being able to engage and assist the patient.

Requirements
============

* Java runtime environment.
* PostgreSQL database server
* 200 MB hard disk space
* 512 MB RAM

Installation and Upgrade
========================

Full instructions on installing and upgrading iDART can be found in the iDART support manual.

If you have any difficulties the best way to get help is to send an
email to the implementers mailing list, idart-implementers@lists.sourceforge.net.
	
If you are not already a memeber you can join the list by going 
to this url: `<http://lists.sourceforge.net/lists/listinfo/idart-implementers>`_

ChangeLog
---------

iDART 3.9.0
=============

This release adds functionality to communicate with iDARTweb, and iDARTweb provides an interface to PREHMIS.

* Added properties for iDARTweb (URL, system id, application key)
* Added proxy settings in the installer. If the proxy url is set, communication with iDARTweb will be via the specified proxy
* Added proxy settings on the login screen - only if proxy url is specified during the installer wizard, or in the idart.properties file
* Added option to bypass the proxy for iDARTweb communication on the login screen
* Note: Proxy uses NTLM authentication
* Added property for PREHMIS integration. This disables functionality to update patient information as PREHMIS is the patient record system
* Communication with iDARTweb involves:
 - retrieval of patient information
 - retrieval of list of doctors
 - create/delete prescription
 - create/delete/update packages (only once it has been given to the patient)


iDART 3.8.0
===========

This release contains the following changes:

* Added ATC codes and MIMS References to drugs and drug components (chemical compounds).
* Extended Editing of chemical compounds.
* Added Tier.net data export functionality

iDART 3.7.6
===========

This is a maintenance release of iDART 3.7.x with the following changes:

* ensure correct data type for package.weekssupply database column

iDART 3.7.5
===========

This is a maintenance release of iDART 3.7.x with the following changes:

* fix null pointer if patient search activated with null patientid
* fix null pointer when saving new drug group

iDART 3.7.4
===========

This is a maintenance release of iDART 3.7.x with the following changes:

* fix bug IDART-290: patient details not loading
* fix blank column in packages leaving report
* don't snap calendar dialog to nextAppointment button on Packaging screen to avoid loading patient history report when selecting date
* in idart.properties file split 'showBatchOnLabels' property into 'showBatchOnSummaryLabels' and 'showBatchOnDrugLabels'
* changes to label printing to fix duration issue: if a batch other than the default batch is chosen when dispensing then the drug labels and summary labels shows 1 of a week prescription even if the duration is 1 month.

iDART 3.7.3
===========

This is a maintenance release of iDART 3.7.x with the following changes:

* change barcode label printing code to use Barbecue barcode library to fix some eKapa barcode scanning problems

iDART 3.7.2
===========

This is a maintenance release of iDART 3.7.x with the following changes:

* update to Communicate 2.3 api
** Configurable MSISDN validation
* Update patient packaging to work better for multiple patients with same identifier
* adjust widgets in DestroyStock that were overlapping with the table

iDART 3.7.1
===========

This is a maintenance release of iDART 3.7.x that fixes the following issues:

* Changes to AlternateIdentifier table caused an error with the Patient History Report
* Non-unique national identifiers in test data caused error when creating a new database with test data

iDART 3.7.0
===========

This release contains the following new features:

* Re-branded with the new logo's
* Added multiple patient identifiers


Update Notes
------------

When upgrading to 3.7.x from an version prior to 3.7 you may encounter errors due to duplicate patient identifiers. To assit you in removing duplicates you can use the following query which will append '-duplicate' to any duplicate patient ID's or national patient identifiers.

	UPDATE patient
	SET
	   patientid = patientid||'-duplicate'
	WHERE
	   id in (SELECT id
			  FROM patient a
			  WHERE a.id = (SELECT max(id) FROM patient
					WHERE patientid = a.patientid
					GROUP BY patientid
					HAVING count(*) > 1)) ;

	UPDATE patient
	SET
	   idnum = idnum||'-duplicate'
	WHERE
	   id in (SELECT id
			  FROM patient a
			  WHERE a.id = (SELECT max(id) FROM patient
					WHERE idnum = a.idnum
					GROUP BY idnum
					HAVING count(*) > 1)) ;
