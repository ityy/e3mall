package cn.yang.e3mall.mapper;

import cn.yang.e3mall.pojo.TbContentCopy;
import cn.yang.e3mall.pojo.TbContentCopyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbContentCopyMapper {
    int countByExample(TbContentCopyExample example);

    int deleteByExample(TbContentCopyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbContentCopy record);

    int insertSelective(TbContentCopy record);

    List<TbContentCopy> selectByExampleWithBLOBs(TbContentCopyExample example);

    List<TbContentCopy> selectByExample(TbContentCopyExample example);

    TbContentCopy selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbContentCopy record, @Param("example") TbContentCopyExample example);

    int updateByExampleWithBLOBs(@Param("record") TbContentCopy record, @Param("example") TbContentCopyExample example);

    int updateByExample(@Param("record") TbContentCopy record, @Param("example") TbContentCopyExample example);

    int updateByPrimaryKeySelective(TbContentCopy record);

    int updateByPrimaryKeyWithBLOBs(TbContentCopy record);

    int updateByPrimaryKey(TbContentCopy record);
}