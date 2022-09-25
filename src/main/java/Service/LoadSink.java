package Service;

import model.Sink;

import java.util.ArrayList;
import java.util.List;

public class LoadSink {
    public static void load(List<List<Sink>> Sinks, String[][] SinkRule){
        for(int i=0;i<SinkRule.length-1;i++){
            if(Integer.parseInt(SinkRule[i][7])!=1){
                List ruleList = new ArrayList();
                for(int j=0;j<Integer.parseInt(SinkRule[i][7]);j++){
                    Sink Sink = new Sink(SinkRule[i+j][1],SinkRule[i+j][2],SinkRule[i+j][3],SinkRule[i+j][5],Integer.parseInt(SinkRule[i+j][6]),Integer.parseInt(SinkRule[i+j][7]));
                    ruleList.add(Sink);
                }
                Sinks.add(ruleList);
                i = i+Integer.parseInt(SinkRule[i][7]);
            } else {
                List ruleList = new ArrayList();
                Sink Sink = new Sink(SinkRule[i][1], SinkRule[i][2], SinkRule[i][3], SinkRule[i][5], Integer.parseInt(SinkRule[i][6]), Integer.parseInt(SinkRule[i][7]));
                ruleList.add(Sink);
                Sinks.add(ruleList);
            }
        }
    }
}
