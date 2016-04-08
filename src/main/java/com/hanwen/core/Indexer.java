package com.hanwen.core;

import com.hanwen.utils.*;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LSQ on 2016/2/12.
 */
public class Indexer {

    private IndexWriter indexWriter = null;
    private IndexSearcher indexSearcher =null;
    private int topNum=5;                                       //默认搜索Top5个结果
    private ILuceneAPI luceneAPI = new LuceneApiImpl();
    private QueryParser queryParser=null;

    /**
     * 表1创建倒排索引
     * @param path 待创建索引文件所在路径
     * @return
     */
    public boolean createIndex1(String path) {
        indexWriter = luceneAPI.getWriter();
        BufferedReader br = FileOperator.getBufferReader(path);

       /* 开始时间*/
        Date startTime = new Date();

        int count = 0;

        try {

            String line = null;
            DataCleaner dataCleaner = new DataCleaner();
            while ((line = br.readLine()) != null) {
                if (line.equals("\n")) continue;

                //先将一行记录分成三个部分：id，consignee_Name,地址和其他部分
                String[] items=line.split("\t",3);

                //处理收货公司名称，标准化写法，例如and，AND，&;并去除会造成lucene报错的关键字，例如\
                String companyName = CleanStrategies.deleteSuffix(CleanStrategies.deleteCompanyNameSymbol(items[1]));

                //将地址和其他部分分成地址（4个字段）和其他部分,共五个部分
                String[] addressAndOthers=items[2].split("\t",5);
                //合并地址的4个字段
                String originAddress=
                        addressAndOthers[0]+"\t"+
                        addressAndOthers[1]+"\t"+
                        addressAndOthers[2]+"\t"+
                        addressAndOthers[3];
                //清理地址部分，只保留下字母，数字，&，@，#，制表符，\.和-等字符
                String address = CleanStrategies.deleteCompanyAddressSymbol(originAddress);

                Document document = new Document();
                //公司名和地址将被分词器解析、保存并且可被索引
                document.add(new Field("index", companyName + "\t" + address, IndexOpts.fieldType_analyzed_stored_indexed));

                //其他信息，包括承运人编号，船东国家编号，船号，航次，提单号，只是保存，仅仅为了搜索时可以提取出来辅助判断
                document.add(new Field("content",addressAndOthers[4],IndexOpts.fieldType_stored));
                indexWriter.addDocument(document);

                count++;
            }
            indexWriter.commit();
            luceneAPI.closeWriter();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* 结束时间*/
        Date endTime = new Date();
        System.out.println("创建索引-----耗时：" + (float)(endTime.getTime() -startTime.getTime())/1000 + "s\n");
        System.out.println("共添加了" + --count + "个文件到索引");
        return true;
    }

    /**
     * 表2创建倒排索引
     *
     * @param path 待创建索引文件所在路径
     * @return
     */
    public boolean createIndex2(String path) {
        indexWriter=luceneAPI.getWriter();
        BufferedReader br=FileOperator.getBufferReader(path);
        if(br==null){
            System.out.println("数据文件不存在或者打开错误");
            return false;
        }
        Date date1 = new Date();

        int count=0;

        try {

            String string = null;
            DataCleaner dataCleaner = new DataCleaner();
            while ((string = br.readLine()) != null) {
                if (string.equals("\n")) continue;
                String[] fields = string.split("\t");

                String name = CleanStrategies.deleteCompanyNameSymbol(fields[0]);
                String address = null;
                if (fields.length < 8) {
                    continue;
                } else {
                    address = CleanStrategies.deleteCompanyAddressSymbol(fields[7]);
                }

                Document document = new Document();
                document.add(new TextField("content", name.toUpperCase() + "\t" + address, Field.Store.YES));
                indexWriter.addDocument(document);
                count++;
            }
            br.close();
            indexWriter.commit();
            luceneAPI.closeWriter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date date2 = new Date();
        System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        System.out.println("共添加了" + --count + "个文件到索引");
        return true;
    }


    /**
     * 检索文本前的准备，主要是按照参数需求新建IndexSearcher，避免每次都新建IndexSearcher，节省开销
     * @param top  检索返回几个结果
     * @param type 取1代表对表1的索引进行检索，取2对表2内容进行检索
     * @return
     */
    public void prevSearch(int top, int type){
        topNum=top;
        if (type == 1)
            indexSearcher =luceneAPI.getIndexSearch(1);  //lucene5.x
        else
            indexSearcher =luceneAPI.getIndexSearch(2);  //lucene5.x

        //设置搜索解析器，并设置解析的文档是-idnex（包含公司名，地址信息）
        queryParser= new QueryParser("index",new WhitespaceAnalyzer());

        //解析器选项设置
        queryParser.setDefaultOperator(QueryParser.Operator.OR);
        queryParser.setLowercaseExpandedTerms(true);
    }


    /**
     * 检索文本
     * @param text 检索的内容
     * @return
     */
    public List<String> searchIndex(String text) {

        List<String> searchResults = null;
        if (text == null || text.trim().equals("")) return null;

        try {
            searchResults=new ArrayList<String>();
            Query query=queryParser.parse(text);
            ScoreDoc[] hits =indexSearcher.search(query, topNum).scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc =indexSearcher.doc(hits[i].doc);
                searchResults.add(hitDoc.get("content"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    /**
     * 搜索结束后的处理，主要是关闭相应的文件流
     * @param :void
     */
    public void nextSearch(){
        luceneAPI.closeSearcher();
    }
}
