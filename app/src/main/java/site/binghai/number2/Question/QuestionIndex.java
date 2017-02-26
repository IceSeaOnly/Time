package site.binghai.number2.Question;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import site.binghai.number2.Index.MainActivity;
import site.binghai.number2.R;

public class QuestionIndex extends AppCompatActivity {
    private ListView question_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_index);
        question_listview = (ListView) findViewById(R.id.question_listview);
        ArrayList<QuestionEntity> questions = new ArrayList<>();
        questions.add(simple1());
        questions.add(simple2());
        questions.add(simple3());
        QuestionAdapter questionAdapter = new QuestionAdapter(questions,this);
        question_listview.setAdapter(questionAdapter);
    }

    private QuestionEntity simple1(){
        ArrayList<String>comment_names = new ArrayList<>();
        comment_names.add("用户1");
        comment_names.add("用户2");
        ArrayList<String> comments = new ArrayList<>();
        comments.add("评论1");
        comments.add("评论2");
        return new QuestionEntity(0L,
                "avatar",
                "这里是问题id为0的问题的主要内容，由代码动态实现",
                "lefeIma","centerIma","rightImg",
                1,2,3,
                comment_names,comments);
    }
    private QuestionEntity simple2(){
        ArrayList<String>comment_names = new ArrayList<>();
        comment_names.add("用户3");
        ArrayList<String> comments = new ArrayList<>();
        comments.add("评论3");
        return new QuestionEntity(1L,
                "avatar",
                "这里是问题id为1的问题的主要内容，由代码动态实现",
                "lefeIma","centerIma","rightImg",
                4,5,6,
                comment_names,comments);
    }

    private QuestionEntity simple3(){
        ArrayList<String>comment_names = new ArrayList<>();
        comment_names.add("用户4");
        comment_names.add("用户5");
        comment_names.add("用户6");
        ArrayList<String> comments = new ArrayList<>();
        comments.add("评论4");
        comments.add("评论5");
        comments.add("评论6");
        return new QuestionEntity(1L,
                "avatar",
                "这里是问题id为1的问题的主要内容，由代码动态实现",
                "lefeIma","centerIma","rightImg",
                4,5,6,
                comment_names,comments);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
