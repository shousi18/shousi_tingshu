package com.shousi.service;

import com.shousi.query.AlbumIndexQuery;
import com.shousi.vo.AlbumSearchResponseVo;

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

    /**
     * 专辑搜索
     * @param albumIndexQuery
     * @return
     */
    AlbumSearchResponseVo search(AlbumIndexQuery albumIndexQuery);
}
