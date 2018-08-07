package picture.diary.lenstofork.Diary.Entry;

public enum CaptionPosition {
    TOP_LEFT ("Top Left"),
    TOP_CENTER ("Top Center"),
    TOP_RIGHT ("Top Right"),
    LEFT_CENTER ("Left Center"),
    CENTER ("Center"),
    RIGHT_CENTER ("Right Center"),
    BOTTOM_LEFT ("Bottom Left"),
    BOTTOM_CENTER ("Bottom Center"),
    BOTTOM_RIGHT ("Bottom Right");

    private String value;

    CaptionPosition(String position){
        this.value = position;
    }

    public String getValue(){
        return value;
    }

    public static CaptionPosition getCaptionPosition(String position){
        switch(position){
            case "Top Left":
                return TOP_LEFT;
            case "Top Center":
                return TOP_CENTER;
            case "Top Right":
                return TOP_RIGHT;
            case "Left Center":
                return LEFT_CENTER;
            case "Center":
                return CENTER;
            case "Right Center":
                return RIGHT_CENTER;
            case "Bottom Left":
                return BOTTOM_LEFT;
            case "Bottom Center":
                return BOTTOM_CENTER;
            case "Bottom Right":
                return BOTTOM_RIGHT;
            default:
                return CENTER;
        }
    }
}
