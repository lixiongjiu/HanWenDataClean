package com;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LSQ on 2015/12/27.
 */
public class DataCleaner {

    /**
     * 删除公司名称的特殊符号
     *
     * @param s 原始输入
     * @return 返回删除符号后的字符串
     */
    public String deleteCompanyNameSymbol(String s) {
        s = s.replaceAll("\\.{1,}", " ");
        s = s.replaceAll(" AND ", " & ");
        s = s.replaceAll(" And ", " & ");
        s = s.replaceAll(" and ", " & ");
        s = s.replaceAll("[^\'a-zA-Z0-9-@&%#]", " ");
        s = s.replaceAll(" {2,}", " ");
        s = s.trim();
        return s;
    }

    /**
     * 删除公司地址的特殊符号
     *
     * @param s 原始输入
     * @return 返回删除符号后的字符串
     */
    public String deleteCompanyAddressSymbol(String s) {
        //需要保留的字符
        s = s.replaceAll("[^\'\\.\t&@#a-zA-Z0-9-]", " ");
        s = s.replaceAll(" {2,}", " ");
        s = s.trim();
        return s;
    }

    /**
     * 保留字母和数字其他全部删除
     *
     * @param s 原始输入
     * @return 慧慧只包含数字和字母的字符串
     */
    public String deleteAllSymbol(String s) {
        s = s.replaceAll("[^a-zA-Z0-9]", " ");
        s = s.replaceAll(" {2,}", " ");
        s = s.replaceAll(" And ", "");
        s = s.replaceAll(" AND ", "");
        s = s.replaceAll(" and ", "");
        s = s.replaceAll(" OR ", "");
        s = s.replaceAll(" Or ", "");
        s = s.replaceAll(" or ", "");
        s = s.trim();
        return s;
    }

    /**
     * 删除公司的后缀名称
     *
     * @param s 原始字符串
     * @return 删除INC和LLC结尾后面的东西
     */
    public String deleteSuffix(String s) {
       /* if (s.length() < 3) return s;
        for (int i = 0; i < s.length() - 3; i++) {
            if (s.substring(i, i + 3).toUpperCase().equals("INC") || s.substring(i, i + 3).toUpperCase().equals("LLC")) {
                s = s.substring(0, i + 3);
            }
        }
        s = s.trim();*/
        //正则表达式写法
        s=s.toUpperCase().replaceAll("(.*(INC|LLC))(.*)", "$1");
        return s;
    }

