package cn.yang.e3mall.search.mapper;

import cn.yang.e3mall.common.pojo.SearchItem;

import java.util.List;

public interface ItemMapper {
    //批量获取
    List<SearchItem> getItemList();

    //单个获取
    SearchItem getItemById(long itemId);


}
