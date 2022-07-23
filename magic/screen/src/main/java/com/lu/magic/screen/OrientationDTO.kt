package com.lu.magic.screen

/**
 * Activity屏幕方向模板
 */
class OrientationDTO(var actList: List<ActItem>) {
    class ActItem(var actClass: String, var orientation: Int, var enable: Boolean)
}