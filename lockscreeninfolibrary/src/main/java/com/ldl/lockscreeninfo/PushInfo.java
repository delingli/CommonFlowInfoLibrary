package com.ldl.lockscreeninfo;

import java.io.Serializable;

/**
 * create by ldl2018/8/23 0023
 */

public class PushInfo implements Serializable {
    /*    {

            "show": true,                           //是否展示这条push数据；默认都为true，走原有逻辑，false 去服务器拉去替换
                "push": {
            "data": {
                "title": "一键换机",
                        "content": "发现手机有新的照片，请做好备份！",
                        "action": "com.stkj.onekey.action.TRANSFER_VIA_SDCARD"
            },
            "le": "true",
                    "dp": "dpl",
                    "html": "html",
                    "ishandle": true,
                    "message": "内容"
        }
        }*/
    private boolean show;
    private PushEntiry push;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public PushEntiry getPush() {
        return push;
    }

    public void setPush(PushEntiry push) {
        this.push = push;
    }

    static class PushEntiry implements Serializable {
        private String le;
        private String dp;
        private String html;
        private boolean ishandle;
        private String message;
        private PushData data;

        public PushData getData() {
            return data;
        }

        public void setData(PushData data) {
            this.data = data;
        }

        public PushEntiry() {
        }

        public PushEntiry(String le, String dp, String html, boolean ishandle, String message) {
            this.le = le;
            this.dp = dp;
            this.html = html;
            this.ishandle = ishandle;
            this.message = message;
        }

        public String getLe() {
            return le == null ? "" : le;
        }

        public void setLe(String le) {
            this.le = le;
        }

        public String getDp() {
            return dp == null ? "" : dp;
        }

        public void setDp(String dp) {
            this.dp = dp;
        }

        public String getHtml() {
            return html == null ? "" : html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public boolean isIshandle() {
            return ishandle;
        }

        public void setIshandle(boolean ishandle) {
            this.ishandle = ishandle;
        }

        public String getMessage() {
            return message == null ? "" : message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    static class PushData implements Serializable {
        private String title;
        private String content;
        private String action;

        public PushData(String title, String content, String action) {
            this.title = title;
            this.content = content;
            this.action = action;
        }

        public PushData() {
        }

        public String getTitle() {
            return title == null ? "" : title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content == null ? "" : content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAction() {
            return action == null ? "" : action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

}
