/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.developers.androidify.xr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.SpatialCapabilities
import androidx.xr.scenecore.scene

/** Check if the device is XR-enabled, but is not yet rendering spatial UI. */
@Composable
fun SpatialCapabilities.couldRequestFullSpace(): Boolean {
    if (LocalSession.current == null) return false
    return !isSpatialUiEnabled
}

/** Default styling for an IconButton with a home space button and behavior. */
@Composable
fun RequestHomeSpaceIconButton(modifier: Modifier = Modifier) {
    val session = LocalSession.current ?: return

    IconButton(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shape = CircleShape,
        ),
        onClick = {
            session.scene.requestHomeSpaceMode()
        },
    ) {
        Icon(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp),
            imageVector = ImageVector.vectorResource(R.drawable.collapse_content_24px),
            contentDescription = "To Home Space Mode",
        )
    }
}

@Composable
fun FullSpaceIcon(modifier: Modifier = Modifier) {
    Icon(
        modifier = Modifier,
        imageVector = ImageVector.vectorResource(R.drawable.expand_content_24px),
        contentDescription = "To Full Space Mode",
    )
}
