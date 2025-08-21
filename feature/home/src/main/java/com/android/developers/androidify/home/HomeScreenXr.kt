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
package com.android.developers.androidify.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.xr.compose.spatial.ApplicationSubspace
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SpatialAlignment
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.aspectRatio
import androidx.xr.compose.subspace.layout.fillMaxHeight
import androidx.xr.compose.subspace.layout.fillMaxWidth
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.rotate
import com.android.developers.androidify.theme.components.AndroidifyTopAppBar
import com.android.developers.androidify.theme.components.FullSquiggleBackground
import com.android.developers.androidify.xr.RequestHomeSpaceIconButton

@Composable
fun HomeScreenContentsXr(
    videoLink: String?,
    dancingBotLink: String?,
    onClickLetsGo: (IntOffset) -> Unit,
    onAboutClicked: () -> Unit,
) {
    var positionButtonClick by remember {
        mutableStateOf(IntOffset.Zero)
    }
    ApplicationSubspace {
        SpatialPanel(
            SubspaceModifier
                .movable()
                .resizable()
                .fillMaxWidth(1f)
                .aspectRatio(1.7f),
        ) {
            Orbiter(
                position = ContentEdge.Top,
                alignment = Alignment.End
            ) {
                RequestHomeSpaceIconButton(
                    modifier = Modifier
                        .size(64.dp, 64.dp)
                        .padding(8.dp)
                )
            }
            Box {
                FullSquiggleBackground()
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight(1f)
                        .align(Alignment.Center),
                ) {
                    MainHomeContent(
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .align(Alignment.Center),
                        dancingBotLink = dancingBotLink,
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        AndroidifyTopAppBar(
                            modifier = Modifier
                                .width(220.dp)
                                .padding(bottom = 16.dp),
                            aboutEnabled = true,
                            onAboutClicked = onAboutClicked,
                        )
                        HomePageButton(
                            modifier = Modifier
                                .onLayoutRectChanged {
                                    positionButtonClick = it.boundsInWindow.center
                                }
                                .height(64.dp)
                                .width(220.dp),
                            onClick = {
                                onClickLetsGo(positionButtonClick)
                            },
                        )
                    }
                }
            }
            Subspace {
                SpatialPanel(
                    SubspaceModifier
                        .fillMaxWidth(0.2f)
                        .fillMaxHeight(0.8f)
                        .aspectRatio(0.77f)
                        .resizable(maintainAspectRatio = true)
                        .align(SpatialAlignment.CenterRight)
                        .offset(z = 10.dp)
                        .rotate(0f, 0f, 5f),
                ) {
                    VideoPlayer(videoLink)
                }
            }
        }
    }
}
