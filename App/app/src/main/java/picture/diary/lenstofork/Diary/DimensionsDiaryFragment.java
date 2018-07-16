package picture.diary.lenstofork.Diary;

public class DimensionsDiaryFragment {
    private static DimensionsDiaryFragment instance;
    private int width;
    private int height;

    private DimensionsDiaryFragment(){

    }

    public static DimensionsDiaryFragment getInstance(){
        if(instance == null) {
            instance = new DimensionsDiaryFragment();
        }
        return instance;
    }

    //-------- Setters & Getters
    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}
