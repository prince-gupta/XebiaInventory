package com.xebia.services.monitor;

import org.hyperic.sigar.*;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collection;

/**
 * Created by Pgupta on 17-08-2016.
 */
@Service
public class MonitorService {

    public void cpuUsage(Collection<Metric<?>> result) throws SigarException, InterruptedException {
        Sigar sigar = new Sigar();
        CpuPerc cpuperc;
        Metric<Double> usage = null;
        try {
            cpuperc = sigar.getCpuPerc();
            usage = new Metric<Double>("cpu.usage",(cpuperc.getCombined() * 100));
            result.add(usage);
        } catch (SigarException se) {
            se.printStackTrace();
        }
    }

    public void ramUsage(Collection<Metric<?>> result) throws SigarException, InterruptedException {
        Sigar sigar = new Sigar();
        Mem mem;
        Metric<Double> usage = null;
        try {
            mem = sigar.getMem();
            usage = new Metric<>("ram.usage", mem.getUsedPercent());
            result.add(usage);
        } catch (SigarException se) {
            se.printStackTrace();
        }
    }

    public void diskUsage(Collection<Metric<?>> result) throws SigarException, InterruptedException {
        Sigar sigar = new Sigar();
        FileSystemUsage filesystemusage = null;
        Metric<Long> free = null;
        Metric<Long> used = null;
        try {
            filesystemusage = sigar.getFileSystemUsage("C:");
            free = new Metric<Long>("disk.c.free",filesystemusage.getAvail());
            used = new Metric<Long>("disk.c.used",(filesystemusage.getTotal() - filesystemusage.getAvail()));
            result.add(free);
            result.add(used);
        } catch (SigarException se) {
            se.printStackTrace();
        }
    }

    @PostConstruct
    public void loadLib(){
        System.out.print("Loading . . . . . .");
        String mylibname = System.mapLibraryName("sigar-amd64-winnt");
        System.load(new File("F:\\Softwares\\jar\\hyperic-sigar-1.6.4\\hyperic-sigar-1.6.4\\sigar-bin\\lib", mylibname).getAbsolutePath());

    }
}
