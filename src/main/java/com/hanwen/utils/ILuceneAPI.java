package com.hanwen.utils;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

/**
 * Created by Administrator on 2016/1/9.
 */
public interface ILuceneAPI {
    public String getIndexURL();
    public void setIndexURL(String indexURL);


    public IndexReader getReader(int type);
    public IndexWriter getWriter();

    public void closeReader();
    public void closeWriter();
    public void commit();
    public IndexSearcher getIndexSearch(int type);
    public void printMatches(TopDocs matches) throws Exception;
    public void deleteAllIndex();
    public void closeSearcher();
}