    /**
     * KMP
     *
     * @param s 原始字符串
     * @param p 模式字符串
     * @return s与p匹配的索引
     */
    public int KMP(String s, String p) {
        int i = 0;
        int j = 0;
        int k = -1;
        int sLen = s.length();
        int pLen = p.length();
        int[] next = new int[pLen];
        next[0] = -1;
        //因为只考虑前一个字符能匹配的最大前缀后缀 所以j < pLen - 1
        while (j < pLen - 1) {
            if (k == -1 || p.charAt(j) == p.charAt(k)) {
                k++;
                j++;
                next[j] = k;
            } else {
                k = next[k];
            }
        }
        j = 0;

        while (i < sLen && j < pLen) {
            if (j == -1 || s.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }

        if (j == pLen) return i - j;
        else return -1;
    }

    /**
     * 处理公司名错位到地址信息一栏的情况
     *
     * @param s 地址信息
     * @return true:有错位情况
     * false:无错位情况
     */
    public boolean FirmNameMess(String s) {
        s = s.toUpperCase();
        if (s != null && s != "") {
            if (KMP(s, "INC") != -1 || KMP(s, "LLC") != -1)
                return true;
            else
                return false;
        } else return false;
    }

    /**
     * 公共前缀
     *
     * @param s1 字符串1
     * @param s2 字符串2
     * @return 返回公共前缀所占的比例(分母是长度短的)
     */
    public double publicPrefix(String s1, String s2) {
        s1 = s1.replaceAll("\\. \'", "").toUpperCase();
        s2 = s2.replaceAll("\\. \'", "").toUpperCase();
        int count = 0;
        int len = (s1.length() > s2.length()) ? s2.length() : s1.length();
        for (int i = 0; i < len; i++) {
            if (s1.charAt(i) != s2.charAt(i)) break;
            count++;
        }
        return (double) count / len;
    }

    /**
     * 计算地址有几个公共项(以空格分隔的认为是项)
     *
     * @param add1 第一个地址
     * @param add2 第二个地址
     * @return 返回比例值(分母是项较少的地址字符串)，比例值越大越相似
     */
    public double addressPublicItems(String add1, String add2) {
        add1 = deleteCompanyAddressSymbol(add1).toUpperCase();
        add2 = deleteCompanyAddressSymbol(add2).toUpperCase();
        String[] add1s = add1.split(" ");
        String[] add2s = add2.split(" ");
        Map<String, Integer> map1 = new HashMap();
        Map<String, Integer> map2 = new HashMap();
        int minLen = Math.min(add1s.length, add2s.length);
        int count = 0;
        for (int i = 0; i < add1s.length; i++) {
            for (int j = 0; j < add2s.length; j++) {
                if (!map1.containsKey(add1s[i]) && !map2.containsKey(add2s[j]) &&
                        (add1s[i].equals(add2s[j]) || (add1s[i].length() > 1 && add2s[j].length() > 1 && strContain(add1s[i], add2s[j])))) {
                    map1.put(add1s[i], 1);
                    map2.put(add2s[j], 1);
                    count++;
                }
            }
        }
        return (double) count / minLen;
    }

    public int ld(String s, String t) {
        int d[][];
        int sLen = s.length();
        int tLen = t.length();
        int si;
        int ti;
        char ch1;
        char ch2;
        int cost;
        if (sLen == 0) {
            return tLen;
        }
        if (tLen == 0) {
            return sLen;
        }
        d = new int[sLen + 1][tLen + 1];
        for (si = 0; si <= sLen; si++) {
            d[si][0] = si;
        }
        for (ti = 0; ti <= tLen; ti++) {
            d[0][ti] = ti;
        }
        for (si = 1; si <= sLen; si++) {
            ch1 = s.charAt(si - 1);
            for (ti = 1; ti <= tLen; ti++) {
                ch2 = t.charAt(ti - 1);
                if (ch1 == ch2) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                d[si][ti] = Math.min(Math.min(d[si - 1][ti] + 1, d[si][ti - 1] + 1), d[si - 1][ti - 1] + cost);
            }
        }
        return d[sLen][tLen];
    }

    /**
     * 计算两个字符串的编辑距离
     *
     * @param src 源字符串
     * @param tar 目标字符串
     * @return 返回一个相似度的值(0至1范围内)
     */
    public double editSimilarity(String src, String tar) {
        int ld = ld(src.toUpperCase(), tar.toUpperCase());
        return 1 - (double) ld / Math.max(src.length(), tar.length());
    }

    /**
     * 将字符串转换成36维向量
     *
     * @param s 输入字符串
     * @return 36维度的响亮 每个值代表相应位置的字符出现的次数
     * @ps 将小写字母和大写字母映射到同一个维度上
     */
    public int[] str2vec(String s) {
        int[] vec = new int[36];
        if (s == null || s.equals("")) return vec;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 48 && c <= 57) {
                vec[26 + c - 48] += 1;
            } else if (c >= 65 && c <= 90) {
                vec[c - 65] += 1;
            } else if (c >= 97 && c <= 122) {
                vec[c - 97] += 1;
            } else {
            }
        }
        return vec;
    }

    /**
     * 将数字串转换成10维向量
     *
     * @param s 输入字符串
     * @return 返回数组
     */
    public int[] num2vec(String s) {
        int[] vec = new int[36];
        if (s == null || s.equals("")) return vec;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 48 && c <= 57) {
                vec[26 + c - 48] += 1;
            }
        }
        return vec;
    }

    /**
     * 将单词串转换成26维向量
     *
     * @param s 输入字符串
     * @return 返回数组
     */
    public int[] word2vec(String s) {
        int[] vec = new int[36];
        if (s == null || s.equals("")) return vec;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 65 && c <= 90) {
                vec[c - 65] += 1;
            } else if (c >= 97 && c <= 122) {
                vec[c - 97] += 1;
            } else {
            }
        }
        return vec;
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param v1 向量1
     * @param v2 向量2
     * @return 返回两个向量的余弦值
     */
    public double cosSimlarity(int[] v1, int[] v2) {
        double fenzi = 0;
        double fenmu = 0;
        double fenmu1 = 0;
        double fenmu2 = 0;
        for (int i = 0; i < 36; i++) {
            fenzi = v1[i] * v2[i] + fenzi;
            fenmu1 = fenmu1 + v1[i] * v1[i];
            fenmu2 = fenmu2 + v2[i] * v2[i];
        }
        fenmu = Math.sqrt(fenmu1) * Math.sqrt(fenmu2);
        if (fenmu == 0) return 0;
        else return fenzi / fenmu;
    }

    /**
     * 用src的信息补全tar的地址信息(针对表2清洗使用)
     *
     * @param src   源信息
     * @param tar   目标信息
     * @param index 需要补全的字段编号
     * @return 新的结果并用/t分割
     */
    public String exchangeInfo(String src, String tar, int[] index) {
        String[] srcs = src.split("\t");
        String[] tars = tar.split("\t");
        String res = "";
        int count = 0;
        for (int i = 0; i < tars.length; i++) {
            if (i < index.length && i == index[count]) {
                count++;
                tars[i] = srcs[i].trim();
            }
            res = res + tars[i].trim() + "\t";
        }
        return res;
    }

    /**
     * 判断两个字符串是否有包含关系(即一个串是否是另一个的子串)
     *
     * @param s1 串1
     * @param s2 串2
     * @return true记为有包含关系
     */
    public boolean strContain(String s1, String s2) {
        if (KMP(s1, s2) == -1 && KMP(s2, s1) == -1) return false;
        else return true;
    }

    /**
     * 判断两个地址的“字母”向量的包含关系
     *
     * @param s1 串1
     * @param s2 串2
     * @return 返回的百分比越大说明包含关系越紧密(越相似)
     */
    public double addressContain(String s1, String s2) {
        int[] v1 = word2vec(s1);
        int[] v2 = word2vec(s2);
        int sum1 = 0;
        int sum2 = 0;
        double sum = 0;
        for (int i = 0; i < 26; i++) {
            sum1 = sum1 + v1[i];
            sum2 = sum2 + v2[i];
            if ((v1[i] == 0 && v2[i] != 0) || (v2[i] == 0 && v1[i] != 0)) {
                sum = sum + Math.abs(v1[0] - v2[0]);
            }
        }
        return 1 - sum / Math.max(sum1, sum2);
    }


    /**
     * 对表1进行预处理，即将可能是同一个公司的信息进行合并
     *
     * @param src 源文件路径(待清洗) 包含id nme add(多个) 字段
     * @param tar 目标文件路径(清洗后) 包含id name add(多个) 字段a
     */
    public void merge1(String src, String tar) {

        Indexer indexer = new Indexer();

        try {
            File srcFile = new File(src);
            BufferedReader br = new BufferedReader(new FileReader(srcFile));
            String line = null;

            File tarFile = new File(tar);
            BufferedWriter bw = new BufferedWriter(new FileWriter(tarFile));
            int count = 1; //计数处理文件数
            int countFalse = 0; //计数错误文件数量

            while ((line = br.readLine()) != null) {
                //line 分别包含id name add(多个字段)

                int cut1 = line.indexOf("\t");
                String cutLine = line.substring(cut1 + 1);
                int cut2 = cutLine.indexOf("\t");

                //将表1的每条记录分割
                int id = Integer.parseInt(line.substring(0, cut1));
                String name = deleteSuffix(deleteCompanyNameSymbol(cutLine.substring(0, cut2)));
                String address = deleteCompanyAddressSymbol(cutLine.substring(cut2 + 1));

                //清洗后的结果
                String newName = name;
                String newAddress = address;

                //用line查询表1倒排索引的结果
                List<String> searchRes = indexer.searchIndex(deleteAllSymbol(line), 20, 1);

                //处理地址错位项问题
                if (newAddress.length() > 2 && newAddress.charAt(0) == '.') {
                    newAddress = newAddress.substring(2);
                }

                //根据查询结果来清洗每条记录
                for (int i = 0; i < searchRes.size(); i++) {
                    String tmpLine = searchRes.get(i);
                    int tmpCut1 = tmpLine.indexOf("\t");
                    String tmpCutLine = tmpLine.substring(tmpCut1 + 1);
                    int tmpCut2 = tmpCutLine.indexOf("\t");

                    //将查询结果进行分割
                    String tmpName = deleteSuffix(deleteCompanyNameSymbol(tmpCutLine.substring(0, tmpCut2)));
                    String tmpAddress = null;

                    //注意处理地址为空的异常项目
                    if (tmpCut2 != -1) tmpAddress = deleteCompanyAddressSymbol(tmpLine.substring(tmpCut2 + 1));

                    //如果地址第一位是.  即存在错位现象，先去除错位
                    if (tmpAddress.length() > 2 && tmpAddress.charAt(0) == '.' && tmpAddress.charAt(1) == '\t') {
                        tmpAddress = tmpAddress.substring(2);
                    }

                    //以下是三条判断是否是同一家公司的三条标准

                    //如果：名字相同或者余弦非常相近(0.98) && 地址余弦比较相似(0.96)或者地址向量存在包含关系(85%) 则认为是同一家公司
                    if ((newName.replaceAll("[^a-zA-Z0-9 ]", "").equals(tmpName.replaceAll("[^a-zA-Z0-9 ]", "")) || cosSimlarity(word2vec(newName), word2vec(tmpName)) > 0.98)
                            && (cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.96 || addressContain(newAddress, tmpAddress) > 0.85)) {

                        //用长的替换短的(考虑后缀的问题 后缀不应该算在名字的长度内)
                        newName = (tmpName.length() > newName.length()) ? tmpName : newName;
                        newAddress = (tmpAddress.length() > newAddress.length()) ? tmpAddress : newAddress;
                    }

                    //如果：地址余弦比较相似(0.96) && 公共前缀比例大于50%或者公司名字的编辑距离大于0.5
                    if ((cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.96)
                            && (publicPrefix(newName, tmpName) > 0.5 || editSimilarity(newName.replaceAll(" \\.", ""), tmpName.replaceAll(" \\.", "")) > 0.5)) {

                        newName = (tmpName.length() > newName.length()) ? tmpName : newName;
                        newAddress = (tmpAddress.replaceAll("[^a-zA-Z0-9]", "").length() > newAddress.replaceAll("[^a-zA-Z0-9]", "").length()) ? tmpAddress : newAddress;

                        //如果：(公司名公共前缀相似度大于50%或者公司名编辑距离大于0.6) &&
                        //     ( (地址余弦相似大于0.94 && 地址的公共项超过50%) || (地址余弦相似大于0.9_这个值其实很低 && 地址的公共项超过70%) )
                    } else if ((publicPrefix(newName, tmpName) > 0.5 || editSimilarity(newName.replaceAll(" \\.", ""), tmpName.replaceAll(" \\.", "")) > 0.6)
                            && ((cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.94 && addressPublicItems(newAddress, tmpAddress) >= 0.5) ||
                            (cosSimlarity(str2vec(newAddress), str2vec(tmpAddress)) > 0.9 || addressPublicItems(newAddress, tmpAddress) > 0.7))) {

                        newName = (tmpName.length() > newName.length()) ? tmpName : newName;
                        newAddress = (tmpAddress.replaceAll("[^a-zA-Z0-9]", "").length() > newAddress.replaceAll("[^a-zA-Z0-9]", "").length()) ? tmpAddress : newAddress;
                    }
                }

                //判断这条记录是否被清洗过
                if (!name.equalsIgnoreCase(newName) || !address.equalsIgnoreCase(newAddress)) {
                    countFalse++;
                }

                //清洗后的结果写入新文件
                bw.write(id + "\t" + newName + "\t" + newAddress + "\n");
                System.out.println(count++ + ":" + newName);
            }
            System.out.println("处理了" + --count + "条记录,\t" + "总共错误了：" + countFalse);
            bw.flush();
            bw.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对表2进行预处理，即将可能是同一个公司的信息进行合并
     *
     * @param src 源文件路径 包含公司名和全地址两个字段
     * @param tar 目标文件路径
     */
    public void merge2(String src, String tar) {
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
    }

    /**
     * 用表1清洗后的结果查询表2返回一个最相似的公司名 加一个新字段在原市公司明后，作为参考
     *
     * @param src 源文件路径 包含id name add(多个) 字段
     * @param tar 目标文件路径 包含id name 新增表2忠相似公司名 add(多个) 这些字段
     */
    public void clean(String src, String tar) {

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
    }

}