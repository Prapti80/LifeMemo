package com.prapti.lifememo

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.annotation.ExperimentalCoilApi
import com.prapti.lifememo.database.JournalEntry
import com.prapti.lifememo.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun AddEntryDialog(onDismiss: () -> Unit, onSave: (String, String, Long, String?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(Color.Yellow.toArgb().toLong()) }
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri.toString()
        }
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        Color(0xFFFFCCCB),
                        Color(0xFFFEFFCC),
                        Color(0xFFD5CCFF),
                        Color(0xFFCCFFFF),
                        Color(0xFFFFCCFF)
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color)
                                .clickable {
                                    selectedColor = color
                                        .toArgb()
                                        .toLong()
                                }
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Enter Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Enter Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Select Image")
                }
                selectedImageUri?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty() && content.isNotEmpty()) {
                            onSave(title, content, selectedColor, selectedImageUri)
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun JournalEntryCard(entry: JournalEntry, onDelete: (JournalEntry) -> Unit, onEdit: (JournalEntry) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEdit(entry) },
        colors = CardDefaults.cardColors(containerColor = Color(entry.color))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = entry.title, style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { onDelete(entry) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Entry")
                }
            }
            Text(text = entry.dateTime, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalApp(viewModel: JournalViewModel = viewModel()) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showDetail by remember { mutableStateOf(false) }
    var currentEntry by remember { mutableStateOf<JournalEntry?>(null) }
    val searchQuery by viewModel.searchQuery
    val entries by remember { derivedStateOf { viewModel.filterEntries(searchQuery) } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("Search by title or date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(entries) { entry ->
                    JournalEntryCard(entry = entry, onDelete = {
                        viewModel.deleteEntry(it)
                    }, onEdit = {
                        currentEntry = it
                        showDetail = true
                    })
                }
            }
        }
    }

    if (showDialog) {
        AddEntryDialog(onDismiss = { showDialog = false }, onSave = { title, content, color, imageUri ->
            viewModel.addEntry(title, content, color, imageUri)
        })
    }

    if (showDetail && currentEntry != null) {
        DetailEntryDialog(
            entry = currentEntry!!,
            onDismiss = { showDetail = false },
            onSave = { updatedEntry ->
                viewModel.updateEntry(updatedEntry)
                showDetail = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun DetailEntryDialog(entry: JournalEntry, onDismiss: () -> Unit, onSave: (JournalEntry) -> Unit) {
    var title by remember { mutableStateOf(entry.title) }
    var content by remember { mutableStateOf(entry.content) }
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(entry.imageUris) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri.toString()
        }
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Edit Entry", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Select Image")
                }
                selectedImageUri?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty() && content.isNotEmpty()) {
                            val updatedEntry = entry.copy(title = title, content = content, imageUris = selectedImageUri)
                            onSave(updatedEntry)
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
