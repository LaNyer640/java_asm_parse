package rules;

import app.Command;
import model.Sink;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Ruleservice {
    private String owner;
    private String methodname;
    private String desc;
    private int targetIndex;
    private String sinkName;
    private int flag;
    private int flag2;
    private int flag3;


    public  void start(List<List<Sink>> Sinks,Command command){
        flag2=Sinks.size()-1;
        if(command.rule!=null){
            loadRules(Sinks,command.rule);
        }
        //print(Sinks);
    }

    private void loadRules(List<List<Sink>> Sinks,String rules){
        try{
            FileInputStream rulefile = new FileInputStream(rules);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rulefile));
            String rule = null;
            while((rule = bufferedReader.readLine() )!= null){
                List tempList = new ArrayList();
                String[] temp = rule.split("\\s+");
                if(temp.length==6){
                    owner = temp[0];
                    methodname = temp[1];
                    desc = temp[2];
                    sinkName = temp[3];
                    targetIndex = Integer.parseInt(temp[4]);
                    flag = Integer.parseInt(temp[5]);
                    Sink sink = new Sink(owner,methodname,desc,sinkName,targetIndex,flag);
                    tempList.add(sink);
                    if(flag3 ==0){
                        flag3 = Integer.parseInt(temp[5]);
                    }
                    if(flag==1){
                        Sinks.add(tempList);
                        flag2++;
                        flag3--;
                    } else if(flag!=1&&flag3==flag){
                        Sinks.add(tempList);
                        flag2++;
                        flag3--;
                    } else if(flag!=1&& flag3!=flag && flag3 !=0){
                        Sinks.get(flag2).add(sink);
                        flag3--;
                    } else if(flag!=1&& flag3==0){
                        Sinks.get(flag2).add(sink);
                        flag2++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*private void print(List<List<Sink>> Sinks){
        for(List<Sink> Sink:Sinks){
            for(Sink sink:Sink){
                System.out.println(sink.getName());
            }
            System.out.println("---------------------------------------------");
        }
    }*/
}
