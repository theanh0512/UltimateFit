package ultimate.fit.ultimatefit.api;

import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 17/8/17.
 */

public class CategoryApiResponse {
    private String model;
    private int pk;
    private Category fields;

    public CategoryApiResponse(Category fields) {
        this.fields = fields;
    }

    public String getModel() {
        return model;
    }

    public int getPk() {
        return pk;
    }

    public Category getFields() {
        return fields;
    }
}
