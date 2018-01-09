package com.lxr.iot.bootstrap.time;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.pool.Scheduled;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.concurrent.*;

/**
 * 扫描消息确认
 *
 * @author lxr
 * @create 2018-01-08 19:22
 **/
@Data
public class SacnScheduled extends ScanRunnable {

    private Channel channel;

    private  ScheduledFuture<?> submit;

    private  int  seconds;

    public SacnScheduled(ConcurrentLinkedQueue queue,Channel channel,int seconds) {
        super(queue);
        this.channel=channel;
        this.seconds=seconds;
    }

    public  void start(){
        Scheduled  scheduled = new ScheduledPool();
        this.submit = scheduled.submit(this);
    }

    public  void close(){
        if(submit!=null && !submit.isCancelled()){
            submit.cancel(true);
        }
    }


    @Override
    public void doInfo(MqttMessage poll) {
            if(checkTime(poll)){
                pubMessage(channel,poll);
            }
    }

    private boolean checkTime(MqttMessage poll) {
        return System.currentTimeMillis()-poll.getTimestamp()>=seconds*1000;
    }


    static class ScheduledPool implements Scheduled {
        private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        public ScheduledFuture<?> submit(Runnable runnable){
            return scheduledExecutorService.scheduleAtFixedRate(runnable,10,10, TimeUnit.SECONDS);
        }


    }

}
