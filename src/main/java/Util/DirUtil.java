package Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirUtil {

    public static boolean removeDir(File dir){
        if(dir.isDirectory()){
            String[] children = dir.list();
            if(children != null){
                for (String child : children){
                    boolean success = removeDir(new File(dir,child));
                    if(!success){
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public static List<String> getAllFile(String directoryPath) {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(directoryPath);;
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                list.add(file.getAbsolutePath());
                list.addAll(getAllFile(file.getAbsolutePath()));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }
}
