package com.shousi;

import com.shousi.entity.BaseCategory3;
import com.shousi.entity.BaseCategoryView;
import com.shousi.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "tingshu-album")
public interface CategoryFeignClient {
    @GetMapping("/api/album/category/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id);

    @GetMapping("/api/album/category/getCategory3ListByCategory1Id/{category1Id}")
    public RetVal<List<BaseCategory3>> getCategory3ListByCategory1Id(@PathVariable Long category1Id);

    @GetMapping("/api/album/category/getCategory3ListByCategory1Id/{category1Id}")
    public RetVal<List<BaseCategory3>> getCategory3TopByCategory1Id(@PathVariable Long category1Id);
}
