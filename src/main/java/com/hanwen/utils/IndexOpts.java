package com.hanwen.utils;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Created by Administrator on 2016/1/16.
 */
public class IndexOpts {
    public static FieldType fieldType_stored=null;
    public static FieldType fieldType_stored_indexed=null;
    public static FieldType fieldType_analyzed_stored_indexed=null;
    static {
        fieldType_stored=new FieldType();
        fieldType_stored.setTokenized(false);
        fieldType_stored.setStored(true);

        fieldType_stored_indexed= new FieldType();
        fieldType_stored_indexed.setStored(true);
        fieldType_stored_indexed.setTokenized(false);
        fieldType_stored_indexed.setIndexOptions(IndexOptions.DOCS_AND_FREQS);

        fieldType_analyzed_stored_indexed= new FieldType();
        fieldType_analyzed_stored_indexed.setStored(true);
        fieldType_analyzed_stored_indexed.setTokenized(true);
        fieldType_analyzed_stored_indexed.setIndexOptions(IndexOptions.DOCS);
    }
}
