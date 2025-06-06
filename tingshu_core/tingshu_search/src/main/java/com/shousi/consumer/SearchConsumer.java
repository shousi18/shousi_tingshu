package com.shousi.consumer;

import com.shousi.constant.KafkaConstant;
import com.shousi.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchConsumer {

    @Autowired
    private SearchService searchService;

    @KafkaListener(topics = KafkaConstant.ONSALE_ALBUM_QUEUE)
    public void onSaleAlbum(Long albumId) {
        if (albumId != null) {
            searchService.onSaleAlbum(albumId);
        }
    }

    //专辑下架
    @KafkaListener(topics = KafkaConstant.OFFSALE_ALBUM_QUEUE)
    public void offSaleAlbum(Long albumId) {
        if (albumId != null) {
            searchService.offSaleAlbum(albumId);
        }
    }

}
