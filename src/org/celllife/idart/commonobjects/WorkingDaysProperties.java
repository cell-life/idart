package org.celllife.idart.commonobjects;

import java.util.List;

import com.pholser.util.properties.BoundProperty;
import com.pholser.util.properties.DefaultsTo;
import com.pholser.util.properties.ValuesSeparatedBy;

public interface WorkingDaysProperties {

    @DefaultsTo("true")
    @BoundProperty("workingMonday")
    public boolean workingMonday();

    @DefaultsTo("true")
    @BoundProperty("workingTuesday")
    public boolean workingTuesday();

    @DefaultsTo("true")
    @BoundProperty("workingWednesday")
    public boolean workingWednesday();

    @DefaultsTo("true")
    @BoundProperty("workingThursday")
    public boolean workingThursday();

    @DefaultsTo("true")
    @BoundProperty("workingFriday")
    public boolean workingFriday();

    @DefaultsTo("false")
    @BoundProperty("workingSaturday")
    public boolean workingSaturday();

    @DefaultsTo("false")
    @BoundProperty("workingSunday")
    public boolean workingSunday();

    @DefaultsTo("true")
    @BoundProperty("easterFridayHoliday")
    public boolean easterFridayHoliday();

    @DefaultsTo("true")
    @BoundProperty("easterMondayHoliday")
    public boolean easterMondayHoliday();

    @BoundProperty("holidays")
    @ValuesSeparatedBy(pattern = "\\s*,\\s*")
    public List<String> holidays();

    @DefaultsTo("false")
    @BoundProperty("sundayMondayRule")
    public boolean sundayMondayRule();

}
