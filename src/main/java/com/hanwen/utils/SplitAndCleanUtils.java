package com.hanwen.utils;

import java.util.List;

/**
 * Created by LXJ on 2016/4/8.
 */


/**
 * @Create by:lixiongjiu
 * @Date:2016/4/8 20:47
 * @Method:SliptAndClean
 * @function:将记录切割成ID,公司名，地址，其他部分，并纠正公司名信息蹿位到地址字段的情况
 * @Params: line 输入的一行信息，对应数据库的一条记录，不同字段之间用\t分割
 * @Return: 字符串数组，包含id，公司名，地址，其他部分（字符串个数为4）
**/

public class SplitAndCleanUtils {
    public static String[] splitAndClean(String line) {
        if (line.equals("\n"))
            return null;


        //先将一行记录分成三个部分：id，consignee_Name,地址和其他部分
        String[] items = line.split("\t", 3);
        /*System.out.println(items[0]);
        System.out.println(items[1]);
        System.out.println(items[2]);;*/
        //处理收货公司名称，标准化写法，例如and，AND，&;并去除会造成lucene报错的关键字，例如\
        //公司名为空时，放弃这条记录（非常重要，有30多万条这种记录）
        if(items[1]==null)
            return null;

        //将地址和其他部分分成地址（4个字段）和其他部分,共五个部分
        String[] addressAndOthers = items[2].split("\t", 5);

        //处理公司名蹿位到地址字段的情况

        if(CleanStrategies.FirmNameMess(items[1])==-1){             //公司名字段中没有INC，LLC等信息，则有可能需要调整，否则不需要
            int index=-1;
            for(int i=0;i<4;i++){

                //地址i+1需要一部分切割回去
                if( (index=CleanStrategies.FirmNameMess(addressAndOthers[i]))!=-1 )
                {
                    //把需要切割的地址段之前的地址段也加到公司名中去，因为它们也是公司名的一部分
                    //例如：发现地址3中前一部分是公司名的部分，那么显然地址1，地址2也是公司名的一部分，也要加上去；
                    //同时对应的地址段变成空的
                    if(i>0){
                        for(int j=0;j<i;j++) {
                            items[1] += addressAndOthers[j];
                            addressAndOthers[j]=null;
                        }

                    }
                    items[1]+=addressAndOthers[i].substring(0,index+4);

                    if(index+4<addressAndOthers[i].length())
                        addressAndOthers[i]=addressAndOthers[i].substring(index+4);
                    else
                        addressAndOthers[i]=null;

                    //一旦找到属于需要切割的地址段，则后面的地址段都不需要切割了,到此结束
                    //实际测试证明：地址1含有一部分公司名的概率最大，所以该段代码时间复杂度并不高
                    break;
                }

            }
        }

        String companyName = CleanStrategies.deleteSuffix(CleanStrategies.deleteCompanyNameSymbol(items[1]));


        //判断地址是否为空
        //合并地址的4个字段
        String originAddress =
                addressAndOthers[0] + "\t" +
                        addressAndOthers[1] + "\t" +
                        addressAndOthers[2] + "\t" +
                        addressAndOthers[3];
        //清理地址部分，只保留下字母，数字，&，@，#，制表符，\.和-等字符
        String address = CleanStrategies.deleteCompanyAddressSymbol(originAddress);

        return new String[]{items[0],companyName,address,addressAndOthers[4]};
    }
}
