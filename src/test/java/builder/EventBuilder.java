package builder;

import br.com.kbmg.wshammeron.model.Event;

import static constants.BaseTestsConstants.ANY_VALUE;

public abstract class EventBuilder {

    public static Event generateEvent() {

        Event event = new Event();

        event.setName(ANY_VALUE);

        return event;
    }

}
