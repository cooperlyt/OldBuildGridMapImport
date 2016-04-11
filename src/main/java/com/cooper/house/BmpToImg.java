package com.cooper.house;

import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.*;

/**
 * Created by cooper on 4/11/16.
 */
public class BmpToImg {

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).trim().toUpperCase();
            }
        }
        return null;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    private static final String SRC_DIR = "/root/Documents/IDCardPictureDir";

    private static final String DESC_DIR= "/root/Documents/personImg/";

    public static void main(String[] args) throws IOException {
        //BmpReader.bmpTojpg();

        File root = new File(SRC_DIR);
        File[] files = root.listFiles();
        for(File f: files){
            if (f.isFile()){
                if ("BMP".equals(getExtensionName(f.getName()))){
                    Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

                    ImageWriter writer = (ImageWriter)iter.next();
                    ImageWriteParam iwp = writer.getDefaultWriteParam();
                    iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    iwp.setCompressionQuality(1);   // an integer between 0 and 1
                    File file = new File(DESC_DIR + getFileNameNoEx(f.getName()) + ".jpg");
                    FileImageOutputStream output = new FileImageOutputStream(file);
                    writer.setOutput(output);
                    FileInputStream in = new FileInputStream(f.getAbsolutePath());

                    BufferedImage bufferedImage = ImageIO.read(in);
                    IIOImage image = new IIOImage(bufferedImage, null, null);
                    writer.write(null, image, iwp);
                    writer.dispose();

                    in.close();
                }else if ("JPG".equals(getExtensionName(f.getName())) || "JPEG".equals(getExtensionName(f.getName()))){
                    try {
                        copyFile(f,new File(DESC_DIR + f.getName()));
                    } catch (IOException e) {

                        e.printStackTrace();
                        return;
                    }
                }

            }
        }

    }


}
