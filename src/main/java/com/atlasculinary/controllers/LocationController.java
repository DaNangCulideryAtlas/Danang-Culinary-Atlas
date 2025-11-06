package com.atlasculinary.controllers;

import com.atlasculinary.dtos.ProvinceDto;
import com.atlasculinary.dtos.DistrictDto;
import com.atlasculinary.dtos.WardDto;
import com.atlasculinary.services.LocationService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    /**
     * GET /api/public/locations/provinces
     * Lấy danh sách tất cả các tỉnh/thành phố
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<ProvinceDto>> getAllProvinces() {
        List<ProvinceDto> provinces = locationService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    /**
     * GET /api/public/locations/provinces/{provinceId}
     * Lấy thông tin chi tiết của một tỉnh/thành phố theo ID
     */
    @GetMapping("/provinces/{provinceId}")
    public ResponseEntity<ProvinceDto> getProvinceById(@PathVariable int provinceId) {
        ProvinceDto province = locationService.getProvinceById(provinceId);
        return ResponseEntity.ok(province);
    }

    // --- 2. District (Quận/Huyện) ---

    /**
     * GET /api/public/locations/provinces/{provinceId}/districts
     * Lấy danh sách các quận/huyện thuộc một tỉnh/thành phố
     */
    @GetMapping("/provinces/{provinceId}/districts")
    public ResponseEntity<List<DistrictDto>> getDistrictsByProvince(@PathVariable int provinceId) {
        List<DistrictDto> districts = locationService.getDistrictsByProvince(provinceId);
        return ResponseEntity.ok(districts);
    }

    /**
     * GET /api/public/locations/districts/{districtId}
     * Lấy thông tin chi tiết của một quận/huyện theo ID
     */
    @GetMapping("/districts/{districtId}")
    public ResponseEntity<DistrictDto> getDistrictById(@PathVariable int districtId) {
        DistrictDto district = locationService.getDistrictById(districtId);
        return ResponseEntity.ok(district);
    }

    // --- 3. Ward (Phường/Xã) ---

    /**
     * GET /api/public/locations/districts/{districtId}/wards
     * Lấy danh sách các phường/xã thuộc một quận/huyện
     */
    @GetMapping("/districts/{districtId}/wards")
    public ResponseEntity<List<WardDto>> getWardsByDistrict(@PathVariable int districtId) {
        List<WardDto> wards = locationService.getWardsByDistrict(districtId);
        return ResponseEntity.ok(wards);
    }

    /**
     * GET /api/public/locations/wards/{wardId}
     * Lấy thông tin chi tiết của một phường/xã theo ID
     */
    @GetMapping("/wards/{wardId}")
    public ResponseEntity<WardDto> getWardById(@PathVariable int wardId) {
        WardDto ward = locationService.getWardById(wardId);
        return ResponseEntity.ok(ward);
    }
}