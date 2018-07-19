package picture.diary.lenstofork.Utils;

/**
 * Stores dimensions of View sizes so that images can be a percentage of a view such as the
 * having a max height of 50% of the height in the DiaryFragment view.
 */
public class Dimensions {
    private static Dimensions instance;

    // DiaryFragment dimensions
    private int diaryWidth = -1;
    private int diaryHeight = -1;

    // DetailFragment Dimensions
    private int detailWidth = -1;
    private int detailHeight = -1;

    public static Dimensions getInstance(){
        if(instance == null) {
            instance = new Dimensions();
        }
        return instance;
    }

    //-------- Setters & Getters
    // Diary Fragment Dimensions
    public int getDiaryWidth() {
        return diaryWidth;
    }

    public void setDiaryWidth(int diaryWidth) {
        this.diaryWidth = diaryWidth;
    }

    public int getDiaryHeight() {
        return diaryHeight;
    }

    public void setDiaryHeight(int diaryHeight) {
        this.diaryHeight = diaryHeight;
    }

    public int getDetailWidth() {
        return detailWidth;
    }

    public void setDetailWidth(int detailWidth) {
        this.detailWidth = detailWidth;
    }

    public int getDetailHeight() {
        return detailHeight;
    }

    public void setDetailHeight(int detailHeight) {
        this.detailHeight = detailHeight;
    }
}
