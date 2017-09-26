package com.qg.smartprinter.localorder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.test.suitebuilder.annotation.SmallTest;

import com.qg.common.logger.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class BTTest {
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Mock
    private BluetoothSocket mMockBTSocket;

    @Mock
    private BluetoothAdapter mMockBTAdapter;

    @Mock
    private InputStream mMockInputStream;

    @Mock
    private OutputStream mMockOutputStream;

    private static final String PRINTER_DEVICE_NAME = "Printer";
    private static final UUID TARGET_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Before
    public void setup() {
        initBTConnection();
    }

    @Before
    public void disableLogging() {
        Log.LOGGING_ENABLE = false;
    }

    @Test
    public void connectBT_Test() throws IOException {
        BluetoothSocket socket = null;
        Set<BluetoothDevice> bondedDevices = mMockBTAdapter.getBondedDevices();
        for (BluetoothDevice bondedDevice : bondedDevices) {
            if (bondedDevice.getName().equals(PRINTER_DEVICE_NAME)) {
                socket = bondedDevice.createRfcommSocketToServiceRecord(TARGET_UUID);
                break;
            }
        }
        System.out.println(socket.getInputStream());
    }

    private void initBTConnection() {
        mMockInputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        mMockOutputStream = new ByteArrayOutputStream();

        mMockBTSocket = createMockSocket();

        mMockBTAdapter = mock(BluetoothAdapter.class);

        Set<BluetoothDevice> mockDeviceSet = createMockDeviceSet();
        when(mMockBTAdapter.getBondedDevices()).thenReturn(mockDeviceSet);

    }

    private BluetoothSocket createMockSocket() {
        BluetoothSocket btSocket = Mockito.mock(BluetoothSocket.class);

        try {
            doNothing().when(btSocket).connect();
            when(btSocket.getInputStream()).thenReturn(mMockInputStream);
            when(btSocket.getOutputStream()).thenReturn(mMockOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return btSocket;
    }

    private Set<BluetoothDevice> createMockDeviceSet() {
        Set<BluetoothDevice> btDeviceSet = new LinkedHashSet<BluetoothDevice>();

        btDeviceSet.add(createMockDevice(PRINTER_DEVICE_NAME));
        btDeviceSet.add(createMockDevice("Not a real device"));

        return btDeviceSet;
    }

    private BluetoothDevice createMockDevice(String deviceName) {
        BluetoothDevice btDevice = Mockito.mock(BluetoothDevice.class);

        when(btDevice.getName()).thenReturn(deviceName);

        try {
            when(btDevice.createRfcommSocketToServiceRecord(TARGET_UUID)).thenReturn(mMockBTSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return btDevice;
    }
}
