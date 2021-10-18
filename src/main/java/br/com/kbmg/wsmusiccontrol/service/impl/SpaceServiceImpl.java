package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.event.producer.SpaceRequestProducer;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SpaceRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SpaceServiceImpl
        extends GenericServiceImpl<Space, SpaceRepository>
        implements SpaceService {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SpaceRequestProducer spaceRequestProducer;

    @Override
    public Space findOrCreatePublicSpace() {
        return this.repository.findByName(KeyMessageConstants.PUBLIC_SPACE).orElseGet(() -> {
            Space publicSpace = new Space();
            publicSpace.setName(KeyMessageConstants.PUBLIC_SPACE);
            publicSpace.setJustification("Default");

            return repository.save(publicSpace);
        });
    }

    @Override
    public void requestNewSpaceForUser(SpaceRequestDto spaceRequestDto, HttpServletRequest request) {
        this.repository.findByName(spaceRequestDto.getName())
                .ifPresent(space -> {
                    throw new ServiceException(String.format
                            (messagesService.get("space.already.exist"), space.getName()));
                });
        Space space = new Space();

        UserApp userLogged = userAppService.findUserLogged();

        space.setName(spaceRequestDto.getName());
        space.setJustification(spaceRequestDto.getJustification());
        space.setRequestedBy(userLogged);

        repository.save(space);

        spaceRequestProducer.publishEvent(request, space);
    }

    @Override
    public Space findByIdValidated(Long spaceId) {
        return repository.findById(spaceId)
                .orElseThrow(() -> new ServiceException(
                        messagesService.get("space.not.exist")));
    }
}
