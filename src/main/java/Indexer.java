import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LSQ on 2016/2/12.
 */
public class Indexer {

    public static String ROOT_PATH = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "\\";
    public static String INDEX_DIR1 = ROOT_PATH + "index1";
    public static String INDEX_DIR2 = ROOT_PATH + "index2";

    //创建索引和搜索所有的分词器都是WhitespaceAnalyzer
    private static Analyzer analyzer = new WhitespaceAnalyzer();
    private static Directory directory = null;
    private static IndexWriter indexWriter = null;

    /**
     * 表1创建倒排索引
     *
     * @param path 待创建索引文件所在路径
     * @return
     */
    public boolean createIndex1(String path) {

        FileOperator fileOperator = new FileOperator();
        File fileIndex = new File(INDEX_DIR1);
        if (fileIndex.exists()) {
            fileOperator.deleteDir(fileIndex);
        } else {
            fileIndex.mkdir();
        }
       /* 开始时间*/
        Date date1 = new Date();
        File data = new File(path);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        int i = 1;

        try {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            directory = FSDirectory.open(Paths.get(INDEX_DIR1));
            indexWriter = new IndexWriter(directory, config);

            FileReader fr = new FileReader(data);
            BufferedReader br = new BufferedReader(fr);//构造一个BufferedReader类来读取文件
            String string = null;
            DataCleaner dataCleaner = new DataCleaner();
            while ((string = br.readLine()) != null) {
                if (string.equals("\n")) continue;
                int cut1 = string.indexOf("\t");
                if (cut1 == -1) continue;
                String tmpString = string.substring(cut1 + 1);
                int cut2 = tmpString.indexOf("\t");
                if (cut2 == -1) continue;

                String name = dataCleaner.deleteSuffix(dataCleaner.deleteCompanyNameSymbol(tmpString.substring(0, cut2)));
                String address = dataCleaner.deleteCompanyAddressSymbol(string.substring(cut2 + 1));

                Document document = new Document();
                document.add(new TextField("content", name + "\t" + address, Field.Store.YES));
//                document.add(new TextField("content", name, Field.Store.YES));
                indexWriter.addDocument(document);
                /*System.out.println(i++);*/
                i++;
            }
            br.close();
            indexWriter.commit();
            closeWriter(indexWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* 结束时间*/
        Date date2 = new Date();
        System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        System.out.println("共添加了" + --i + "个文件到索引");
        return true;
    }

    /**
     * 表2创建倒排索引
     *
     * @param path 待创建索引文件所在路径
     * @return
     */
    public boolean createIndex2(String path) {

        FileOperator fileOperator = new FileOperator();
        File fileIndex = new File(INDEX_DIR2);
        if (fileIndex.exists()) {
            fileOperator.deleteDir(fileIndex);
        } else {
            fileIndex.mkdir();
        }

        Date date1 = new Date();
        File data = new File(path);
        analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        int i = 1;

        try {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            directory = FSDirectory.open(Paths.get(INDEX_DIR2));
            indexWriter = new IndexWriter(directory, config);

            FileReader fr = new FileReader(data);
            BufferedReader br = new BufferedReader(fr);//构造一个BufferedReader类来读取文件
            String string = null;
            DataCleaner dataCleaner = new DataCleaner();
            while ((string = br.readLine()) != null) {
                if (string.equals("\n")) continue;
                String[] fields = string.split("\t");
//                int cut = string.indexOf("\t");
//                if (cut == -1) continue;

                String name = dataCleaner.deleteCompanyNameSymbol(fields[0]);
                String address = null;
                if (fields.length < 8) {
                    continue;
                } else {
                    address = dataCleaner.deleteCompanyAddressSymbol(fields[7]);
                }

                Document document = new Document();
                document.add(new TextField("content", name.toUpperCase() + "\t" + address, Field.Store.YES));
//                document.add(new TextField("content", name, Field.Store.YES));
                indexWriter.addDocument(document);
                System.out.println(i++);
            }
            br.close();
            indexWriter.commit();
            closeWriter(indexWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date date2 = new Date();
        System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        System.out.println("共添加了" + --i + "个文件到索引");
        return true;
    }

    /**
     * 关闭索引输入流
     *
     * @param indexWriter
     */
    public void closeWriter(IndexWriter indexWriter) {
        try {
            if (indexWriter != null) {
                indexWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检索文本
     *
     * @param text 检索的内容
     * @param top  检索返回几个结果
     * @param type 取1代表对表1的索引进行检索，取2对表2内容进行检索
     * @return
     */
    public List<String> searchIndex(String text, int top, int type) {

        List<String> searchFiles = new ArrayList();
        if (text == null || text.trim().equals("")) return searchFiles;

        try {
            if (type == 1) directory = FSDirectory.open(Paths.get(INDEX_DIR1));  //lucene5.x
            else if (type == 2) directory = FSDirectory.open(Paths.get(INDEX_DIR2));  //lucene5.x

            IndexReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            QueryParser parser = new QueryParser("content", analyzer);  //lucene5.x

            parser.setDefaultOperator(QueryParser.Operator.OR);
            parser.setLowercaseExpandedTerms(true);
            Query query = parser.parse(text);

            ScoreDoc[] hits = isearcher.search(query, top).scoreDocs;


            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                searchFiles.add(hitDoc.get("content"));

            }
            ireader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchFiles;
    }
}
