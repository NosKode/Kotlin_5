package com.example.pr_5_2

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    viewModel: PhotoGalleryViewModel = viewModel()
) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentPhotoFile by remember { mutableStateOf<File?>(null) }
    var selectedPhoto by remember { mutableStateOf<PhotoItem?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var shouldLaunchCamera by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.loadPhotos()
        }
        currentPhotoFile = null
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldLaunchCamera = true
        }
    }

    // Launch camera when flag is set
    LaunchedEffect(shouldLaunchCamera) {
        if (shouldLaunchCamera) {
            try {
                val photoFile = viewModel.createImageFile()
                currentPhotoFile = photoFile
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                Toast.makeText(context, "Запуск камеры...", Toast.LENGTH_SHORT).show()
                cameraLauncher.launch(photoUri)
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                shouldLaunchCamera = false
            }
        }
    }

    // Storage permission launcher (for Android 10 and below)
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectedPhoto?.let { viewModel.exportToGallery(it) }
        }
    }

    // Handle snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    // Export dialog
    if (showExportDialog && selectedPhoto != null) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Экспорт фото") },
            text = { Text("Добавить это фото в общую галерею устройства?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPhoto?.let { photo ->
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                // Request WRITE_EXTERNAL_STORAGE for Android 9 and below
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    viewModel.exportToGallery(photo)
                                } else {
                                    storagePermissionLauncher.launch(
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                }
                            } else {
                                // No permission needed for Android 10+
                                viewModel.exportToGallery(photo)
                            }
                        }
                        showExportDialog = false
                    }
                ) {
                    Text("Экспортировать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) -> {
                            // Permission already granted, launch camera
                            Toast.makeText(context, "Разрешение есть, открываем камеру", Toast.LENGTH_SHORT).show()
                            shouldLaunchCamera = true
                        }
                        else -> {
                            // Request permission
                            Toast.makeText(context, "Запрос разрешения", Toast.LENGTH_SHORT).show()
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Сделать фото"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (photos.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "У вас пока нет фото",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) -> {
                                    shouldLaunchCamera = true
                                }
                                else -> {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Сделать первое фото")
                    }
                }
            } else {
                // Photo grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(photos) { photo ->
                        PhotoGridItem(
                            photo = photo,
                            onExportClick = {
                                selectedPhoto = photo
                                showExportDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGridItem(
    photo: PhotoItem,
    onExportClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { showMenu = true }
    ) {
        AsyncImage(
            model = photo.file,
            contentDescription = photo.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Экспорт в галерею") },
                onClick = {
                    showMenu = false
                    onExportClick()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
