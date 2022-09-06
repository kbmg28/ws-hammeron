package br.com.kbmg.wshammeron.enums;

import java.time.OffsetDateTime;

public enum RangeDateFilterEnum {
    LAST_2_HOURS {
        @Override
        public OffsetDateTime getStartOfRangeDateEvent() {
            return OffsetDateTime.now().minusHours(2);
        }
    },
    LAST_THIRTY_DAYS {
        @Override
        public OffsetDateTime getStartOfRangeDateEvent() {
            return getOffsetDateTime(1);
        }
    },
    LAST_THREE_MONTHS {
        @Override
        public OffsetDateTime getStartOfRangeDateEvent() {
            return getOffsetDateTime(3);
        }
    },
    LAST_SIX_MONTHS {
        @Override
        public OffsetDateTime getStartOfRangeDateEvent() {
            return getOffsetDateTime(6);
        }
    },
    LAST_ONE_YEAR {
        @Override
        public OffsetDateTime getStartOfRangeDateEvent() {
            return getOffsetDateTime(12);
        }
    };

    private static OffsetDateTime getOffsetDateTime(int months) {
        return OffsetDateTime.now().minusMonths(months).minusHours(2);
    }

    public abstract OffsetDateTime getStartOfRangeDateEvent();

}
