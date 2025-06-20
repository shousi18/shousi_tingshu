package com.shousi.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import com.alibaba.fastjson.JSONObject;
import com.shousi.AlbumFeignClient;
import com.shousi.CategoryFeignClient;
import com.shousi.UserFeignClient;
import com.shousi.entity.*;
import com.shousi.query.AlbumIndexQuery;
import com.shousi.repository.AlbumRepository;
import com.shousi.repository.SuggestionRepository;
import com.shousi.result.RetVal;
import com.shousi.service.SearchService;
import com.shousi.util.PinYinUtils;
import com.shousi.vo.AlbumInfoIndexVo;
import com.shousi.vo.AlbumSearchResponseVo;
import com.shousi.vo.UserInfoVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Qualifier("com.shousi.AlbumFeignClient")
    @Autowired
    private AlbumFeignClient albumFeignClient;

    @Autowired
    private CategoryFeignClient categoryFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public void onSaleAlbum(Long albumId) {
        // 获取专辑信息
        AlbumInfo albumInfo = albumFeignClient.getAlbumInfoById(albumId).getData();
        // 获取专辑属性值
        List<AlbumAttributeValue> albumPropertyValue = albumFeignClient.getAlbumPropertyValue(albumId);
        // 获取专辑分类
        BaseCategoryView baseCategoryView = categoryFeignClient.getCategoryView(albumInfo.getCategory3Id());
        // 获取专辑用户
        UserInfoVo userInfoVo = userFeignClient.getUserById(albumInfo.getUserId()).getData();

        // 要进行一系列判空...
        AlbumInfoIndex albumInfoIndex = new AlbumInfoIndex();
        BeanUtils.copyProperties(albumInfo, albumInfoIndex);
        List<AttributeValueIndex> attributeValueIndexList = albumPropertyValue.stream().map(albumAttributeValue -> {
            AttributeValueIndex albumAttributeValueIndex = new AttributeValueIndex();
            BeanUtils.copyProperties(albumAttributeValue, albumAttributeValueIndex);
            return albumAttributeValueIndex;
        }).collect(Collectors.toList());
        albumInfoIndex.setAttributeValueIndexList(attributeValueIndexList);
        albumInfoIndex.setCategory1Id(baseCategoryView.getCategory1Id());
        albumInfoIndex.setCategory2Id(baseCategoryView.getCategory2Id());
        albumInfoIndex.setAnnouncerName(userInfoVo.getNickname());
        albumInfoIndex.setHotScore(Math.random());

        //更新统计量与得分，默认随机，方便测试
        int num1 = new Random().nextInt(1000);
        int num2 = new Random().nextInt(100);
        int num3 = new Random().nextInt(50);
        int num4 = new Random().nextInt(300);
        albumInfoIndex.setPlayStatNum(num1);
        albumInfoIndex.setSubscribeStatNum(num2);
        albumInfoIndex.setBuyStatNum(num3);
        albumInfoIndex.setCommentStatNum(num4);
        //计算公式：未实现
        double hotScore = num1 * 0.2 + num2 * 0.3 + num3 * 0.4 + num4 * 0.1;
        albumInfoIndex.setHotScore(hotScore);

        // 保存到elasticSearch中
        albumRepository.save(albumInfoIndex);

        // 标题自动补全
        SuggestIndex titleSuggestIndex = new SuggestIndex();
        titleSuggestIndex.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        titleSuggestIndex.setTitle(albumInfo.getAlbumTitle());
        titleSuggestIndex.setKeyword(new Completion(new String[]{albumInfo.getAlbumTitle()}));
        titleSuggestIndex.setKeywordPinyin(new Completion(new String[]{PinYinUtils.toHanyuPinyin(albumInfo.getAlbumTitle())}));
        titleSuggestIndex.setKeywordSequence(new Completion(new String[]{PinYinUtils.getFirstLetter(albumInfo.getAlbumTitle())}));
        suggestionRepository.save(titleSuggestIndex);
        // 专辑主播名称自动补全
        if (StringUtils.hasText(albumInfoIndex.getAnnouncerName())) {
            SuggestIndex announcerNameSuggestIndex = new SuggestIndex();
            announcerNameSuggestIndex.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            announcerNameSuggestIndex.setTitle(albumInfoIndex.getAnnouncerName());
            announcerNameSuggestIndex.setKeyword(new Completion(new String[]{albumInfoIndex.getAnnouncerName()}));
            announcerNameSuggestIndex.setKeywordPinyin(new Completion(new String[]{PinYinUtils.toHanyuPinyin(albumInfoIndex.getAnnouncerName())}));
            announcerNameSuggestIndex.setKeywordSequence(new Completion(new String[]{PinYinUtils.getFirstLetter(albumInfoIndex.getAnnouncerName())}));
            suggestionRepository.save(announcerNameSuggestIndex);
        }
    }

    @Override
    public void offSaleAlbum(Long albumId) {
        albumRepository.deleteById(albumId);
    }

    @SneakyThrows
    @Override
    public List<Map<String, Object>> getChannelData(Long category1Id) {
        List<BaseCategory3> baseCategory3List = categoryFeignClient.getCategory3TopByCategory1Id(category1Id).getData();
        Map<Long, BaseCategory3> baseCategory3Map = baseCategory3List.stream()
                .collect(Collectors.toMap(BaseEntity::getId, baseCategory3 -> baseCategory3));
        List<FieldValue> fieldValueList = baseCategory3List.stream()
                .map(baseCategory3 -> FieldValue.of(baseCategory3.getId()))
                .collect(Collectors.toList());
        // 搜索es语句
        SearchResponse<AlbumInfoIndex> response = elasticsearchClient.search(searchRequest -> searchRequest
                        .index("albuminfo")
                        .query(q -> q.terms(
                                t -> t.field("category3Id")
                                        .terms(new TermsQueryField.Builder().value(fieldValueList).build())
                        ))
                        .aggregations(
                                "category3IdAgg", a -> a.terms(t -> t.field("category3Id").size(10))
                                        .aggregations(
                                                "topSixHotScoreAgg", aa -> aa
                                                        .topHits(at -> at
                                                                .size(6)
                                                                .sort(xs -> xs.field(xf -> xf.field("hotScore").order(SortOrder.Desc))))
                                        )
                        )
                ,
                AlbumInfoIndex.class);
        return response.aggregations().get("category3IdAgg")
                .lterms()
                .buckets()
                .array()
                .stream()
                .map(bucket -> {
                    List<AlbumInfoIndex> topAlbumInfoIndexList = bucket.aggregations().get("topSixHotScoreAgg")
                            .topHits()
                            .hits()
                            .hits()
                            .stream()
                            .map(hit -> JSONObject.parseObject(hit.source().toString(), AlbumInfoIndex.class))
                            .collect(Collectors.toList());
                    Map<String, Object> retMap = new HashMap<>();
                    retMap.put("baseCategory3", baseCategory3Map.get(bucket.key()));
                    retMap.put("list", topAlbumInfoIndexList);
                    return retMap;
                }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public AlbumSearchResponseVo search(AlbumIndexQuery albumIndexQuery) {
        // 构建查询DSL
        SearchRequest request = buildQueryDSL(albumIndexQuery);
        // 执行搜索
        SearchResponse<AlbumInfoIndex> response = elasticsearchClient.search(request, AlbumInfoIndex.class);
        // 解析响应
        AlbumSearchResponseVo albumSearchResponseVo = parseSearchResult(response);
        albumSearchResponseVo.setPageNo(albumIndexQuery.getPageNo());
        albumSearchResponseVo.setPageSize(albumIndexQuery.getPageSize());
        albumSearchResponseVo.setTotalPages((albumSearchResponseVo.getTotal() + albumSearchResponseVo.getPageSize() - 1) / albumSearchResponseVo.getPageSize());
        return albumSearchResponseVo;
    }

    @Override
    @SneakyThrows
    public Set<String> autoCompleteSuggest(String keyword) {
        Suggester suggester = new Suggester.Builder()
                .suggesters("suggestionKeyword", s -> s
                        .prefix(keyword)
                        .completion(
                                c -> c.field("keyword")
                                        .size(10)
                                        .skipDuplicates(true)
                        )
                )
                .suggesters("suggestionKeywordPinyin", s -> s
                        .prefix(keyword)
                        .completion(
                                c -> c.field("keywordPinyin")
                                        .size(10)
                                        .skipDuplicates(true)
                        )
                )
                .suggesters("suggestionKeywordSequence", s -> s
                        .prefix(keyword)
                        .completion(
                                c -> c.field("keywordSequence")
                                        .size(10)
                                        .skipDuplicates(true)
                        )
                ).build();
        SearchResponse<SuggestIndex> response = elasticsearchClient.search(s -> s
                .index("suggestinfo")
                .suggest(suggester), SuggestIndex.class);
        // 2.解析索引里面的信息-不可重复
        Set<String> suggestTitleList = analysisResponse(response);
        return suggestTitleList;
    }

    private Set<String> analysisResponse(SearchResponse<SuggestIndex> response) {
        Set<String> stringHashSet = new HashSet<>();
        Map<String, List<Suggestion<SuggestIndex>>> suggestMap = response.suggest();
        suggestMap.entrySet().stream().forEach(entry -> {
            List<Suggestion<SuggestIndex>> suggestionValueList = entry.getValue();
            suggestionValueList.stream().forEach(suggestionValue -> {
                List<String> stringList = suggestionValue.completion().options().stream()
                        .map(option -> option.source().getTitle())
                        .collect(Collectors.toList());
                stringHashSet.addAll(stringList);
            });
        });
        return stringHashSet;
    }

    private AlbumSearchResponseVo parseSearchResult(SearchResponse<AlbumInfoIndex> response) {
        AlbumSearchResponseVo albumSearchResponseVo = new AlbumSearchResponseVo();
        List<AlbumInfoIndexVo> albumInfoIndexList = new ArrayList<>();
        albumSearchResponseVo.setTotal(response.hits().total().value());
        List<Hit<AlbumInfoIndex>> searchAlbumInfoHits = response.hits().hits();
        for (Hit<AlbumInfoIndex> searchAlbumInfoHit : searchAlbumInfoHits) {
            AlbumInfoIndexVo albumInfoIndexVo = new AlbumInfoIndexVo();
            BeanUtils.copyProperties(searchAlbumInfoHit.source(), albumInfoIndexVo);
            if (!CollectionUtils.isEmpty(searchAlbumInfoHit.highlight())) {
                albumInfoIndexVo.setAlbumTitle(searchAlbumInfoHit.highlight().get("albumTitle").get(0));
            }
            albumInfoIndexList.add(albumInfoIndexVo);
        }
        albumSearchResponseVo.setList(albumInfoIndexList);
        return albumSearchResponseVo;
    }

    private SearchRequest buildQueryDSL(AlbumIndexQuery albumIndexQuery) {
        //2.构建bool查询
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        //3.构建should关键字查询
        String keyword = albumIndexQuery.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            boolQuery.should(s -> s.match(m -> m.field("albumTitle").query(keyword)));
            boolQuery.should(s -> s.match(m -> m.field("albumIntro").query(keyword)));
            boolQuery.should(s -> s.match(m -> m.field("announcerName").query(keyword)));
        }
        //4.根据一级分类id查询
        Long category1Id = albumIndexQuery.getCategory1Id();
        if (null != category1Id) {
            boolQuery.filter(f -> f.term(t -> t.field("category1Id").value(category1Id)));
        }
        //4.根据二级分类id查询
        Long category2Id = albumIndexQuery.getCategory2Id();
        if (null != category2Id) {
            boolQuery.filter(f -> f.term(t -> t.field("category2Id").value(category2Id)));
        }
        //4.根据三级分类id查询
        Long category3Id = albumIndexQuery.getCategory3Id();
        if (null != category3Id) {
            boolQuery.filter(f -> f.term(t -> t.field("category3Id").value(category3Id)));
        }
        //5.根据分类属性嵌套过滤
        List<String> propertyList = albumIndexQuery.getAttributeList();
        if (!CollectionUtils.isEmpty(propertyList)) {
            for (String property : propertyList) {
                //property长的类似于这种格式-->15:32
                String[] propertySplit = StringUtils.split(property, ":");
                if (propertySplit != null && propertySplit.length == 2) {
                    Query nestedQuery = NestedQuery.of(n -> n
                            .path("attributeValueIndexList")
                            .query(q -> q.bool(b -> b
                                    .must(m -> m.term(t -> t.field("attributeValueIndexList.attributeId").value(propertySplit[0])))
                                    .must(m -> m.term(t -> t.field("attributeValueIndexList.valueId").value(propertySplit[1])))
                            )))._toQuery();
                    boolQuery.filter(nestedQuery);
                }
            }
        }
        //1.构建最外层的query
        Query query = boolQuery.build()._toQuery();
        //6.构建分页与高亮信息
        Integer pageNo = albumIndexQuery.getPageNo();
        Integer pageSize = albumIndexQuery.getPageSize();
        SearchRequest.Builder requestBuilder = new SearchRequest.Builder()
                .index("albuminfo")
                .from((pageNo - 1) * pageSize)
                .size(pageSize)
                .query(query)
                .highlight(param -> param.fields("albumTitle", filed -> filed
                        .preTags("<font color='red'>")
                        .postTags("</font>")))
                .source(param -> param.filter(filed -> filed.excludes("attributeValueIndexList", "hotScore")));
        //7.构建排序信息 order=1:asc
        String order = albumIndexQuery.getOrder();
        String orderField = "hotScore";
        String sortType = "desc";
        //如果传递了排序参数
        if (StringUtils.hasText(order)) {
            String[] orderSplit = StringUtils.split(order, ":");
            if (orderSplit != null && orderSplit.length == 2) {
                switch (orderSplit[0]) {
                    case "1":
                        orderField = "hotScore";
                        break;
                    case "2":
                        orderField = "playStatNum";
                        break;
                    case "3":
                        orderField = "createTime";
                        break;
                }
                sortType = orderSplit[1];
            }
        }
        if (StringUtils.hasText(keyword)) {
            String sortTypeParam = sortType;
            String sortField = orderField;
            requestBuilder.sort(s -> s.field(f -> f.field(sortField)
                    .order("asc".equals(sortTypeParam) ? SortOrder.Asc : SortOrder.Desc)));
        }
        SearchRequest request = requestBuilder.build();
        System.out.println("拼接的DSL语句:" + request.toString());
        return request;
    }
}
