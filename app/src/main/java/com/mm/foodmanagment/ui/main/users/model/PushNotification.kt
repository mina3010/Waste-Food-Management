package com.mm.foodmanagment.ui.main.users.model

import com.mm.foodmanagment.ui.main.users.model.NotificationData

data class PushNotification(
    var data: NotificationData,
    var to:String
)