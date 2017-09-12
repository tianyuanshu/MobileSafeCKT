package com.ckt.mobilesafeckt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by D22280 on 2017/9/8.
 */

public class StreamUtils {

    public static  String readFromStream(InputStream is){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        byte buff[]=new byte[1024];
        int len;
        try {
            while( (len=is.read(buff))!=-1){
                bos.write(buff,0,len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toString();
    }
}
