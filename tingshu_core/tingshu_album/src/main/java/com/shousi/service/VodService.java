package com.shousi.service;

import com.shousi.entity.TrackInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface VodService {
    /**
     * 上传声音
     *
     * @param file
     * @return
     */
    Map<String, Object> uploadTrack(MultipartFile file);

    /**
     * 获取声音信息
     *
     * @param trackInfo
     */
    void getTrackMediaInfo(TrackInfo trackInfo);
}
