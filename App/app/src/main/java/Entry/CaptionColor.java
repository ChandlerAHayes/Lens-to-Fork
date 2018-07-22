package Entry;

public enum CaptionColor {
    // defines the color of the caption for a specific entry
    WHITE ("white"),
    BLACK ("black");

    private String value;

    CaptionColor(String colorValue){
        this.value = colorValue;
    }

    public String getColorValue(){
        return value;
    }
}
