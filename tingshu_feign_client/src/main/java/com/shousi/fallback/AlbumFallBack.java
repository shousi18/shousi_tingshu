package com.shousi.fallback;

import com.shousi.AlbumFeignClient;
import com.shousi.entity.AlbumAttributeValue;
import com.shousi.entity.AlbumInfo;
import com.shousi.result.RetVal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AlbumFallBack implements AlbumFeignClient {
    @Override
    public RetVal<AlbumInfo> getAlbumInfoById(Long id) {
        log.error("调用远程服务失败");
        return RetVal.fail();
    }

    @Override
    public List<AlbumAttributeValue> getAlbumPropertyValue(Long albumId) {
        log.error("调用远程服务失败");
        return null;
    }
}
