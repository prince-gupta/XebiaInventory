package com.xebia.services.impl;

import com.xebia.dao.AssetApprovalDAO;
import com.xebia.dto.AssetStatisticsDTO;
import com.xebia.enums.ApprovalStateEnum;
import com.xebia.services.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements IStatisticsService{

    @Autowired
    AssetApprovalDAO assetApprovalDAO;

    public List<AssetStatisticsDTO> getAssetStatistics(){
        List countList = assetApprovalDAO.getApprovalStateCounts();
        Map<String, AssetStatisticsDTO> assetStatisticsDTOMap = new HashMap<>();
        List<AssetStatisticsDTO> assetStatisticsDTOs = new ArrayList<>();
        for(ApprovalStateEnum approvalStateEnum : ApprovalStateEnum.values()){
            AssetStatisticsDTO assetStatisticsDTO = new AssetStatisticsDTO();
            assetStatisticsDTO.setName(approvalStateEnum.getDisplayName());
            assetStatisticsDTO.setY(0);
            assetStatisticsDTOMap.put(approvalStateEnum.getDbName(),assetStatisticsDTO);
        }
        for(Object object : countList){
            Object[] objects = (Object[]) object;
            AssetStatisticsDTO assetStatisticsDTO = assetStatisticsDTOMap.remove(objects[0]);
            assetStatisticsDTO.setY((long)objects[1]);
            assetStatisticsDTOs.add(assetStatisticsDTO);
        }

        assetStatisticsDTOs.addAll(assetStatisticsDTOMap
                                    .keySet()
                                    .stream()
                                    .map(assetStatisticsDTOMap::get)
                                    .collect(Collectors.toList()));
        return assetStatisticsDTOs;
    }
}
