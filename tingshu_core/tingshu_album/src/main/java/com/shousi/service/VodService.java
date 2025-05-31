package com.shousi.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface VodService {
    Map<String, Object> uploadTrack(MultipartFile file);
}
