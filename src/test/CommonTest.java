import org.junit.Test;

import javax.swing.filechooser.FileSystemView;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CommonTest {

    @Test
    public void baseTest(){
        System.out.println(FileSystemView.getFileSystemView().getHomeDirectory().toString());
    }
    @Test
    public void testMethod(){
        DataCleaner dataCleaner=new DataCleaner();
        System.out.println(dataCleaner.FirmNameMess("dsdaf"));
    }
    @Test
    public void testDel(){
        DataCleaner dataCleaner=new DataCleaner();
        System.out.println(dataCleaner.deleteAllSymbol("Infadfa werwerweqr 2343423423.sdfasd"));
        System.out.println("'sdafdfsaf343214$#@".replaceAll("[^0-9a-zA-z$@]", ""));
        System.out.println("avds423423442f sadfsfinc. adsfas".toUpperCase().replaceAll("(.*(INC|LLC))(.*)","$1"));
        System.out.println(dataCleaner.deleteSuffix("sadf.Inc .werwe"));
        System.out.println("   ".replaceAll("\\s","a"));
    }
    @Test
    public void testSplit(){
        String line="4\tCummins Business Services\tP O BOX NO 290909\tNASHVILLE TN 37229-0909          US\t\t\tEXDO\tUS\tYORKTOWN EXPRESS\t00002\t14";
        String[] items=line.split("\t");
        System.out.println("CompanyName:"+items[1]);
        System.out.println("addres4:"+items[4]);
        System.out.println("billTyoe:"+items[10]);
        items=line.split("\t",3);
        System.out.println(items[0]);
        System.out.println(items[1]);
        System.out.println(items[2]);


    }
}
