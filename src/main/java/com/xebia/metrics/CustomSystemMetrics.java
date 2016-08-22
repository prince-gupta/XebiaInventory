package com.xebia.metrics;

import com.xebia.services.monitor.MonitorService;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by Pgupta on 17-08-2016.
 */
@Service
public class CustomSystemMetrics implements PublicMetrics, Ordered {

    @Autowired
    MonitorService monitorService;


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        Collection<Metric<?>> result = new LinkedHashSet<>();

        try {
            monitorService.cpuUsage(result);
            monitorService.diskUsage(result);
            monitorService.ramUsage(result);
        } catch (SigarException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
