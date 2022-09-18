package Util;

import java.io.File;

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
}
