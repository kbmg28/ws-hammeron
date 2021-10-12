package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.model.Space;

public interface SpaceService extends GenericService<Space>{
    Space findOrCreatePublicSpace();
}
