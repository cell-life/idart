UPDATE patient 
SET 
   patientid = patientid||'-duplicate'
WHERE 
   id in (SELECT id
		  FROM patient a
		  WHERE a.id = (SELECT max(id) FROM patient
				WHERE patientid = a.patientid	
				GROUP BY patientid
				HAVING count(*) >1)) ;
