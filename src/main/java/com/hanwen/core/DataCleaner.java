package com.hanwen.core;

import com.hanwen.utils.CleanStrategies;
import com.hanwen.utils.FileOperator;
import com.hanwen.utils.SplitAndCleanUtils;
import com.hanwen.utils.Struct;

import java.io.*;
import java.util.Date;

/**
 * Created by LSQ on 2015/12/27.
 */
public class DataCleaner {


    /**
     * 对表1进行预处理，即将可能是同一个公司的信息进行合并
     *
     * @param src 源文件路径(待清洗) 包含id nme add(多个) 字段
     * @param tar 目标文件路径(清洗后) 包含id name add(多个) 字段a
     */
    public void merge1(String src, String tar) {

        Indexer indexer = new Indexer();
        Date start = new Date();
        try {

            BufferedReader br = FileOperator.getBufferReader(src);
            BufferedWriter bw = FileOperator.getBufferWriter(tar);
            int count = 1; //计数处理文件数
            int countFalse = 1; //计数错误文件数量

            String line = null;

            //搜索前的准备，设置每次搜索的TOP K和搜索的索引（1和2，代表表1和表2）
            indexer.prevSearch(20, 1);
            while ((line = br.readLine()) != null) {
                //line 分别包含id name address(多个字段)和其他信息
                String[] items = SplitAndCleanUtils.splitAndClean(line);
                //整个记录都为空或者公司名为空，则跳过这条记录，无可救药
                if (items == null)
                    continue;

                String id = items[0];
                String name = items[1];
                String address = items[2];
                String others = items[3];

                //清洗后的结果
                //可以有选择的传入others信息，辅助判断,此时传入的是Bill_Type
                String billType = others.substring(others.lastIndexOf("\t") + 1);
                indexer.searchIndex(id,name, address, billType);

                //判断这条记录是否被清洗过,被清洗过则更新信息
                if (!name.equalsIgnoreCase(Struct.companyName) || !address.equalsIgnoreCase(Struct.companyAddress)) {
                    countFalse++;
                }
                count++;
                //清洗后的结果写入新文件
                bw.write(id + "\t" + Struct.companyName + "\t" + Struct.companyAddress + "\t" + others + "\n");
                if (count % 1000 == 0)
                    bw.flush();
            }
            Date end = new Date();
            indexer.nextSearch();

            System.out.println("处理了" + --count + "条记录,\t" + "总共清洗了：" + countFalse);
            System.out.println("总共耗时" + (float) (end.getTime() - start.getTime()) / 1000 + " 秒");
            bw.flush();
            bw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对表2进行预处理，即将可能是同一个公司的信息进行合并
     *
     * @param src 源文件路径 包含公司名和全地址两个字段
     * @param tar 目标文件路径
     */
   /* public void merge2(String src, String tar) {
        Indexer indexer = new Indexer();
        Map<String, String> map = new HashMap<String, String>();

        try {
            File srcFile = new File(src);
            BufferedReader br = new BufferedReader(new FileReader(srcFile));
            String line = null;

            File tarFile = new File(tar);
            BufferedWriter bw = new BufferedWriter(new FileWriter(tarFile));
            int count = 1;
            int countFalse = 0;
            while ((line = br.readLine()) != null) {
                List<String> searchRes = indexer.searchIndex(deleteAllSymbol(line), 1, 2);

                int cut = line.indexOf("\t");
                String name = deleteSuffix(deleteCompanyNameSymbol(line.substring(0, cut)));
                String address = null;
                if (cut != -1) address = deleteCompanyAddressSymbol(line.substring(cut + 1));
                String newName = name;
                String newAddress = address;

                if (newAddress.length() > 2 && newAddress.charAt(0) == '.') {
                    newAddress = newAddress.substring(2);
                }

                for (int i = 0; i < searchRes.size(); i++) {
                    String tmpLine = searchRes.get(i);
                    int tmpCut = tmpLine.indexOf("\t");
                    String tmpName = deleteSuffix(deleteCompanyNameSymbol(tmpLine.substring(0, tmpCut)));
                    String tmpAddress = deleteCompanyAddressSymbol(tmpLine.substring(tmpCut + 1));
                    //如果地址第一位是.  即存在错位现象，先去除错位
                    if (tmpAddress.length() > 2 && tmpAddress.charAt(0) == '.' && tmpAddress.charAt(1) == '\t') {
                        tmpAddress = tmpAddress.substring(2);
                    }

                    if ((newName.replaceAll("[^a-zA-Z0-9 ]", "").equals(tmpName.replaceAll("[^a-zA-Z0-9 ]", "")) || cosSimlarity(word2vec(newName), word2vec(tmpName)) > 0.98)
                            && (cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.96 || addressContain(newAddress, tmpAddress) > 0.85)) {

//                        System.out.println("---------" + newName + "---" + tmpName);
                        newName = (tmpName.length() > newName.length()) ? tmpName : newName;
                        newAddress = (tmpAddress.length() > newAddress.length()) ? tmpAddress : newAddress;
                    }

                    if (cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.96
                            && (publicPrefix(newName, tmpName) > 0.5 || editSimilarity(newName.replaceAll(" \\.", ""), tmpName.replaceAll(" \\.", "")) > 0.5)) {

                        newName = (tmpName.length() > newName.length()) ? tmpName : newName;
                        newAddress = (tmpAddress.replaceAll("[^a-zA-Z0-9]", "").length() > newAddress.replaceAll("[^a-zA-Z0-9]", "").length()) ? tmpAddress : newAddress;

                        //当src的公司名比tmp的公司名短，并且有较多的公共前缀并且首字母相同并且地址有一定的相似度则进行更新
                    } else if ((publicPrefix(newName, tmpName) > 0.5 || editSimilarity(newName.replaceAll(" \\.", ""), tmpName.replaceAll(" \\.", "")) > 0.6)
                            && ((cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.9 && addressPublicItems(newAddress, tmpAddress) >= 0.5) ||
                            (cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.92 || addressPublicItems(newAddress, tmpAddress) > 0.7))) {

                        newName = (tmpName.length() > newName.length()) ? tmpName : newName;
                        newAddress = (tmpAddress.replaceAll("[^a-zA-Z0-9]", "").length() > newAddress.replaceAll("[^a-zA-Z0-9]", "").length()) ? tmpAddress : newAddress;
                    }

                    if (!name.equalsIgnoreCase(newName) || !address.equalsIgnoreCase(newAddress)) {
                        countFalse++;
                    }
                }
                bw.write(newName + "\t" + newAddress + "\n");
                System.out.println(count++ + ":" + newName);
            }
            bw.flush();
            bw.close();
            br.close();
            System.out.println("处理了" + --count + "条记录,\t" + "总共错误了：" + countFalse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 用表1清洗后的结果查询表2返回一个最相似的公司名 加一个新字段在原市公司明后，作为参考
     *
     * @param src 源文件路径 包含id name add(多个) 字段
     * @param tar 目标文件路径 包含id name 新增表2忠相似公司名 add(多个) 这些字段
     */
   /* public void clean(String src, String tar) {

        Indexer indexer = new Indexer();
        try {
            File srcFile = new File(src);
            BufferedReader br = new BufferedReader(new FileReader(srcFile));
            String line;

            File tarFile = new File(tar);
            BufferedWriter bw = new BufferedWriter(new FileWriter(tarFile));
            int count = 1;
            int right = 0;
            while ((line = br.readLine()) != null) {
                int cut1 = line.indexOf("\t");
                int id = Integer.parseInt(line.substring(0, cut1));
                String cutLine = line.substring(cut1 + 1);
                int cut2 = cutLine.indexOf("\t");
                String name = cutLine.substring(0, cut2);
                String address = cutLine.substring(cut2 + 1);
                String newName = null;
                List<String> searchRes = indexer.searchIndex(deleteCompanyAddressSymbol(name), 20, 2);

                //用来标记lucene是否找到和表2的匹配项
                for (int i = 0; i < searchRes.size(); i++) {
                    String tmpLine = searchRes.get(i);
                    int tmpCut = tmpLine.indexOf("\t");
                    String tmpName = tmpLine.substring(0, tmpCut);

                    //通过表1的公司名查询表2的公司名，看余弦相似，0.9这个值不是很相似，这是由于是lucene已经过滤一遍了
                    if (cosSimlarity(str2vec(tmpName), str2vec(name)) > 0.9) {
                        newName = tmpName;
                        right++;
                        break;
                    }
                }

                //将merge后的结果，公司名后面加入一个表2的查询公司名
                bw.write(id + "\t" + name + "\t" + newName + "\t" + address);
                System.out.println(count++ + ":" + newName);
            }
            System.out.println("约" + (count - right) + "条记录无法从信息表定位！");
            bw.flush();
            bw.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}