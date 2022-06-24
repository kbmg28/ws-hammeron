package br.com.kbmg.wshammeron.repository.converter;

import br.com.kbmg.wshammeron.util.DateUtilUTC;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Converter
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(OffsetDateTime attribute) {
        return DateUtilUTC.toTimestamp(attribute);
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(Timestamp dbData) {
        return DateUtilUTC.toOffsetDateTime(dbData);
    }
}
