package com.hyphenate.chatdemo.common.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class RecyclerViewUtils {
    /**
     * Get the first and last visible position of RecyclerView
     * @param rvList
     * @return
     */
    public static int[] rangeMeasurement(RecyclerView rvList){
        int[] range = new int[2];
        RecyclerView.LayoutManager manager = rvList.getLayoutManager();
        if (manager instanceof LinearLayoutManager){
            range = finRangeLinear((LinearLayoutManager)manager);
        }else if (manager instanceof StaggeredGridLayoutManager){
            range = finRangeStaggeredGrid((StaggeredGridLayoutManager)manager);
        }
        return range;
    }

    private static int[] finRangeLinear(LinearLayoutManager manager){
        int[] range = new int[2];
        range[0] = manager.findFirstVisibleItemPosition();
        range[1] = manager.findLastVisibleItemPosition();
        return range;
    }

    private static int[] finRangeStaggeredGrid(StaggeredGridLayoutManager manager){
        int[] startPos = new int[manager.getSpanCount()];
        int[] endPos = new int[manager.getSpanCount()];
        manager.findFirstVisibleItemPositions(startPos);
        manager.findLastVisibleItemPositions(endPos);
        return finRange(startPos,endPos);
    }

    private static int[] finRange(int[] startPos,int[] endPos){
        int start = startPos[0];
        int end = endPos[0];
        for (int i = 1; i < startPos.length; i++) {
            if (start > startPos[i]){
                start = startPos[i];
            }
        }
        for (int i = 1; i < endPos.length; i++) {
            if (end < endPos[i]){
                end = endPos[i];
            }
        }
        return new int[]{start,end};
    }
}
