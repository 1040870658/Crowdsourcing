package hk.hku.yechen.crowdsourcing.model;

import android.content.res.Resources;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.R;

/**
 * Created by yechen on 2017/12/27.
 */

/**
 * DecoratorModel is a essential part of the hover recyclerView
 */

public class DecoratorModel {
    private int groupNum = 2;
    private List<Integer> groupPositions;
    private List<String> groupText;
    private List<Integer> groupColor;
    private List<Integer> groupTextColor;
    private List<List> datas;
    private Resources resources;
    private String hoverText;
    private int previousHoverGroup;
    private int currentHoverGroup;
    public static final int HOVER_HEIGHT = 60;
    public static final int NORMAL_HEIGHT = 60;
    public static final int HOVER_TEXT_TOP = 40;
    public static final int PADDING_LEFT = 10;
    public static final int PADDING_TOP = 10;
    public static final int TEXT_SIZE = 30;
    public static final int TEXT_TOP = 20;

    public DecoratorModel(){

    }

    public int getGroupNum() {
        return groupNum;
    }

    public List<Integer> getGroupColor() {
        return groupColor;
    }

    public List<String> getGroupText() {
        return groupText;
    }

    public List<Integer> getGroupTextColor() {
        return groupTextColor;
    }

    public List<List> getDatas() {
        return datas;
    }

    public Resources getResources() {
        return resources;
    }

    public int getCurrentHoverGroup() {
        return currentHoverGroup;
    }

    public void setGroupPositions(List<Integer> groupPositions) {
        this.groupPositions = groupPositions;
    }

    public void setGroupText(List<String> groupText) {
        this.groupText = groupText;
    }

    public void setGroupColor(List<Integer> groupColor) {
        this.groupColor = groupColor;
    }

    public void setDatas(List<List> datas) {
        this.datas = datas;
    }

    public void setGroupTextColor(List<Integer> groupTextColor) {
        this.groupTextColor = groupTextColor;
    }

    public void setHoverText(String hoverText) {
        this.hoverText = hoverText;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void notifyDataSetChanged(){
        init(resources,datas);
    }
    public void notifyDataSetChanged(List<String> groupText){
        this.groupText = groupText;
        init(resources,datas);
    }
    private void init(Resources resources, List<List> datas){
        this.datas = datas;
        this.groupNum = datas.size();
        this.resources = resources;
        groupPositions = new ArrayList<Integer>(groupNum);
        groupColor = new ArrayList<Integer>(groupNum);
        groupTextColor = new ArrayList<Integer>(groupNum);
        groupPositions.add(0);
        int totalPos = 0;
        for(int i = 0;i != groupNum;i ++){
            if(i != 0){
                totalPos += datas.get(i - 1).size();
                groupPositions.add(totalPos);
                //groupPositions[i] = 6;
            }
            groupColor.add(resources.getColor(R.color.light_gray));
            groupTextColor.add(resources.getColor(R.color.orange));
        }
    }
    public DecoratorModel(Resources resources, List<List> datas, List<String> groupText ){
        init(resources,datas);
        this.groupText = groupText;
        if (groupText.size() > 0) {
            hoverText = groupText.get(0);
        }
        else {
            hoverText = "";
        }
        previousHoverGroup = 0;
        currentHoverGroup = 0;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }

    public String getHoverText(){
        return hoverText;
    }
    public void setHoverText(int group){
        if(groupText.size() == 0)
            return;
        previousHoverGroup = currentHoverGroup - 1;
        if(previousHoverGroup < 0)
            previousHoverGroup = 0;
        currentHoverGroup = group;
        hoverText = groupText.get(group);
    }
    public String getPreviousHoverText(){
        return groupText.get(previousHoverGroup);
    }
    public int getPreviousHoverGroup(){
        return previousHoverGroup;
    }


    public boolean isFirst(int pos){
        for(int i = 0;i < groupPositions.size();i ++){
            if(pos == groupPositions.get(i))
                return true;
        }
        return false;
    }
    public List getGroupPositions() {
        return groupPositions;
    }

    public int getGroup(int position){
        int length = 0;
        for(int i = 0;i < groupNum;i ++){
            length += datas.get(i).size();
            if(position < length){
                return i;
            }
        }
        return 0;
    }
}
