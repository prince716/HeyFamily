package fanshawe.heyfamily;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Prince on 2018-03-11.
 */

class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NotificationsFragment notificationFragment = new NotificationsFragment();
                return notificationFragment;
            case 1:
                ChatFragment chatsFragment = new ChatFragment();
                return chatsFragment;
            case 2:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;
            case 3:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Notification";
            case 1:
                return "Chats";
            case 2:
                return "Groups";
            case 3:
                return "Find Friends";
            default:
                return null;
        }
    }


}