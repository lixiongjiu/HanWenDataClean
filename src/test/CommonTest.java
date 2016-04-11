import com.hanwen.core.DataCleaner;
import com.hanwen.core.Indexer;
import com.hanwen.utils.*;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.junit.Test;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CommonTest {

    @Test
    public void baseTest() {
        System.out.println(FileSystemView.getFileSystemView().getHomeDirectory().toString());
    }

    @Test
    public void testIndex() {
        Indexer indexer = new Indexer();
        if(!indexer.createIndex1("E:\\瀚文项目数据\\Name_Not_NULL.txt"))
            System.out.println("创建索引失败!");
    }

    @Test
    public void testClear() {
        DataCleaner dataCleaner = new DataCleaner();
        String dest = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "\\results\\";
        dataCleaner.merge1("E:\\瀚文项目数据\\Name_Not_NULL.txt", dest);
    }

    @Test
    public void testSearch(){
        try {
            ILuceneAPI luceneAPI=new LuceneApiImpl();
            IndexSearcher indexSearcher=luceneAPI.getIndexSearch(1);
            QueryParser queryParser=new QueryParser("index",new WhitespaceAnalyzer());
            Query query=queryParser.parse("P O BOX NO 290909\tNASHVILLE TN 37229-0909          US\t\t\n");
            ScoreDoc[] scoreDocs=indexSearcher.search(query, 20).scoreDocs;
            for(ScoreDoc scoreDoc:scoreDocs){
                System.out.println(scoreDoc.score);
                Document document=indexSearcher.doc(scoreDoc.doc);
                String name_address=document.get("index");
                String others=document.get("content");
                System.out.println("公司名和地址："+name_address);
                System.out.println("分割后：");
                String[] items=name_address.split("\t");
                for(String item:items){
                    if(item==null || item.equals("") )
                        System.out.println("空");
                    else
                        System.out.println(item);
                }
                System.out.println("其他信息：" + others+"\n");

            }
            luceneAPI.closeSearcher();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void testSplit() {
        String line = "id\tcompanyNamellc\taddr1\taddr2\t\t\tcarrier_code\tvessel_country\tvessel-name\tvoyage_number\tbill_type";

        StringBuffer buffer=new StringBuffer();
        buffer.append("err");
        buffer.append("\t");
        buffer.append("add");
        buffer.append("\t");
        buffer.append("");

        String[] items=buffer.toString().split("\t",3);
        System.out.println(items.length);
        for(String item:items){
            if(item.equals(""))
                System.out.println("\"\"");
            else
                System.out.println(item);
        }

        /*int index = line.lastIndexOf("\t");
        String billType = line.substring(line.lastIndexOf("\t") + 1);
        System.out.println(billType);
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("(MM月dd日 HH:mm:ss)");
        String dateString = formatter.format(currentTime);
        System.out.println(dateString);*/

    }
    @Test
    public void testMehod(){
        System.out.println(" OR".replaceAll(" OR$", " OREGON"));
    }

    @Test
    public void testJavaBase(){


        String test="";
        test+="\t";
        test+="asfasfd";
        test+="\t";
        test+="";
        System.out.println(test.split("\t").length);
        System.out.println(test);
        try {
            FileWriter fw=new FileWriter("C:\\Users\\Administrator\\Desktop\\results\\test.txt");
            BufferedWriter bufferedWriter=new BufferedWriter(fw);
            bufferedWriter.write(test);
            bufferedWriter.write(test);
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
