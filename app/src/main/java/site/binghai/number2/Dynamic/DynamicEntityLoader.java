package site.binghai.number2.Dynamic;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DynamicEntityLoader {
    void loadDynamicData(ArrayList<DynamicContentEntity> entities);

    void loadFailed();
}
