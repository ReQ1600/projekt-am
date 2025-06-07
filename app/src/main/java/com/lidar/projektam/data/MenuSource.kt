package com.lidar.projektam.data

import com.lidar.projektam.model.MenuItem
import com.lidar.projektam.R

class MenuSource {
    fun loadMenuItems() : List<MenuItem> {
        return listOf<MenuItem>(
            MenuItem(R.string.menu_exrates, R.drawable.tst, 0),
            MenuItem(R.string.menu_spendings, R.drawable.tst, 1),
            MenuItem(R.string.menu_charts, R.drawable.tst, 2),
            MenuItem(R.string.menu_spendings, R.drawable.tst, 3),
            MenuItem(R.string.menu_exrates, R.drawable.tst, 4),
            MenuItem(R.string.menu_spendings, R.drawable.tst, 5),
            MenuItem(R.string.menu_exrates, R.drawable.tst, 6),
            MenuItem(R.string.menu_spendings, R.drawable.tst, 7)
        )
    }
}