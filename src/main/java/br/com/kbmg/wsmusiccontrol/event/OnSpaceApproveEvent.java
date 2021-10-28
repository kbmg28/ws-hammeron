package br.com.kbmg.wsmusiccontrol.event;

import br.com.kbmg.wsmusiccontrol.model.Space;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OnSpaceApproveEvent extends AbstractEvent<Space> {

    public OnSpaceApproveEvent(Space space, Locale locale, String appUrl) {
        super(space, locale, appUrl);
    }
}