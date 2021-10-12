package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SpaceUserAppAssociationRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpaceUserAppAssociationServiceImpl
        extends GenericServiceImpl<SpaceUserAppAssociation, SpaceUserAppAssociationRepository>
        implements SpaceUserAppAssociationService {

    @Autowired
    private SpaceService spaceService;

    @Override
    public void createAssociationWithPublicSpace(UserApp userApp) {
        Space space = spaceService.findOrCreatePublicSpace();

        SpaceUserAppAssociation spaceUserAppAssociation = new SpaceUserAppAssociation();
        spaceUserAppAssociation.setUserApp(userApp);
        spaceUserAppAssociation.setSpace(space);

        repository.save(spaceUserAppAssociation);
    }
}
