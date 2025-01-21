package pla.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pla.dto.validation.KioskValidationDto;
import pla.dto.validation.LibraryValitationDto;
import pla.dto.validation.PublicWifiValidationDto;

@RestController
@RequestMapping("/api/validation")
@Validated
public class ValidationController {
	// 도서관 데이터 검증
    @PostMapping("/library")
    public ResponseEntity<?> validateLibrary(@Valid @RequestBody LibraryValitationDto request) {
        return ResponseEntity.ok().body("도서관 입력값이 유효합니다.");
    }

    // 무인발급기 데이터 검증
    @PostMapping("/kiosk")
    public ResponseEntity<?> validateKiosk(@Valid @RequestBody KioskValidationDto request) {
        return ResponseEntity.ok().body("무인발급기 입력값이 유효합니다.");
    }

    // 공공와이파이 데이터 검증
    @PostMapping("/wifi")
    public ResponseEntity<?> validateWiFi(@Valid @RequestBody PublicWifiValidationDto request) {
        return ResponseEntity.ok().body("공공와이파이 입력값이 유효합니다.");
    }
}
