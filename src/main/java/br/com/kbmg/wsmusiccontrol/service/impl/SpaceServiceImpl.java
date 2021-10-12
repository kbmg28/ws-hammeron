package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.repository.SpaceRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import org.springframework.stereotype.Service;

@Service
public class SpaceServiceImpl
        extends GenericServiceImpl<Space, SpaceRepository>
        implements SpaceService {

    @Override
    public Space findOrCreatePublicSpace() {
//        this.repository.findByName(KeyMessageConstants.PUBLIC_SPACE).orElse(() -> {
//            // TODO: IMPLEMENT
//
//        });
        return null;
    }
}
