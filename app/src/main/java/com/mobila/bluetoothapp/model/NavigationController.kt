package com.mobila.bluetoothapp.model

import androidx.navigation.NavController

object NavigationController {

    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        navController = controller
    }

    /*fun getNavController(): NavController {
        return navController
    }*/

    fun navigate(string: String) {
        navController?.navigate(string)
    }
}