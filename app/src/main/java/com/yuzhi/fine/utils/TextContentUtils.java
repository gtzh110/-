package com.yuzhi.fine.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by lemon on 2016/3/29.
 */
public class TextContentUtils {
    public static void setTextWatcherWithoutEmoji(final EditText child) {
        //防止录入输入法表情
        child.addTextChangedListener(new TextWatcher() {
            //匹配非表情符号的正则表达式
            private final String reg = "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";

            private Pattern pattern = Pattern.compile(reg);
            //输入表情前的光标位置
            private int cursorPos;
            //输入表情前EditText中的文本
            private String tmp;
            //   		 //是否重置了EditText的内容
            private boolean resetText;
            private int selection;

            private boolean isEmojiCharacter(char codePoint) {
                return !((codePoint == 0x0) ||
                        (codePoint == 0x9) ||
                        (codePoint == 0xA) ||
                        (codePoint == 0xD) ||
                        ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                        ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                        ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (arg3 > 0) {//表情符号的字符长度最小为3
                    //提取输入的长度大于3的文本
                    try {
                        int currentSelection = ((EditText) child).getSelectionStart();
                        if (currentSelection > 0) {
                            char input = arg0.charAt(currentSelection - 1);


                            //正则匹配是否是表情符号
                            //   								     Matcher matcher = pattern.matcher(input.toString());
                            if (isEmojiCharacter(input)) {
                                resetText = true;
                                //是表情符号就将文本还原为输入表情符号之前的内容
                                ((EditText) child).setText(tmp);
                                if (selection > 0) {
                                    ((EditText) child).setSelection(selection);
                                }
                                ((EditText) child).invalidate();
                            } else {
                                resetText = false;
                            }
                        }
                    } catch (Exception e) {
                        //return;
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                //   			cursorPos = ((EditText) child).getSelectionEnd();
                //   		    tmp = arg0.toString();//这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
                if (!resetText) {
                    cursorPos = ((EditText) child).getSelectionEnd();
                    tmp = arg0.toString();//这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
                    selection = ((EditText) child).getSelectionStart();
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
    }
}
