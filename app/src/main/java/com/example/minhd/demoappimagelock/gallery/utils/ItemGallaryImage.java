package com.example.minhd.demoappimagelock.gallery.utils;

import java.io.Serializable;


public class ItemGallaryImage extends ItemGallary implements Serializable {
    private PairInt mPairInt;


    public ItemGallaryImage(PairInt pairInt, String pathFile, long id) {
        super(pathFile, id);
        mPairInt = pairInt;
    }

    public ItemGallaryImage(String pathFile, long id) {
        super(pathFile, id);
    }

    public ItemGallaryImage(PairInt pairInt, String pathFile, String pathThumnail, long id) {
        super(pathFile, pathThumnail, id);
        mPairInt = pairInt;
    }

    public PairInt getPairInt() {
        return mPairInt;
    }
}
