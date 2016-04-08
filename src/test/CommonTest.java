import com.hanwen.core.DataCleaner;
import com.hanwen.utils.CleanStrategies;
import com.hanwen.utils.SplitAndCleanUtils;
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
       /* DataCleaner dataCleaner=new DataCleaner();
        System.out.println(dataCleaner.FirmNameMess("dsdaf"));*/
    }
    @Test
    public void testDel(){
      String test="sadfasdf\tdsfadf";
        int index=test.indexOf("\t");
        if(index!=-1) {
            System.out.println(test.substring(0, index));
            System.out.println(test.substring(index+1));
        }

        System.out.println(CleanStrategies.deleteSuffix("asdfasdf INc.sadfa"));
        System.out.println(CleanStrategies.extractDigits("sadf2342 45442daf53453342jsdaf23"));

    }
    @Test
    public void testSplit(){
       /* String line="4\tCummins Business Services\tP O BOX NO 290909\tNASHVILLE TN 37229-0909          US\t\t\tEXDO\tUS\tYORKTOWN EXPRESS\t00002\t14";
        String[] items=line.split("\t");
        System.out.println("CompanyName:"+items[1]);
        System.out.println("addres4:"+items[4]);
        System.out.println("billTyoe:"+items[10]);
        items=line.split("\t",3);
        System.out.println(items[0]);
        System.out.println(items[1]);
        System.out.println(items[2]);*/
        String line="id\tadsfasdf\tfdsadr1\taddr2llc dsaf\taddr3\taddr4\tcarrier_code\tvessel_country\tvessel-name\tvoyage_number\tbill_type";
        String[] results= SplitAndCleanUtils.splitAndClean(line);
        System.out.println(results[0]);
        System.out.println(results[1]);
        System.out.println(results[2]);
        System.out.println(results[3]);
      /*  int index=CleanStrategies.FirmNameMess("sadfasdfinc.werwr");
        System.out.println(index);
        System.out.println("sadfasdfinc.werwr".substring(0,index+4));*/

    }
}
