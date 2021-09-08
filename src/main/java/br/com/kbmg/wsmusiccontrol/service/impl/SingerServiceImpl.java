package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.repository.SingerRepository;
import br.com.kbmg.wsmusiccontrol.service.SingerService;
import org.springframework.stereotype.Service;

@Service
public class SingerServiceImpl extends GenericServiceImpl<Singer, SingerRepository> implements SingerService {

}
