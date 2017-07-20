package com.wxy.House.HouseOwnerRecord;




import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 10/11/2016.
 */
public class DescriptionDisplay {

    public static String toStringValue(DescriptionDisplay descriptionDisplay){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(descriptionDisplay);
        } catch (IOException e) {
            throw new IllegalArgumentException("display json data gen fail!",e);
        }
    }

    public static DescriptionDisplay instance(String data){
        DescriptionDisplay descriptionDisplay;
        if (data == null){
            descriptionDisplay = new DescriptionDisplay();
        }else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                descriptionDisplay = mapper.readValue(data,DescriptionDisplay.class);
            } catch (IOException e) {
                //Logging.getLog(DescriptionDisplay.class).warn(data + " is not a json data!");
                descriptionDisplay = new DescriptionDisplay();
            }
        }
        return descriptionDisplay;
    }


    public enum DisplayStyle{
        NORMAL,LABEL,TEXT,PARAGRAPH,IMPORTANT,DECORATE
    }

    public static class DisplayData{

        public DisplayData() {
        }

        public DisplayData(DisplayStyle displayStyle, String value) {
            this.displayStyle = displayStyle;
            this.value = value;
        }

        private DisplayStyle displayStyle;
        private String value;

        public DisplayStyle getDisplayStyle() {
            return displayStyle;
        }

        public void setDisplayStyle(DisplayStyle displayStyle) {
            this.displayStyle = displayStyle;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static class DataLine{

        public DataLine() {
        }

        public DataLine(DisplayStyle displayStyle) {
            this.displayStyle = displayStyle;
        }

        public DataLine(DisplayStyle displayStyle, List<DisplayData> displayDatas) {
            this.displayStyle = displayStyle;
            this.displayDatas = displayDatas;
        }

        private DisplayStyle displayStyle;
        private List<DisplayData> displayDatas = new ArrayList<DisplayData>();

        public DisplayStyle getDisplayStyle() {
            return displayStyle;
        }

        public void setDisplayStyle(DisplayStyle displayStyle) {
            this.displayStyle = displayStyle;
        }

        public List<DisplayData> getDisplayDatas() {
            return displayDatas;
        }

        public void setDisplayDatas(List<DisplayData> displayDatas) {
            this.displayDatas = displayDatas;
        }

    }

    private List<DataLine> dataLines = new ArrayList<DataLine>();

    public List<DataLine> getDataLines() {
        return dataLines;
    }

    public void setDataLines(List<DataLine> dataLines) {
        this.dataLines = dataLines;
    }

    public boolean addLine(DisplayStyle lineStyle, DisplayData... data){
        DataLine line = new DataLine(lineStyle);
        for(DisplayData d: data){
            line.getDisplayDatas().add(d);
        }
        return dataLines.add(line);
    }

    private DataLine curLine;

    public DataLine newLine(DisplayStyle lineStyle){
        curLine = new DataLine(lineStyle);
        dataLines.add(curLine);
        return curLine;
    }

    public DataLine addData(DisplayStyle displayStyle, String value){
        if (curLine != null){
            curLine.getDisplayDatas().add(new DisplayData(displayStyle,value));
            return curLine;
        }else{
            throw new IllegalArgumentException("not in line");
        }
    }

}
