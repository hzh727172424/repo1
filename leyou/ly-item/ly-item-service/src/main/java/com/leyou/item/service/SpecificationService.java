package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;
    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPECGROUP_NOT_FOUND);
        }
        return list;
    }
    public List<SpecParam> querySpecParamByGid(Long gid, Long cid, Boolean searching) {
      SpecParam specParam =new SpecParam();
      specParam.setGroupId(gid);
      specParam.setCid(cid);
      specParam.setSearching(searching);
        List<SpecParam> list =specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPECGROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecGroup> querySpecListByCid(Long cid) {
        List<SpecGroup> specGroups = querySpecGroupByCid(cid);
        List<SpecParam> specParams = querySpecParamByGid(null, cid, null);
        Map<Long,List<SpecParam>>  map =new HashMap<>();
        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())){
                map.put(param.getGroupId(),new ArrayList<>() );
            }
            map.get(param.getGroupId()).add(param);
        }
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
//       specGroups.forEach(specGroup -> specGroup.setParams(map.get(specGroup.getId())));
        return specGroups;
    }
}
