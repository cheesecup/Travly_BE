package com.travelland.valid.trip;

import jakarta.validation.GroupSequence;

@GroupSequence({
        TripValidationGroups.TitleBlankGroup.class,
        TripValidationGroups.ContentBlankGroup.class,
        TripValidationGroups.CostRangeGroup.class,
        TripValidationGroups.AddressBlankGroup.class})
public interface TripValidationSequence {
}
