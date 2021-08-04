package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
@CrossOrigin(origins = "*")
public class InfoController extends GenericController {

    @Autowired
    private BuildProperties buildProperties;

    @GetMapping
    public ResponseEntity<ResponseData<String>> getVersion() {
        return super.ok(buildProperties.getVersion());
    }
}
