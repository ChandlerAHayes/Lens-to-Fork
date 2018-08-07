package picture.diary.lenstofork.Diary.Entry;

import android.graphics.Color;

public enum CaptionColor {
    // defines the color of the caption for a specific entry
    WHITE ("white", Color.parseColor("#FFFFFF")),
    BLACK ("black", Color.parseColor("#000000")),
    BLUE_GREY ("blue grey", Color.parseColor("#5E7C8B")),
    GREY ("grey", Color.parseColor("#9D9D9D")),
    DEEP_ORANGE ("deep orange", Color.parseColor("#FC6F2D")),
    ORANGE ("orange", Color.parseColor("#FF9800")),
    MUSTARD ("mustard", Color.parseColor("#F0BE29")),
    YELLOW ("yellow", Color.parseColor("#FFEC16")),
    LIME ("lime", Color.parseColor("#BBF029")),
    LIGHT_GREEN ("light green", Color.parseColor("#88C440")),
    GREEN ("green", Color.parseColor("#3DA641")),
    TEAL("teal", Color.parseColor("#00B8A5")),
    BLUE("blue", Color.parseColor("#1093F5")),
    DEEP_BLUE ("deep blue", Color.parseColor("#1355D1")),
    INDIGO ("indigo", Color.parseColor("#3D4DB7")),
    DEEP_PURPLE ("deep purple", Color.parseColor("#6633B9")),
    PURPLE ("purple", Color.parseColor("#9C1AB1")),
    PINK ("pink", Color.parseColor("#EB1460")),
    RED ("red", Color.parseColor("#FF0000")),
    MAROON("maroon", Color.parseColor("#870101"));

    private String value;
    private int color;

    CaptionColor(String colorValue, int color){
        this.value = colorValue;
        this.color = color;
    }

    CaptionColor(String colorValue){
        this.value = colorValue;

    }

    public String getColorString(){
        return value;
    }

    public int getColor (){
        return color;
    }

    public static CaptionColor getCaptionColor(String colorString){
        switch (colorString){
            case "white":
                return WHITE;
            case "black":
                return BLACK;
            case "blue grey":
                return BLUE_GREY;
            case "grey":
                return GREY;
            case "deep orange":
                return DEEP_ORANGE;
            case "orange":
                return ORANGE;
            case "mustard":
                return MUSTARD;
            case "yellow":
                return YELLOW;
            case "lime":
                return LIME;
            case "light green":
                return LIGHT_GREEN;
            case "green":
                return GREEN;
            case "teal":
                return TEAL;
            case "blue":
                return BLUE;
            case "deep blue":
                return DEEP_BLUE;
            case "indigo":
                return INDIGO;
            case "deep purple":
                return DEEP_PURPLE;
            case "purple":
                return PURPLE;
            case "pink":
                return PINK;
            case "red":
                return RED;
            case "maroon":
                return MAROON;
            default:
                return WHITE;
        }
    }

    public static CaptionColor getCaptionColor(int color){
        if(color == WHITE.color){
            return WHITE;
        }
        else if(color == BLACK.color){
            return BLACK;
        }
        else if(color == BLUE_GREY.color){
            return BLUE_GREY;
        }
        else if(color == GREY.color){
            return GREY;
        }
        else if(color == DEEP_ORANGE.color){
            return DEEP_ORANGE;
        }
        else if(color == ORANGE.color){
            return ORANGE;
        }
        else if(color == YELLOW.color){
            return YELLOW;
        }
        else if(color == MUSTARD.color){
            return MUSTARD;
        }
        else if(color == LIME.color){
            return LIME;
        }
        else if(color == LIGHT_GREEN.color){
            return LIGHT_GREEN;
        }
        else if(color == GREEN.color){
            return GREEN;
        }
        else if(color == TEAL.color){
            return TEAL;
        }
        else if(color == BLUE.color){
            return BLUE;
        }
        else if(color == DEEP_BLUE.color){
            return DEEP_BLUE;
        }
        else if(color == INDIGO.color){
            return INDIGO;
        }
        else if(color == DEEP_PURPLE.color){
            return DEEP_PURPLE;
        }
        else if(color == PURPLE.color){
            return PURPLE;
        }
        else if(color == PINK.color){
            return PINK;
        }
        else if(color == RED.color){
            return RED;
        }
        else if(color == MAROON.color){
            return MAROON;
        }
        else{
            return WHITE;
        }
    }
}
