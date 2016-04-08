import javax.swing.filechooser.FileSystemView;

/**
 * Created by LSQ on 2016/1/8.
 */
public class Test {
    public static void main(String[] args) {

        String desktop = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "\\data_clean\\";

        Indexer indexer = new Indexer();
        //索引将在桌面创建 index1和index2
        indexer.createIndex1(desktop + "1_ready.txt");  //对表1穿件索引
        indexer.createIndex2(desktop + "2_clean.txt"); //对表2创建索引

        DataCleaner dataCleaner = new DataCleaner();
        dataCleaner.merge1(desktop + "1_ready.txt", desktop + "1_clean.txt"); //对表1进行清洗
        dataCleaner.clean(desktop + "1_clean.txt", desktop + "1_result.txt"); //对表1清洗的结果加入一个表2查询字段(公司名)
    }
}



