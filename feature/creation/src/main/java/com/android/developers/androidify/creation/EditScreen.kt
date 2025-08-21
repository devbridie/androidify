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
@file:OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)

package com.android.developers.androidify.creation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.android.developers.androidify.data.DropBehaviourFactory
import com.android.developers.androidify.theme.components.AndroidifyTopAppBar
import com.android.developers.androidify.theme.components.PrimaryButton
import com.android.developers.androidify.theme.components.SecondaryOutlinedButton
import com.android.developers.androidify.theme.components.SquiggleBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.android.developers.androidify.creation.R as CreationR

@Composable
fun EditScreen(
    snackbarHostState: SnackbarHostState,
    dropBehaviourFactory: DropBehaviourFactory,
    isExpanded: Boolean,
    onCameraPressed: () -> Unit,
    onBackPressed: () -> Unit,
    onAboutPressed: () -> Unit,
    uiState: CreationState,
    onChooseImageClicked: (PickVisualMedia.VisualMediaType) -> Unit,
    onPromptOptionSelected: (PromptType) -> Unit,
    onUndoPressed: () -> Unit,
    onPromptGenerationPressed: () -> Unit,
    onBotColorSelected: (BotColor) -> Unit,
    onStartClicked: () -> Unit,
    onDropCallback: (Uri) -> Unit = {},
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData,
                        shape = SnackbarDefaults.shape,
                        modifier = Modifier.padding(4.dp),
                    )
                },
                modifier = Modifier.safeContentPadding(),
            )
        },
        topBar = {
            AndroidifyTopAppBar(
                backEnabled = true,
                isMediumWindowSize = isExpanded,
                aboutEnabled = true,
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
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { contentPadding ->
        SquiggleBackground(offsetHeightFraction = 0.5f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding(),
        ) {
            var showColorPickerBottomSheet by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                if (!isExpanded) {
                    PromptTypeToolbar(
                        uiState.selectedPromptOption,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        onOptionSelected = onPromptOptionSelected,
                    )
                }
                if (isExpanded) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                    ) {
                        MainCreationPane(
                            uiState,
                            dropBehaviourFactory = dropBehaviourFactory,
                            modifier = Modifier.weight(.6f),
                            onCameraPressed = onCameraPressed,
                            onChooseImageClicked = {
                                onChooseImageClicked(PickVisualMedia.ImageOnly)
                            },
                            onUndoPressed = onUndoPressed,
                            onPromptGenerationPressed = onPromptGenerationPressed,
                            onSelectedPromptOptionChanged = onPromptOptionSelected,
                            onDropCallback = onDropCallback,
                        )
                        Box(
                            modifier = Modifier
                                .weight(.4f)
                                .padding(top = 16.dp, bottom = 16.dp)
                                .fillMaxSize()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                    shape = MaterialTheme.shapes.large,
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outline,
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
                } else {
                    MainCreationPane(
                        uiState,
                        dropBehaviourFactory = dropBehaviourFactory,
                        modifier = Modifier.weight(1f),
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

                if (isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        TransformButton(
                            modifier = Modifier.padding(bottom = 8.dp),
                            buttonText = stringResource(CreationR.string.start_transformation_button),
                            onClicked = onStartClicked,
                        )
                    }
                } else {
                    BottomButtons(
                        onButtonColorClicked = {
                            showColorPickerBottomSheet = !showColorPickerBottomSheet
                        },
                        uiState = uiState,
                        onStartClicked = onStartClicked,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }

            BotColorPickerBottomSheet(
                showColorPickerBottomSheet,
                dismissBottomSheet = {
                    showColorPickerBottomSheet = false
                },
                onColorChanged = onBotColorSelected,
                listBotColors = uiState.listBotColors,
                selectedBotColor = uiState.botColor,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun MainCreationPane(
    uiState: CreationState,
    dropBehaviourFactory: DropBehaviourFactory,
    modifier: Modifier = Modifier,
    onCameraPressed: () -> Unit,
    onChooseImageClicked: () -> Unit = {},
    onUndoPressed: () -> Unit = {},
    onPromptGenerationPressed: () -> Unit,
    onSelectedPromptOptionChanged: (PromptType) -> Unit,
    onDropCallback: (Uri) -> Unit,
) {
    val defaultDropAreaBackgroundColor = MaterialTheme.colorScheme.surface
    val alternateDropAreaBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
    var background by remember { mutableStateOf(defaultDropAreaBackgroundColor) }

    val activity = LocalActivity.current as ComponentActivity
    val externalAppCallback = remember {
        dropBehaviourFactory.createTargetCallback(
            activity = activity,
            onImageDropped = { uri -> onDropCallback(uri) },
            onDropStarted = { background = alternateDropAreaBackgroundColor },
            onDropEnded = { background = defaultDropAreaBackgroundColor },
        )
    }

    Box(
        modifier = modifier,
    ) {
        val spatialSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
        val pagerState =
            rememberPagerState(uiState.selectedPromptOption.ordinal) { PromptType.entries.size }
        val focusManager = LocalFocusManager.current
        LaunchedEffect(uiState.selectedPromptOption) {
            launch {
                pagerState.animateScrollToPage(
                    uiState.selectedPromptOption.ordinal,
                    animationSpec = spatialSpec,
                )
            }.invokeOnCompletion {
                if (uiState.selectedPromptOption != PromptType.entries[pagerState.currentPage]) {
                    onSelectedPromptOptionChanged(PromptType.entries[pagerState.currentPage])
                }
            }
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onSelectedPromptOptionChanged(PromptType.entries[page])
            }
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.targetPage }.collect {
                if (pagerState.targetPage != PromptType.TEXT.ordinal) {
                    focusManager.clearFocus()
                }
            }
        }
        HorizontalPager(
            pagerState,
            modifier.fillMaxSize(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
        ) {
            when (it) {
                PromptType.PHOTO.ordinal -> {
                    val imageUri = uiState.imageUri
                    PhotoPrompt(
                        imageUri,
                        background,
                        dropBehaviourFactory,
                        externalAppCallback,
                        onCameraPressed,
                        onChooseImageClicked,
                        onUndoPressed,
                    )
                }

                PromptType.TEXT.ordinal -> {
                    TextPrompt(
                        textFieldState = uiState.descriptionText,
                        promptGenerationInProgress = uiState.promptGenerationInProgress,
                        generatedPrompt = uiState.generatedPrompt,
                        onPromptGenerationPressed = onPromptGenerationPressed,
                        modifier = Modifier
                            .fillMaxSize()
                            .heightIn(min = 200.dp)
                            .padding(2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.BottomButtons(
    onButtonColorClicked: () -> Unit,
    uiState: CreationState,
    onStartClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .wrapContentSize()
            .padding(8.dp)
            .align(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SecondaryOutlinedButton(
            onClick = {
                onButtonColorClicked()
            },
            buttonText = stringResource(CreationR.string.bot_color_button),
            modifier = Modifier.fillMaxRowHeight(),
            leadingIcon = {
                Row {
                    DisplayBotColor(
                        uiState.botColor,
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                CircleShape,
                            )
                            .size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            },
        )
        TransformButton(
            modifier = Modifier.fillMaxRowHeight(),
            onClicked = onStartClicked,
        )
    }
}

@Composable
private fun TransformButton(
    modifier: Modifier = Modifier,
    buttonText: String = stringResource(CreationR.string.transform_button),
    onClicked: () -> Unit = {},
) {
    PrimaryButton(
        modifier = modifier,
        onClick = onClicked,
        buttonText = buttonText,
        trailingIcon = {
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    ImageVector.vectorResource(com.android.developers.androidify.theme.R.drawable.rounded_arrow_forward_24),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun BotColorPickerBottomSheet(
    showColorPickerBottomSheet: Boolean,
    dismissBottomSheet: () -> Unit,
    onColorChanged: (BotColor) -> Unit,
    listBotColors: List<BotColor>,
    selectedBotColor: BotColor,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showColorPickerBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier,
            sheetState = sheetState,
            onDismissRequest = {
                dismissBottomSheet()
            },
        ) {
            val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier.padding(
                    start = 36.dp,
                    end = 36.dp,
                    top = 16.dp,
                    bottom = 8.dp,
                ),
            ) {
                AndroidBotColorPicker(
                    selectedBotColor,
                    onBotColorSelected = {
                        onColorChanged(it)
                        scope.launch {
                            delay(400)
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                dismissBottomSheet()
                            }
                        }
                    },
                    listBotColor = listBotColors,
                )
            }
        }
    }
}
