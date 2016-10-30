package com.xebia.services;

import com.xebia.dto.AssetStatisticsDTO;

import java.util.List;

/**
 * Created by Pgupta on 11-10-2016.
 */
public interface IStatisticsService {

    public List<AssetStatisticsDTO> getAssetStatistics();
}
