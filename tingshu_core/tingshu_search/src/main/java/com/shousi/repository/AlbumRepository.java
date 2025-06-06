package com.shousi.repository;

import com.shousi.entity.AlbumInfoIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AlbumRepository extends ElasticsearchRepository<AlbumInfoIndex, Long> {
}
