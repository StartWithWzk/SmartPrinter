/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License")";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qg.smartprinter.localorder;

/**
 * Defines several constants used between {@link PrinterService} and the UI.
 */
public interface Constants {

    // Message types sent from the BluetoothPrStringerService Handler
    String MESSAGE_STATE_CHANGE = "1";
    String MESSAGE_READ = "2";
    String MESSAGE_DEVICE_NAME = "4";
    String MESSAGE_ERR = "MESSAGE_ERR";

    String MESSAGE_CONN_FAIL = "MESSAGE_CONN_FAIL";
    String MESSAGE_CONN_LOST = "MESSAGE_CONN_LOST";

    String MSG_ORDER_TCP = "MSG_ORDER_TCP";
    String MSG_ORDER_BT = "MSG_ORDER_BT";
    String MSG_CONNECT_TCP = "MSG_CONNECT_TCP";
    String MSG_CONNECT_BT = "MSG_CONNECT_BT";
    String MSG_DISCONNECT_TCP = "MSG_DISCONNECT_TCP";
    String MSG_DISCONNECT_BT = "MSG_DISCONNECT_BT";

    String OBJECT = "obj"; // obj in message

}
