package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.repository.MusicRepository;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import org.springframework.stereotype.Service;

@Service
public class MusicServiceImpl extends GenericServiceImpl<Music, MusicRepository> implements MusicService {

}
