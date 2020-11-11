package com.hyphenate.easeui.provider;

import com.hyphenate.easeui.domain.EaseUser;

/**
 * User profile provider
 * @author wei
 *
 */
public interface EaseUserProfileProvider {
    /**
     * return EaseUser for input username
     * @param username
     * @return
     */
    EaseUser getUser(String username);

    /**
     * update user
     * @param user
     * @return
     */
    EaseUser getUser(EaseUser user);
}