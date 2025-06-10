package com.shousi.service;

import java.util.List;
import java.util.Map;

public interface SearchService {

    /**
     * 上架专辑
     * @param albumId
     */
    void onSaleAlbum(Long albumId);

    void offSaleAlbum(Long albumId);

    List<Map<String, Object>> getChannelData(Long category1Id);
}
