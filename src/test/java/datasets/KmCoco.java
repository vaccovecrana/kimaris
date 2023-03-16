package datasets;

import java.util.List;

public class KmCoco {

  public static class Category {
    public int id;
    public String name;
  }

  public static class Image {
    public int id;
    public String file_name;
    public int height, width;
  }

  public static class Annotation {
    public int id;
    public int image_id;
    public int category_id;
    public double[] bbox;
  }

  public List<Category> categories;
  public List<Image> images;
  public List<Annotation> annotations;

}
