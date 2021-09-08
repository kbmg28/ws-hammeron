package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SecuredAdminOrUser;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music")
@CrossOrigin(origins = "*")
@SecuredAdminOrUser
public class MusicController extends GenericController {

    @Autowired
    private MusicService musicService;


}
