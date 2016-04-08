package com.hanwen.utils;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LSQ on 2015/11/22.
 */
public class FileOperator {

    /**
     * 实现文件的快速复制
     * <p>实现文件的复制<br>
     * 采用异步io实现的文件复制。
     *
     * @param srcFile 源文件
     * @param destDir 目标文件
     * @return 没有返回值
     */
    public static void copyFile(File srcFile, File destDir) {
        try {
            FileChannel fcin = new FileInputStream(srcFile).getChannel();
            FileChannel fcout = new FileOutputStream(destDir).getChannel();
            long size = fcin.size();
            fcin.transferTo(0, fcin.size(), fcout);
            fcin.close();
            fcout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取文件。
     * <p>读取文件<br>
     * 递归的读取文件夹下(包括子文件夹下)的所有文件，并返回list结果。
     *
     * @param file           源文件夹
     * @param resultFileName 存放读取的文件
     * @return 返回一个resultFileName，存放读取结果
     */
    public static List<File> getFiles(File file, List<File> resultFileName) {
        File[] files = file.listFiles();
        if (files == null) return resultFileName;
        for (File f : files) {
            if (f.isDirectory()) {
                getFiles(f, resultFileName);
            } else
                resultFileName.add(f);
        }
        return resultFileName;
    }


    /**
     * 读取txt文件
     * <p>读取txt文件<br>
     * 读取当先文件下的所有文件(不包括子文件夹)，并存储所有的txt文件。
     *
     * @param f 当前要读取文件夹
     * @return 返回一个fileList，存放读取的结果
     */
    public static List<File> getFileList(File f) {
        File[] files = f.listFiles();
        List<File> fileList = new ArrayList<File>();
        if (files != null) {
            for (File file : files) {
                if (isTxtFile(file.getName())) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 判断是否是txt文件
     * <p>判断是否是txt文件<br>
     * 判断是否是txt文件。
     *
     * @param fileName 当前要读取的文件
     * @return 返回一个boolean变量true表示是txt，false反之
     */
    public static boolean isTxtFile(String fileName) {
        if (fileName.lastIndexOf(".txt") > 0) {
            return true;
        }
        return false;
    }

    /**
     * txt文件转换城字符串
     * <p>txt文件转换城字符串<br>
     * 读取一个txt文件并将其转换城String类型
     *
     * @param file 当前要读取的文件
     * @return 返回一个String存储txt的内容
     */
    public static String txt2String(File file) {
        String result = "";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                if (s.equals("\n")) continue;
                result = result + s;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BufferedReader getBufferReader(String dataPath){
        File data=new File(dataPath);
        if(!data.exists())
            return null;
        try {
            return new BufferedReader(new FileReader(data));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 删除目录
     * <p>删除目录<br>
     * 删除文件目录及目录下的所有文件。
     *
     * @param file 当前要删除的目录
     * @return 没有返回值
     */
    public static void deleteDir(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i]);
                }
            }
        }
        file.delete();
    }

    /**
     * 获取文件名
     * <p>获取文件名<br>
     * 获取一个文件名(无拓展名)。
     *
     * @param fileName 当前要获取的文件所在路径字符串。
     * @return 返回一个字符串是文件的名字
     */
    public static String getFileNameNoEx(String fileName) {
        File tempFile = new File(fileName.trim());
        fileName = tempFile.getName();
        if ((fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }

    /**
     * 快速压缩文件
     * <p>快速压缩文件<br>
     * 将文件或文件夹压缩到指定路径。
     *
     * @param pathName    当前要压缩的文件所在路径全名
     * @param srcPathName 压缩后的文件路径及全名
     * @return 没有返回值
     */
    public static void zip(String pathName, String srcPathName) {
        File srcdir = new File(srcPathName);
        zip(pathName, srcdir);
    }

    public static void zip(String pathName, File srcdir) {
        try {
            File zipFile = new File(pathName);
            if (!srcdir.exists()) return;
            Project prj = new Project();
            Zip zip = new Zip();
            zip.setProject(prj);
            zip.setDestFile(zipFile);
            FileSet fileSet = new FileSet();
            fileSet.setProject(prj);
            fileSet.setDir(srcdir);
            zip.addFileset(fileSet);
            zip.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
