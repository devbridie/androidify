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
package com.android.developers.androidify.creation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialColumn
import androidx.xr.compose.subspace.SpatialLayoutSpacer
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.SubspaceComposable
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.aspectRatio
import androidx.xr.compose.subspace.layout.fillMaxHeight
import androidx.xr.compose.subspace.layout.fillMaxSize
import androidx.xr.compose.subspace.layout.fillMaxWidth
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.rotate
import androidx.xr.compose.subspace.layout.width
import com.android.developers.androidify.data.DropBehaviourFactory
import com.android.developers.androidify.theme.components.AndroidifyTopAppBar
import com.android.developers.androidify.theme.components.FullSquiggleBackground
import com.android.developers.androidify.theme.components.VideoPlayer
import com.android.developers.androidify.xr.MainPanelWorkaround
import com.android.developers.androidify.xr.RequestHomeSpaceIconButton
import com.android.developers.androidify.creation.R as CreationR

@Composable
fun EditScreenXr(
    snackbarHostState: SnackbarHostState,
    dropBehaviourFactory: DropBehaviourFactory,
    isExpanded: Boolean,
    onCameraPressed: () -> Unit,
    onBackPressed: () -> Unit,
    onAboutPressed: () -> Unit,
    uiState: CreationState,
    onChooseImageClicked: (ActivityResultContracts.PickVisualMedia.VisualMediaType) -> Unit,
    onPromptOptionSelected: (PromptType) -> Unit,
    onUndoPressed: () -> Unit,
    onPromptGenerationPressed: () -> Unit,
    onBotColorSelected: (BotColor) -> Unit,
    onStartClicked: () -> Unit,
    onDropCallback: (Uri) -> Unit = {},
    creationViewModel: CreationViewModel = hiltViewModel(),
) {
    MainPanelWorkaround()
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            creationViewModel.onImageSelected(uri)
        }
    }

    Subspace {
        SpatialPanel(
            SubspaceModifier
                .movable()
                .aspectRatio(2f)
                .fillMaxWidth(0.6f),
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
                Subspace {
                    SpatialColumn(SubspaceModifier.fillMaxSize(1f)) {
                        SpatialPanel(
                            SubspaceModifier
                                .offset(z = 10.dp)
                                .movable()
                                .fillMaxWidth(0.4f),
                        ) {
                            AndroidifyTopAppBar(
                                backEnabled = true,
                                isMediumWindowSize = isExpanded,
                                aboutEnabled = true,
                                isXr = true,
                                onBackPressed = onBackPressed,
                                onAboutClicked = onAboutPressed,
                                expandedCenterButtons = {
                                    PromptTypeToolbar(
                                        uiState.selectedPromptOption,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                                        onOptionSelected = onPromptOptionSelected,
                                    )
                                },
                            )
                        }
                        SpatialRow {
                            XrAndroidBotColorPickerPanel(
                                uiState = uiState,
                                onBotColorSelected = onBotColorSelected,
                            )
                            SpatialLayoutSpacer(SubspaceModifier.width(50.dp))
                            XrMainCreationPanel(
                                modifier = SubspaceModifier
                                    .offset(z = 10.dp)
                                    .fillMaxWidth(0.4f)
                                    .fillMaxHeight(0.8f),
                                uiState = uiState,
                                dropBehaviourFactory = dropBehaviourFactory,
                                onCameraPressed = onCameraPressed,
                                onChooseImageClicked = onChooseImageClicked,
                                onUndoPressed = onUndoPressed,
                                onPromptGenerationPressed = onPromptGenerationPressed,
                                onPromptOptionSelected = onPromptOptionSelected,
                                onDropCallback = onDropCallback,
                            )
                            SpatialLayoutSpacer(SubspaceModifier.width(50.dp))
                            XrExplanationPanel(
                                SubspaceModifier
                                    .fillMaxWidth(0.5f)
                                    .fillMaxHeight(0.8f)
                                    .aspectRatio(0.77f)
                                    .resizable(maintainAspectRatio = true)
                                    .offset(z = 10.dp)
                                    .rotate(0f, 0f, 5f),
                                videoLink = uiState.videoLink,
                            )
                            Orbiter(position = ContentEdge.Bottom, offset = 16.dp) {
                                TransformButton(
                                    buttonText = stringResource(CreationR.string.start_transformation_button),
                                    onClicked = onStartClicked,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@SubspaceComposable
private fun XrMainCreationPanel(
    modifier: SubspaceModifier,
    uiState: CreationState,
    dropBehaviourFactory: DropBehaviourFactory,
    onCameraPressed: () -> Unit,
    onChooseImageClicked: (PickVisualMedia.VisualMediaType) -> Unit,
    onUndoPressed: () -> Unit,
    onPromptGenerationPressed: () -> Unit,
    onPromptOptionSelected: (PromptType) -> Unit,
    onDropCallback: (Uri) -> Unit,
) {
    SpatialPanel(modifier) {
        Box(
            Modifier.background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.large,
            ),
        ) {
            MainCreationPane(
                uiState,
                dropBehaviourFactory = dropBehaviourFactory,
                onCameraPressed = onCameraPressed,
                onChooseImageClicked = {
                    onChooseImageClicked(PickVisualMedia.ImageOnly)
                },
                onUndoPressed = onUndoPressed,
                onPromptGenerationPressed = onPromptGenerationPressed,
                onSelectedPromptOptionChanged = onPromptOptionSelected,
                onDropCallback = onDropCallback,
            )
        }
    }
}

@Composable
private fun XrAndroidBotColorPickerPanel(
    uiState: CreationState,
    onBotColorSelected: (BotColor) -> Unit,
) {
    SpatialPanel(
        SubspaceModifier
            .offset(z = 10.dp)
            .fillMaxWidth(0.2f)
            .fillMaxHeight(0.7f),
    ) {
        Box(
            Modifier.background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.large,
            ),
        ) {
            AndroidBotColorPicker(
                selectedBotColor = uiState.botColor,
                modifier = Modifier.padding(16.dp),
                onBotColorSelected = onBotColorSelected,
                listBotColor = uiState.listBotColors,
            )
        }
    }
}

@Composable
@SubspaceComposable
private fun XrExplanationPanel(
    modifier: SubspaceModifier = SubspaceModifier,
    videoLink: String?,
) {
    SpatialPanel(
        modifier,
    ) {
        VideoPlayer(videoLink)
    }
}
