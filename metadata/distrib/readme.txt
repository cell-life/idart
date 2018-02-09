===================
iDART $idartVersion
===================

iDART is a software solution designed to support the dispensing of ARV drugs in the public health care sector. It supports pharmacists in their important role of dispensing accurately to an increasing number of patients whilst still being able to engage and assist the patient.

Requiremets
===========

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
=========

iDART 3.7.8
===========

 * Changed the installation wizard so it is now possible to select the Country and Locale (these result in changes in idart.properties)
 * Added the Nigerian country code prefix (234) to sms.properties
 * Added a msisdn regex for Nigeria that allows numbers in the following format only 234xxxxxxxxxx where the first x cannot be a 0. See: http://en.wikipedia.org/wiki/Telephone_numbers_in_Nigeria
 * Linked the mobile number validation to the country code, so that if you select ZA in the installation wizard it matches against 27 and if you select NG, it uses 234
 * Added the FGHIN clinics.

.. This file uses reStructuredText markup (http://docutils.sourceforge.net/rst.html).