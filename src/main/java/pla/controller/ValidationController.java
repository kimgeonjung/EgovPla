package pla.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pla.dto.validation.CanopyValidationDto;
import pla.dto.validation.KioskValidationDto;
import pla.dto.validation.LibraryValidationDto;
import pla.dto.validation.PublicWifiValidationDto;

@RestController
@RequestMapping("/api/validation")
@Validated
public class ValidationController {
	// 도서관 데이터 검증
    @PostMapping("/library")
    public ResponseEntity<Map<String, Object>> validateLibrary(@Valid @RequestBody LibraryValidationDto request) {
    	Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "도서관 입력값이 유효합니다.");
    	return ResponseEntity.ok(response);
    }

    // 무인발급기 데이터 검증
    @PostMapping("/kiosk")
    public ResponseEntity<Map<String, Object>> validateKiosk(@Valid @RequestBody KioskValidationDto request) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "무인발급기 입력값이 유효합니다.");
    	return ResponseEntity.ok(response);
    }

    // 공공와이파이 데이터 검증
    @PostMapping("/public_wifi")
    public ResponseEntity<Map<String, Object>> validateWiFi(@Valid @RequestBody PublicWifiValidationDto request) {
    	Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "공공 와이파이 입력값이 유효합니다.");
    	return ResponseEntity.ok(response);
    }

    // 무더위 그늘막 데이터 검증
    @PostMapping("/canopy")
    public ResponseEntity<Map<String, Object>> validateCanopy(@Valid @RequestBody CanopyValidationDto request) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "무더위 그늘막 입력값이 유효합니다.");
        return ResponseEntity.ok(response);
    }
}
