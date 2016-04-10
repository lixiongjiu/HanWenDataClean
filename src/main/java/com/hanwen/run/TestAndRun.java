package com.hanwen.run;

import com.hanwen.core.DataCleaner;
import com.hanwen.core.Indexer;

import javax.swing.filechooser.FileSystemView;

/**
 * Created by LSQ on 2016/1/8.
 */
public class TestAndRun {
    public static void main(String[] args) {
/*
        Indexer indexer=new Indexer();
        indexer.createIndex1("E:\\瀚文项目数据\\sample_new0408\\sample_new0408.txt");*/
        DataCleaner dataCleaner = new DataCleaner();
        String dest = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "\\results\\";

        dataCleaner.merge1("E:\\瀚文项目数据\\sample_new0408\\sample_new0408.txt", dest);
    }
}



