package pla.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@CrossOrigin(origins = "http://localhost:9991") // 자기가 쓰는 포트 번호
@Log4j2
public class MapController {

    @GetMapping("/map")
    public String map() {
        return "map/map";
    }
    
}
