package site.binghai.number2.Utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/8/27.
 */
public class Config {
    public static String service_root = "http://timeserver.haidaoo.com/";
    public static String IndexSlide = service_root+"index_slide.do";
    public static String LoginServer = service_root+"normal_login.do";
    public static String AntoLoginServer = service_root+"auto_login.do";
    public static String RegServer = service_root+"user_reg.do";
    public static String ChangeAvatar = service_root+"change_avatar.do";
    public static String defaultAvatar = "http://time01-10066161.image.myqcloud.com/c0e3698d-749e-4a93-98f9-9ab09dc37b2a";
    public static String niuren = service_root+"niuren_list.do";
    public static String queryWelcomeAd = service_root+"welcomead.do";
    public static String postMsg = service_root+"send_msg_to_friend.do";
    public static String QuerySystemMsg = service_root+"query_system_message.do";
    public static String PIC_SIGN_SERVER = "http://115.159.44.148/TimeImageUploadSignServer/sign";
    public static String SystemMsgServer = service_root+"system_msg_server.do";
    public static String SearchFriend = service_root+"search_friend.do";
    public static String UpdateUserDeviceId = service_root+"update_user_deviceid.do";
    public static String DownLoadLastestTreeHoleVoice = service_root+"download_lastest_tree_hole_voice.do";
    public static String IHaveReceivedMsg = service_root+"i_have_received_msg.do";
    public static String postNewDynamic = service_root+"post_new_dynamic.do";

    public static String commonPostComment = service_root+"post_comment.do";
    public static String commonPeiZanOpt = service_root+"pei_zan_opt.do";

    public static String VOICE_SIGN_SERVER = "http://115.159.44.148/TimeVoiceUploadSignServer/sign";
    public static String ali_securityKey = "3f2c08cb-495c-4cb4-8e5e-71364f5a474b";
    public static String UpdateTreeHoleFace = service_root+"images/tree_hole_face.png";
    public static String UpdateShowYourLoveFace = service_root+"images/show_your_love_face.png";
    public static String refreshAllFriends = service_root+"refresh_all_friends.do";
    public static String APP_NET_CONNECT = service_root+"app_net_connected.do";
    public static String editMyInfo = service_root+"my_infos.do";
    public static String my_incomes = service_root+"my_incomes.do";
    public static String getDynamicIndex = service_root+"get_dynamic_list.do";
    public static String getDynamicEntities = service_root+"get_dynamic_list_entity.do";
    public static String CallBackChatMsg = service_root+"callback_chatmsg.do";
    public static String postNewShowYourLove = service_root+"post_new_show_your_love.do";
    public static String getNextShowYourLove = service_root+"get_next_page_show_your_love.do";
    public static String Report_JuBao = service_root+"jubao.do";
    public static String suggestion = "http://image.haidaoo.com/v-U711RE9UR3";
    public static String DeleteEntity = service_root+"delete_entity.do";
    public static String getComments = service_root+"get_comments.do";
    public static String DeleteComment = service_root+"del_comment.do";
    public static String LoveSkin = service_root+"love_skin.do";

    /**
     * 用于签名要求不强的随机签名
     * */
    public static String randomSign() {
        String datas = String.valueOf(System.currentTimeMillis())+"Time_UP_RANDOM";
        String SIGN = SignUtil.getSign(datas);
        Log.i("随机签名:","data="+datas+",sign="+SIGN);
        return "?data="+datas+"&SIGN="+SIGN;
    }

    public static void setServerRoot(String s) {
        service_root = s;
    }
}
