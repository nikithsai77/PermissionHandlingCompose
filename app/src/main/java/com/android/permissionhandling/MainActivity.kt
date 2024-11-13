@file:OptIn(ExperimentalPermissionsApi::class)

package com.android.permissionhandling

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.permissionhandling.ui.theme.PermissionHandlingTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionHandlingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HandlingPermission()
                }
            }
        }
    }
}

@Composable
fun HandlingPermission() {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA)
    )
    val lifeCycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = true) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START)
                permissionState.launchMultiplePermissionRequest()
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        permissionState.permissions.forEach {
            when(it.permission) {
                Manifest.permission.CAMERA -> {
                    when {
                        it.hasPermission -> {
                            Text(text = "Camera Permission Granted")
                        }
                        it.shouldShowRationale -> {
                            Text(text = "Camera Permission is Needed To Access The Camera")
                        }
                        !it.hasPermission && !it.shouldShowRationale -> {
                            Text(text = "Camera permission was permanently denied. You can enable it in the app settings.")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PermissionHandlingTheme {
        HandlingPermission()
    }
}
