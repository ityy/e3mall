package cn.yang.e3mall.item.pojo;

import cn.yang.e3mall.pojo.TbItem;

import java.util.Date;

/**
 * 包装了TbItem，添加了getImages方法
 */
public class Item extends TbItem {
    //构造方法 将TbItem的属性传给Item
    public Item(TbItem tbItem) {
        this.setId(tbItem.getId());
        this.setTitle(tbItem.getTitle());
        this.setSellPoint(tbItem.getSellPoint());
        this.setPrice(tbItem.getPrice());
        this.setNum(tbItem.getNum());
        this.setBarcode(tbItem.getBarcode());
        this.setImage(tbItem.getImage());
        this.setCid(tbItem.getCid());
        this.setStatus(tbItem.getStatus());
        this.setCreated(tbItem.getCreated());
        this.setUpdated(tbItem.getUpdated());
    }


    public String[] getImages() {
        String image2 = this.getImage();
        if (image2 != null && "".equals(image2) == false) {
            String[] strings = image2.split(",");
            return strings;
        } else {
            return null;
        }
    }
}
