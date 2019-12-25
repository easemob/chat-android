package com.hyphenate.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;

import java.util.ArrayList;

public class EaseUser implements Parcelable {
    /**
     * \~chinese
     * 此用户的唯一标示名, 即用户的环信id
     *
     * \~english
     * the user name assigned from app, which should be unique in the application
     */
    private String username;
    private String nick;
    /**
     * initial letter from nickname
     */
    private String initialLetter;
    /**
     * user's avatar
     */
    private String avatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nick == null ? username : nick;
    }

    public void setNickname(String nick) {
        this.nick = nick;

    }

    public String getInitialLetter() {
        if(initialLetter == null) {
            if(!TextUtils.isEmpty(nick)) {
                return getInitialLetter(nick);
            }
            return getInitialLetter(username);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInitialLetter(String name) {
        return new GetInitialLetter().getLetter(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
    }

    public EaseUser() {
    }

    public static final Parcelable.Creator<EaseUser> CREATOR = new Parcelable.Creator<EaseUser>() {

        @Override
        public EaseUser createFromParcel(Parcel source) {
            return new EaseUser(source);
        }

        @Override
        public EaseUser[] newArray(int size) {
            return new EaseUser[size];
        }
    };

    private EaseUser(Parcel in) {
        username = in.readString();
    }

    public class GetInitialLetter {
        private String defaultLetter = "#";

        /**
         * 获取首字母
         * @param name
         * @return
         */
        public String getLetter(String name) {
            if(TextUtils.isEmpty(name)) {
                return defaultLetter;
            }
            char char0 = name.toLowerCase().charAt(0);
            if(Character.isDigit(char0)) {
                return defaultLetter;
            }
            ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
            if(l != null && !l.isEmpty() && l.get(0).target.length() > 0) {
                HanziToPinyin.Token token = l.get(0);
                String letter = token.target.substring(0, 1).toUpperCase();
                char c = letter.charAt(0);
                if(c < 'A' || c > 'Z') {
                    return defaultLetter;
                }
                return letter;
            }
            return defaultLetter;
        }
    }

}
