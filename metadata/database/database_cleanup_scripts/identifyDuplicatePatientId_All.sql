-- This script identifies all patientId's that occur more than once in the patient table

select * from 

(select patientId, count(patientId) 

from patient 
group by patientId) as c

where c.count > 1
