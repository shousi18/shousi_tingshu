package com.shousi;

import com.shousi.entity.AlbumAttributeValue;
import com.shousi.entity.AlbumInfo;
import com.shousi.fallback.AlbumFallBack;
import com.shousi.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "tingshu-album", fallback = AlbumFallBack.class)
public interface AlbumFeignClient {

    @GetMapping("/api/album/albumInfo/getAlbumInfoById/{albumId}")
    public RetVal<AlbumInfo> getAlbumInfoById(@PathVariable Long albumId);
    @GetMapping("/api/album/albumInfo/getAlbumPropertyValue/{albumId}")
    public List<AlbumAttributeValue> getAlbumPropertyValue(@PathVariable Long albumId);
}
