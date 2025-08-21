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

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.developers.androidify.customize.CustomizeAndExportScreen
import com.android.developers.androidify.customize.CustomizeExportViewModel
import com.android.developers.androidify.results.ResultsScreen
import com.android.developers.androidify.util.isAtLeastMedium

@Composable
fun CreationScreen(
    fileName: String? = null,
    creationViewModel: CreationViewModel = hiltViewModel(),
    isMedium: Boolean = isAtLeastMedium(),
    onCameraPressed: () -> Unit = {},
    onBackPressed: () -> Unit,
    onAboutPressed: () -> Unit,
) {
    val uiState by creationViewModel.uiState.collectAsStateWithLifecycle()
    BackHandler(
        enabled = uiState.screenState != ScreenState.EDIT,
    ) {
        creationViewModel.onBackPress()
    }
    LaunchedEffect(Unit) {
        if (fileName != null) {
            creationViewModel.onImageSelected(fileName.toUri())
        } else {
            creationViewModel.onImageSelected(null)
        }
    }
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            creationViewModel.onImageSelected(uri)
        }
    }
    val snackbarHostState by creationViewModel.snackbarHostState.collectAsStateWithLifecycle()
    when (uiState.screenState) {
        ScreenState.EDIT -> {
            EditScreen(
                snackbarHostState = snackbarHostState,
                dropBehaviourFactory = creationViewModel.dropBehaviourFactory,
                isExpanded = isMedium,
                onCameraPressed = onCameraPressed,
                onBackPressed = onBackPressed,
                onAboutPressed = onAboutPressed,
                uiState = uiState,
                onChooseImageClicked = { pickMedia.launch(PickVisualMediaRequest(it)) },
                onPromptOptionSelected = creationViewModel::onSelectedPromptOptionChanged,
                onUndoPressed = creationViewModel::onUndoPressed,
                onPromptGenerationPressed = creationViewModel::onPromptGenerationClicked,
                onBotColorSelected = creationViewModel::onBotColorChanged,
                onStartClicked = creationViewModel::startClicked,
                onDropCallback = creationViewModel::onImageSelected,
            )
        }

        ScreenState.LOADING -> {
            LoadingScreen(
                onCancelPress = {
                    creationViewModel.cancelInProgressTask()
                },
            )
        }

        ScreenState.RESULT -> {
            val prompt = uiState.descriptionText.text.toString()
            val key = if (uiState.descriptionText.text.isBlank()) {
                uiState.imageUri.toString()
            } else {
                prompt
            }
            ResultsScreen(
                uiState.resultBitmap!!,
                if (uiState.selectedPromptOption == PromptType.PHOTO) {
                    uiState.imageUri
                } else {
                    null
                },
                promptText = prompt,
                viewModel = hiltViewModel(key = key),
                onAboutPress = onAboutPressed,
                onBackPress = onBackPressed,
                onNextPress = creationViewModel::customizeExportClicked,
            )
        }

        ScreenState.CUSTOMIZE -> {
            val prompt = uiState.descriptionText.text.toString()
            val key = if (uiState.descriptionText.text.isBlank()) {
                uiState.imageUri.toString()
            } else {
                prompt
            }
            uiState.resultBitmap?.let { bitmap ->
                CustomizeAndExportScreen(
                    resultImage = bitmap,
                    originalImageUri = uiState.imageUri,
                    onBackPress = onBackPressed,
                    onInfoPress = onAboutPressed,
                    viewModel = hiltViewModel<CustomizeExportViewModel>(key = key),
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun PromptTypeToolbar(
    selectedOption: PromptType,
    modifier: Modifier = Modifier,
    onOptionSelected: (PromptType) -> Unit,
) {
    val options = PromptType.entries
    HorizontalFloatingToolbar(
        modifier = modifier.border(
            2.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.large,
        ),
        colors = FloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.surface,
            toolbarContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            fabContainerColor = MaterialTheme.colorScheme.tertiary,
            fabContentColor = MaterialTheme.colorScheme.onTertiary,
        ),
        expanded = true,
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                modifier = Modifier,
                checked = selectedOption == label,
                onCheckedChange = { onOptionSelected(label) },
                shapes = ToggleButtonDefaults.shapes(checkedShape = MaterialTheme.shapes.large),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Text(label.displayName, maxLines = 1)
            }
            if (index != options.size - 1) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
