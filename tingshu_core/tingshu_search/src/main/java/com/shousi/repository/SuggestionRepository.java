package com.shousi.repository;

import com.shousi.entity.AlbumInfoIndex;
import com.shousi.entity.SuggestIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SuggestionRepository extends ElasticsearchRepository<SuggestIndex, Long> {
}
