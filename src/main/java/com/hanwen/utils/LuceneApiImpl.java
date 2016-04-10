package com.hanwen.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Administrator on 2016/1/6.
 */
public class LuceneApiImpl implements ILuceneAPI{

    //lucene三个关键变量
    private IndexWriter writer=null;
    private IndexReader reader=null;
    private IndexSearcher searcher=null;

    public static String ROOT_PATH = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "\\";
    public static String INDEX_DIR1 = ROOT_PATH + "index1";
    public static String INDEX_DIR2 = ROOT_PATH + "index2";

    //创建索引和搜索所有的分词器都是WhitespaceAnalyzer
    private static Analyzer analyzer = new WhitespaceAnalyzer();
    private static Directory directory = null;

    public String getIndexURL() {
        return INDEX_DIR1;
    }

    public void setIndexURL(String indexURL) {
        INDEX_DIR1 = indexURL;
    }

    public IndexWriter getWriter() {
        try {

            //每次建立索引前，都删除之前的索引文件
            File fileIndex = new File(INDEX_DIR1);
            if (fileIndex.exists()) {
                FileOperator.deleteDir(fileIndex);
            } else {
                fileIndex.mkdir();
            }
            if(writer==null) {
                //索引的存放位置
                directory= FSDirectory.open(Paths.get(INDEX_DIR1));
                IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

                //设置indexReader的属性
                //iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                writer=new IndexWriter(directory, iwc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return  writer;
        }
    }

    //单例模式，打开IndexReader
    public IndexReader getReader(int type){
        try {
            //打开索引文件
            if(reader==null)
            {
                if(type==1)
                    directory= FSDirectory.open(Paths.get(INDEX_DIR1));
                else
                    directory=FSDirectory.open(Paths.get(INDEX_DIR2));
                reader=DirectoryReader.open(directory);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            return reader;
        }
    }

    //关闭reader，提供异常处理
    public void closeReader(){
        try{
            if(reader!=null) {
                reader.close();
                reader=null;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    //关闭writer,提供异常处理
    public void closeWriter(){
        try {
            if(writer!=null) {
                writer.close();
                writer=null;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void commit(){
        try {
            writer.commit();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public IndexSearcher getIndexSearch(int type){
        if(type==1)
            searcher=new IndexSearcher(getReader(1));
        else
            searcher=new IndexSearcher(getReader(2));
        return searcher;
    }

    public void printMatches(TopDocs matches)throws Exception{
        ScoreDoc[] hits=matches.scoreDocs;
        for(ScoreDoc scoreDoc:hits){
            System.out.println("匹配得分：" + scoreDoc.score);
            System.out.println("文档索引ID：" + scoreDoc.doc);
            Document document =searcher.doc(scoreDoc.doc);
            System.out.println(document.get("contents"));
            System.out.println(document.get("id"));
        }
        reader.close();
    }
    public void closeSearcher(){
        closeReader();
    }
    public void deleteAllIndex(){
        try{
            writer.deleteAll();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
