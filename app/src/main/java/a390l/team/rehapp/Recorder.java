package a390l.team.rehapp;

import java.util.ArrayList;

/**
 * Created by Paul Daccache on 2017-03-23.
 */

public class Recorder {

    protected ArrayList<Double> HRTime = new ArrayList();
    protected ArrayList<Integer> HeartBeat= new ArrayList();
    protected ArrayList<Double> STime = new ArrayList();
    protected ArrayList<Double> Speed= new ArrayList();

    void AddHRDatapoint(double time, int heartrate)
    {
        HRTime.add(time);
        HeartBeat.add(heartrate);
    }

    void AddSDatapoint(double time,double speed)
    {
        STime.add(time);
        Speed.add(speed);
    }

    Double[] getHRTime()
    {
        Double[] timereturn = HRTime.toArray(new Double[HRTime.size()]);
        return timereturn;
    }

    Integer[] getHeartrate()
    {
        Integer[] HRreturn= HeartBeat.toArray(new Integer[HeartBeat.size()]);
        return HRreturn;
    }

    Double[] getSTime()
    {
        Double[] timereturn = STime.toArray(new Double[STime.size()]);
        return timereturn;
    }

    Double[] getSpeed()
    {
        Double[] Sreturn = Speed.toArray(new Double[Speed.size()]);
        return Sreturn;
    }

    Double getDistance()
    {
        double distance=0;
        for(int i =1;i<Speed.size()-1;i++)
        {
            distance = distance + (Speed.get(i)-Speed.get(i-1))*(STime.get(i)-STime.get(i-1));
        }
        return distance;
    }




    int getCalories()
    {
        return 0;
    }



}
