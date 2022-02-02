package br.com.kbmg.wsmusiccontrol.enums;

import java.time.LocalDate;

public enum RangeDateFilterEnum {
    LAST_THIRTY_DAYS {
        @Override
        public LocalDate getStartOfRangeDateEvent() {
            return LocalDate.now().minusMonths(1);
        }
    },
    LAST_THREE_MONTHS {
        @Override
        public LocalDate getStartOfRangeDateEvent() {
            return LocalDate.now().minusMonths(3);
        }
    },
    LAST_SIX_MONTHS {
        @Override
        public LocalDate getStartOfRangeDateEvent() {
            return LocalDate.now().minusMonths(6);
        }
    },
    LAST_ONE_YEAR {
        @Override
        public LocalDate getStartOfRangeDateEvent() {
            return LocalDate.now().minusMonths(12);
        }
    };

    public abstract LocalDate getStartOfRangeDateEvent();

}
