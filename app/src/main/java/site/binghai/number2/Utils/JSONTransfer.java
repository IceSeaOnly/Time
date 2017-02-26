package site.binghai.number2.Utils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/25.
 * fastjson与原生json组件的转换
 */
public class JSONTransfer {
    public static com.alibaba.fastjson.JSONObject toFastJson(JSONObject object){
        return JSON.parseObject(object.toString());
    }

    public static com.alibaba.fastjson.JSONObject toFastJson(String object){
        return JSON.parseObject(object);
    }
}
